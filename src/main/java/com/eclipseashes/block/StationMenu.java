package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class StationMenu extends AbstractContainerMenu {

    protected StationMenu(
            net.minecraft.world.inventory.MenuType<?> type,
            int containerId
    ) {
        super(type, containerId);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    protected static boolean valid(Inventory inventory) {
        return inventory.player.isAlive();
    }
}