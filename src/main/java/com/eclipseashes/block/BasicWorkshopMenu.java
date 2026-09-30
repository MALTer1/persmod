package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class BasicWorkshopMenu extends AbstractContainerMenu {

    private final BasicWorkshopBlockEntity workshop;

    private final DataSlot floorData = DataSlot.standalone();
    private final DataSlot sectionData = DataSlot.standalone();
    private final DataSlot forgeData = DataSlot.standalone();
    private final DataSlot qualityData = DataSlot.standalone();
    private final DataSlot sectorData = DataSlot.standalone();

    public BasicWorkshopMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, null);
    }

    public BasicWorkshopMenu(
            int containerId,
            Inventory inventory,
            BasicWorkshopBlockEntity workshop
    ) {
        super(ModMenuTypes.BASIC_WORKSHOP, containerId);
        this.workshop = workshop;

        addDataSlot(floorData);
        addDataSlot(sectionData);
        addDataSlot(forgeData);
        addDataSlot(qualityData);
        addDataSlot(sectorData);

        updateData();
    }

    private void updateData() {
        if (workshop == null) return;

        floorData.set(workshop.getForgeFloorCount());
        sectionData.set(workshop.getForgeSectionCount());
        forgeData.set(workshop.getMetalForgeCount());
        qualityData.set(workshop.getWorkshopQuality());
        sectorData.set(workshop.getSectorCount());
    }

    @Override
    public void broadcastChanges() {
        updateData();
        super.broadcastChanges();
    }

    public int getFloorCount() { return floorData.get(); }
    public int getSectionCount() { return sectionData.get(); }
    public int getForgeCount() { return forgeData.get(); }
    public int getQuality() { return qualityData.get(); }
    public int getSectorCount() { return sectorData.get(); }

    public int getSectorFloorCount(int sector) {
        return workshop == null ? 0 : workshop.getSectorForgeFloorCount(sector);
    }

    public int getSectorForgeCount(int sector) {
        return workshop == null ? 0 : workshop.getSectorForgeCount(sector);
    }

    public int getSectorQuality(int sector) {
        return workshop == null ? 0 : workshop.getSectorQuality(sector);
    }

    public String getWorkshopName() {
        return workshop == null ? "Basic Workshop" : workshop.getWorkshopName();
    }

    @Override
    public boolean stillValid(Player player) {
        return workshop != null && workshop.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}