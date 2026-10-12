package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MetalForgeBlockEntity
        extends BlockEntity
        implements Container, MenuProvider {

    public static final int MATERIAL_SLOTS = 9;

    public static final int LAVA_INPUT_SLOT = 9;
    public static final int LAVA_OUTPUT_SLOT = 10;
    public static final int PROCESS_OUTPUT_SLOT = 11;

    public static final int INVENTORY_SIZE = 12;

    public static final int LAVA_PER_BUCKET = 1000;
    public static final int MAX_LAVA = 10_000;

    public static final int PROCESS_TIME = 200;

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    private int lavaAmount = 0;
    private int processingProgress = 0;

    public MetalForgeBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(ModBlockEntities.METAL_FORGE, pos, state);
    }

    // =========================================================
    // INVENTORY
    // =========================================================

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(
            int slot,
            int amount
    ) {
        ItemStack result =
                ContainerHelper.removeItem(items, slot, amount);

        if (!result.isEmpty()) {
            setChanged();
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = items.get(slot);

        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        items.set(slot, ItemStack.EMPTY);

        return result;
    }

    @Override
    public void setItem(
            int slot,
            ItemStack stack
    ) {

        // Lava bucket input
        if (slot == LAVA_INPUT_SLOT && stack.is(Items.LAVA_BUCKET)) {
            // Never leave a lava bucket stuck in the input slot when the tank
            // or empty-bucket output is full.
            if (!canAcceptLavaBucket()) {
                return;
            }

            items.set(LAVA_INPUT_SLOT, ItemStack.EMPTY);
            lavaAmount += LAVA_PER_BUCKET;

            ItemStack output = items.get(LAVA_OUTPUT_SLOT);
            if (output.isEmpty()) {
                items.set(LAVA_OUTPUT_SLOT, new ItemStack(Items.BUCKET));
            } else {
                output.grow(1);
            }

            setChanged();
            return;
        }

        // Never allow manual insertion into outputs.
        if (slot == LAVA_OUTPUT_SLOT
                || slot == PROCESS_OUTPUT_SLOT) {
            return;
        }

        items.set(slot, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    @Override
    public boolean canPlaceItem(
            int slot,
            ItemStack stack
    ) {

        if (slot == LAVA_OUTPUT_SLOT
                || slot == PROCESS_OUTPUT_SLOT) {
            return false;
        }

        if (slot == LAVA_INPUT_SLOT) {
            return stack.is(Items.LAVA_BUCKET)
                    && canAcceptLavaBucket();
        }

        return true;
    }

    public boolean canAcceptLavaBucket() {

        if (lavaAmount + LAVA_PER_BUCKET > MAX_LAVA) {
            return false;
        }

        ItemStack output =
                items.get(LAVA_OUTPUT_SLOT);

        return output.isEmpty()
                || (
                output.is(Items.BUCKET)
                        && output.getCount()
                        < output.getMaxStackSize()
        );
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(
                this,
                player
        );
    }

    @Override
    public void clearContent() {
        items.clear();
        processingProgress = 0;
        setChanged();
    }

    // =========================================================
    // LAVA
    // =========================================================

    public int getLavaAmount() {
        return lavaAmount;
    }

    public int getMaxLava() {
        return MAX_LAVA;
    }

    public void setLavaAmount(int amount) {
        lavaAmount =
                Math.max(
                        0,
                        Math.min(MAX_LAVA, amount)
                );

        setChanged();
    }

    public void addLava(int amount) {
        setLavaAmount(
                lavaAmount + amount
        );
    }

    // =========================================================
    // PROCESSING
    // =========================================================

    public int getProcessingProgress() {
        return processingProgress;
    }

    public int getProcessingTime() {
        return PROCESS_TIME;
    }

    private Item getResult(Item item) {

        // Vanilla metal ores and raw metals are processed in the modded forge.
        if (item == Items.RAW_IRON || item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE) return Items.IRON_INGOT;
        if (item == Items.RAW_COPPER || item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE) return Items.COPPER_INGOT;
        if (item == Items.RAW_GOLD || item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE || item == Items.NETHER_GOLD_ORE) return Items.GOLD_INGOT;

        // Finished vanilla ingots/gems become material-tagged hot metal for casting.
        if (item == Items.IRON_INGOT || item == Items.COPPER_INGOT || item == Items.GOLD_INGOT
                || item == Items.NETHERITE_INGOT || item == Items.DIAMOND) return ModItem.HEATED_METAL;

        // First crude forge stage: raw material -> unrefined material.
        if (item == ModItem.RAW_CRIMSON_IRON) {
            return ModItem.UNREFINED_CRIMSON_IRON;
        }

        if (item == ModItem.RAW_MOONSTEEL) {
            return ModItem.UNREFINED_MOONSTEEL;
        }

        if (item == ModItem.RAW_SUNSTEEL) {
            return ModItem.UNREFINED_SUNSTEEL;
        }

        if (item == ModItem.UNREFINED_CRIMSON_IRON) {
            return ModItem.REFINED_CRIMSON_IRON;
        }

        if (item == ModItem.UNREFINED_MOONSTEEL) {
            return ModItem.REFINED_MOONSTEEL;
        }

        if (item == ModItem.UNREFINED_SUNSTEEL) {
            return ModItem.REFINED_SUNSTEEL;
        }

        // Finished modded ingots become material-tagged hot metal for casting.
        if (item == ModItem.CRIMSON_IRON || item == ModItem.MOONSTEEL || item == ModItem.SUNSTEEL
                || item == ModItem.DRAGONITE || item == ModItem.VOID_CRYSTAL
                || item == ModItem.CELESTIAL_ALLOY || item == ModItem.ECLIPSE) return ModItem.HEATED_METAL;

        // Refined material becomes the finished ingot.
        if (item == ModItem.REFINED_CRIMSON_IRON) {
            return ModItem.CRIMSON_IRON;
        }

        if (item == ModItem.REFINED_MOONSTEEL) {
            return ModItem.MOONSTEEL;
        }

        if (item == ModItem.REFINED_SUNSTEEL) {
            return ModItem.SUNSTEEL;
        }

        if (item == ModItem.RAW_DRAGONITE) return ModItem.UNREFINED_DRAGONITE;
        if (item == ModItem.RAW_VOID_CRYSTAL) return ModItem.UNREFINED_VOID_CRYSTAL;
        if (item == ModItem.RAW_CELESTIAL_ALLOY) return ModItem.UNREFINED_CELESTIAL_ALLOY;
        if (item == ModItem.RAW_ECLIPSE) return ModItem.UNREFINED_ECLIPSE;

        if (item == ModItem.UNREFINED_DRAGONITE) return ModItem.REFINED_DRAGONITE;
        if (item == ModItem.UNREFINED_VOID_CRYSTAL) return ModItem.REFINED_VOID_CRYSTAL;
        if (item == ModItem.UNREFINED_CELESTIAL_ALLOY) return ModItem.REFINED_CELESTIAL_ALLOY;
        if (item == ModItem.UNREFINED_ECLIPSE) return ModItem.REFINED_ECLIPSE;

        if (item == ModItem.REFINED_DRAGONITE) return ModItem.DRAGONITE;
        if (item == ModItem.REFINED_VOID_CRYSTAL) return ModItem.VOID_CRYSTAL;
        if (item == ModItem.REFINED_CELESTIAL_ALLOY) return ModItem.CELESTIAL_ALLOY;
        if (item == ModItem.REFINED_ECLIPSE) return ModItem.ECLIPSE;

        return null;
    }

    private int findProcessableSlot() {

        for (int i = 0; i < MATERIAL_SLOTS; i++) {

            ItemStack stack = items.get(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (getResult(stack.getItem()) != null) {
                return i;
            }
        }

        return -1;
    }

    private static String materialName(ItemStack stack) {
        String name = stack.getHoverName().getString();
        if (name.endsWith(" Ingot")) {
            return name.substring(0, name.length() - " Ingot".length());
        }
        return name;
    }

    private boolean canOutput(Item result, ItemStack input) {
        ItemStack output = items.get(PROCESS_OUTPUT_SLOT);
        if (output.isEmpty()) return true;
        if (!output.is(result) || output.getCount() >= output.getMaxStackSize()) return false;
        if (result == ModItem.HEATED_METAL) {
            String incomingMaterial = materialName(input);
            CompoundTag outputData = output.get(DataComponents.CUSTOM_DATA) == null
                    ? new CompoundTag()
                    : output.get(DataComponents.CUSTOM_DATA).copyTag();
            return outputData.getString("material").orElse("").equals(incomingMaterial);
        }
        return true;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            MetalForgeBlockEntity forge
    ) {

        if (level.isClientSide()) {
            return;
        }

        int inputSlot =
                forge.findProcessableSlot();

        // Nothing to process.
        if (inputSlot < 0) {
            forge.processingProgress = 0;
            return;
        }

        ItemStack input =
                forge.items.get(inputSlot);

        Item result =
                forge.getResult(input.getItem());

        if (result == null) {
            forge.processingProgress = 0;
            return;
        }

        // Need lava before processing can happen.
        if (forge.lavaAmount < LAVA_PER_BUCKET) {
            return;
        }

        // Don't start if output cannot accept the result.
        if (!forge.canOutput(result, input)) {
            return;
        }

        forge.processingProgress++;

        if (forge.processingProgress >= PROCESS_TIME) {

            forge.processingProgress = 0;

            // Capture the material name before shrinking the input stack.
            String processedMaterial = materialName(input);

            // Consume one material.
            input.shrink(1);

            // Consume lava.
            forge.lavaAmount -= LAVA_PER_BUCKET;

            // Add result.
            ItemStack output =
                    forge.items.get(PROCESS_OUTPUT_SLOT);

            if (output.isEmpty()) {
                ItemStack resultStack = new ItemStack(result);
                if (result == ModItem.HEATED_METAL) {
                    String material = processedMaterial;
                    CompoundTag data = new CompoundTag();
                    data.putString("material", material);
                    CustomData.set(DataComponents.CUSTOM_DATA, resultStack, data);
                    resultStack.set(DataComponents.CUSTOM_NAME,
                            Component.literal("Heated " + material));
                }
                forge.items.set(PROCESS_OUTPUT_SLOT, resultStack);
            } else {
                output.grow(1);
            }

            forge.setChanged();
        }
    }

    // =========================================================
    // MENU
    // =========================================================

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "block.eclipseashes.metal_forge"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
    ) {
        return new MetalForgeMenu(
                containerId,
                inventory,
                this
        );
    }

    // =========================================================
    // SAVING
    // =========================================================

    @Override
    protected void loadAdditional(
            ValueInput input
    ) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(
                input,
                items
        );

        lavaAmount =
                input.getIntOr(
                        "lava",
                        0
                );

        processingProgress =
                input.getIntOr(
                        "processing_progress",
                        0
                );
    }

    @Override
    protected void saveAdditional(
            ValueOutput output
    ) {

        ContainerHelper.saveAllItems(
                output,
                items
        );

        output.putInt(
                "lava",
                lavaAmount
        );

        output.putInt(
                "processing_progress",
                processingProgress
        );

        super.saveAdditional(output);
    }
}