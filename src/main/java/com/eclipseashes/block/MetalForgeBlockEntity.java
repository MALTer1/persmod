package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MetalForgeBlockEntity extends BlockEntity implements Container, MenuProvider {

    public static final int INVENTORY_SIZE = 9;
    public static final int MAX_LAVA = 10_000;

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    private int lavaAmount = 0;

    public MetalForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.METAL_FORGE, pos, state);
    }

    // -----------------------------
    // Inventory
    // -----------------------------

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
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);

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
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }

        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    // -----------------------------
    // Lava
    // -----------------------------

    public int getLavaAmount() {
        return lavaAmount;
    }

    public int getMaxLava() {
        return MAX_LAVA;
    }

    public void setLavaAmount(int amount) {
        lavaAmount = Math.max(0, Math.min(MAX_LAVA, amount));
        setChanged();
    }

    public void addLava(int amount) {
        setLavaAmount(lavaAmount + amount);
    }

    // -----------------------------
    // MenuProvider
    // -----------------------------

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

    // -----------------------------
    // Saving / Loading
    // -----------------------------

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(input, items);
        lavaAmount = input.getIntOr("lava", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        output.putInt("lava", lavaAmount);

        super.saveAdditional(output);
    }
}