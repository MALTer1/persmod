package com.eclipseashes.block;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MetalForgeMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SLOTS =
            MetalForgeBlockEntity.INVENTORY_SIZE;
    
    public static final int PROCESS_OUTPUT_SLOT =
            MetalForgeBlockEntity.PROCESS_OUTPUT_SLOT;

        private final DataSlot progressData =
            DataSlot.standalone();

    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END =
            CONTAINER_START + CONTAINER_SLOTS;

    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END =
            INVENTORY_START + Inventory.INVENTORY_SIZE;

    private final Container container;

    private final MetalForgeBlockEntity forge;

    private final DataSlot lavaData =
            DataSlot.standalone();
    
    public int getProcessingProgress() {
        return progressData.get();
    }

    public int getProcessingTime() {
        return MetalForgeBlockEntity.PROCESS_TIME;
    }

    // Client-side constructor
    public MetalForgeMenu(
            int containerId,
            Inventory inventory
    ) {
        this(
                containerId,
                inventory,
                new SimpleContainer(CONTAINER_SLOTS),
                null
        );
    }

    // Server-side constructor
    public MetalForgeMenu(
            int containerId,
            Inventory inventory,
            MetalForgeBlockEntity forge
    ) {
        this(
                containerId,
                inventory,
                forge,
                forge
        );
    }

    private MetalForgeMenu(
            int containerId,
            Inventory inventory,
            Container container,
            MetalForgeBlockEntity forge
    ) {
        super(ModMenuTypes.METAL_FORGE, containerId);

        checkContainerSize(
                container,
                CONTAINER_SLOTS
        );

        this.container = container;
        this.forge = forge;

        addDataSlot(progressData);

        if (forge != null) {
            progressData.set(
                forge.getProcessingProgress()
            );
        }

        container.startOpen(inventory.player);

        // -----------------------------
        // 3x3 material area
        // -----------------------------

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {

                int slot =
                        column + row * 3;

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

        // -----------------------------
        // Lava input
        // -----------------------------

        addSlot(
                new Slot(
                        container,
                        MetalForgeBlockEntity.LAVA_INPUT_SLOT,
                        134,
                        17
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.is(Items.LAVA_BUCKET);
                    }
                }
        );

        // -----------------------------
        // Empty bucket output
        // -----------------------------

        addSlot(
                new Slot(
                        container,
                        MetalForgeBlockEntity.LAVA_OUTPUT_SLOT,
                        134,
                        53
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(
                            Player player
                    ) {
                        return true;
                    }
                }
        );

        addSlot(
            new Slot(
                    container,
                    MetalForgeBlockEntity.PROCESS_OUTPUT_SLOT,
                    152,
                    35
            ) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            }
    );

        // -----------------------------
        // Player inventory
        // -----------------------------

        addStandardInventorySlots(
                inventory,
                8,
                84
        );

        // -----------------------------
        // Lava synchronization
        // -----------------------------

        addDataSlot(lavaData);
        addDataSlot(progressData);

        if (forge != null) {
            progressData.set(
                    forge.getProcessingProgress()
            );
        }

        if (forge != null) {
            lavaData.set(
                    forge.getLavaAmount()
            );
        }
    }

    public int getLavaAmount() {
        return lavaData.get();
    }

    public int getMaxLava() {
        return MetalForgeBlockEntity.MAX_LAVA;
    }

    @Override
    public void broadcastChanges() {

        if (forge != null) {

            lavaData.set(
                    forge.getLavaAmount()
            );

            progressData.set(
                    forge.getProcessingProgress()
            );
        }

        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
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

        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();

        // -----------------------------
        // Forge -> player inventory
        // -----------------------------

        if (slotIndex < CONTAINER_END) {

            if (!moveItemStackTo(
                    source,
                    INVENTORY_START,
                    INVENTORY_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

        } else {

            // Lava bucket -> lava input
            if (source.is(Items.LAVA_BUCKET)) {

                if (!moveItemStackTo(
                        source,
                        MetalForgeBlockEntity.LAVA_INPUT_SLOT,
                        MetalForgeBlockEntity.LAVA_INPUT_SLOT + 1,
                        false
                )) {
                    return ItemStack.EMPTY;
                }

            } else {

                // Normal items -> 3x3 material slots
                if (!moveItemStackTo(
                        source,
                        CONTAINER_START,
                        MetalForgeBlockEntity.MATERIAL_SLOTS,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (source.isEmpty()) {
            slot.setByPlayer(
                    ItemStack.EMPTY
            );
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        container.stopOpen(
                player
        );
    }
}