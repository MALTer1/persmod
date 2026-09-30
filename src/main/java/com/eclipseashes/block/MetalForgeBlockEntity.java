package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.BlockPos;
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
        if (slot == LAVA_INPUT_SLOT
                && stack.is(Items.LAVA_BUCKET)) {

            if (canAcceptLavaBucket()) {

                items.set(
                        LAVA_INPUT_SLOT,
                        ItemStack.EMPTY
                );

                lavaAmount += LAVA_PER_BUCKET;

                ItemStack output =
                        items.get(LAVA_OUTPUT_SLOT);

                if (output.isEmpty()) {
                    items.set(
                            LAVA_OUTPUT_SLOT,
                            new ItemStack(Items.BUCKET)
                    );
                } else {
                    output.grow(1);
                }

                setChanged();
                return;
            }
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

        if (item == ModItem.RAW_CRIMSON_IRON) {
            return ModItem.CRIMSON_IRON;
        }

        if (item == ModItem.RAW_MOONSTEEL) {
            return ModItem.MOONSTEEL;
        }

        if (item == ModItem.RAW_SUNSTEEL) {
            return ModItem.SUNSTEEL;
        }

        if (item == ModItem.RAW_DRAGONITE) {
            return ModItem.DRAGONITE;
        }

        if (item == ModItem.RAW_VOID_CRYSTAL) {
            return ModItem.VOID_CRYSTAL;
        }

        if (item == ModItem.RAW_CELESTIAL_ALLOY) {
            return ModItem.CELESTIAL_ALLOY;
        }

        if (item == ModItem.RAW_ECLIPSE) {
            return ModItem.ECLIPSE;
        }

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

    private boolean canOutput(Item result) {

        ItemStack output =
                items.get(PROCESS_OUTPUT_SLOT);

        if (output.isEmpty()) {
            return true;
        }

        return output.is(result)
                && output.getCount()
                < output.getMaxStackSize();
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
        if (!forge.canOutput(result)) {
            return;
        }

        forge.processingProgress++;

        if (forge.processingProgress >= PROCESS_TIME) {

            forge.processingProgress = 0;

            // Consume one material.
            input.shrink(1);

            // Consume lava.
            forge.lavaAmount -= LAVA_PER_BUCKET;

            // Add result.
            ItemStack output =
                    forge.items.get(PROCESS_OUTPUT_SLOT);

            if (output.isEmpty()) {

                forge.items.set(
                        PROCESS_OUTPUT_SLOT,
                        new ItemStack(result)
                );

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