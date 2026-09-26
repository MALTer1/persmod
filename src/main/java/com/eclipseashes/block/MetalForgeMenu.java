package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MetalForgeMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SLOTS = 9;
    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END = CONTAINER_START + CONTAINER_SLOTS;

    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END =
            INVENTORY_START + Inventory.INVENTORY_SIZE;

    private final net.minecraft.world.Container container;

    // Client-side constructor
    public MetalForgeMenu(
            int containerId,
            Inventory inventory
    ) {
        this(
                containerId,
                inventory,
                new net.minecraft.world.SimpleContainer(CONTAINER_SLOTS)
        );
    }

    // Server-side constructor
    public MetalForgeMenu(
            int containerId,
            Inventory inventory,
            net.minecraft.world.Container container
    ) {
        super(ModMenuTypes.METAL_FORGE, containerId);

        checkContainerSize(container, CONTAINER_SLOTS);

        this.container = container;

        container.startOpen(inventory.player);

        // Forge inventory: 3x3
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {

                int slot = column + row * 3;

                addSlot(
                        new Slot(
                                container,
                                slot,
                                62 + column * 18,
                                17 + row * 18
                        )
                );
            }
        }

        // Player inventory
        addStandardInventorySlots(
                inventory,
                8,
                84
        );
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int slotIndex
    ) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();

        if (slotIndex < CONTAINER_END) {

            if (!moveItemStackTo(
                    stack,
                    INVENTORY_START,
                    INVENTORY_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

        } else {

            if (!moveItemStackTo(
                    stack,
                    CONTAINER_START,
                    CONTAINER_END,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}