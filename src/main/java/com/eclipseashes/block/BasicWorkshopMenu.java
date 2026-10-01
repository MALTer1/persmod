package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class BasicWorkshopMenu extends AbstractContainerMenu {

    private static final int MAX_DISPLAY_SECTORS = 16;

    private final BasicWorkshopBlockEntity workshop;

    private final DataSlot floorData = DataSlot.standalone();
    private final DataSlot sectionData = DataSlot.standalone();
    private final DataSlot forgeData = DataSlot.standalone();
    private final DataSlot qualityData = DataSlot.standalone();
    private final DataSlot sectorData = DataSlot.standalone();
    private final DataSlot groupData = DataSlot.standalone();

    private final DataSlot[] sectorFloorData = new DataSlot[MAX_DISPLAY_SECTORS];
    private final DataSlot[] sectorForgeData = new DataSlot[MAX_DISPLAY_SECTORS];
    private final DataSlot[] sectorQualityData = new DataSlot[MAX_DISPLAY_SECTORS];

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
        addDataSlot(groupData);

        for (int i = 0; i < MAX_DISPLAY_SECTORS; i++) {
            sectorFloorData[i] = DataSlot.standalone();
            sectorForgeData[i] = DataSlot.standalone();
            sectorQualityData[i] = DataSlot.standalone();

            addDataSlot(sectorFloorData[i]);
            addDataSlot(sectorForgeData[i]);
            addDataSlot(sectorQualityData[i]);
        }

        updateData();
    }

    private void updateData() {
        if (workshop == null) return;

        floorData.set(workshop.getForgeFloorCount());
        sectionData.set(workshop.getForgeSectionCount());
        forgeData.set(workshop.getMetalForgeCount());
        qualityData.set(workshop.getWorkshopQuality());
        sectorData.set(workshop.getSectorCount());
        groupData.set(workshop.getGroupCount());

        int sectorCount = Math.min(workshop.getSectorCount(), MAX_DISPLAY_SECTORS);

        for (int i = 0; i < MAX_DISPLAY_SECTORS; i++) {
            if (i < sectorCount) {
                sectorFloorData[i].set(workshop.getSectorForgeFloorCount(i));
                sectorForgeData[i].set(workshop.getSectorForgeCount(i));
                sectorQualityData[i].set(workshop.getSectorQuality(i));
            } else {
                sectorFloorData[i].set(0);
                sectorForgeData[i].set(0);
                sectorQualityData[i].set(0);
            }
        }
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
    public int getGroupCount() { return groupData.get(); }

    public int getSectorFloorCount(int sector) {
        if (sector < 0 || sector >= MAX_DISPLAY_SECTORS) return 0;
        return sectorFloorData[sector].get();
    }

    public int getSectorForgeCount(int sector) {
        if (sector < 0 || sector >= MAX_DISPLAY_SECTORS) return 0;
        return sectorForgeData[sector].get();
    }

    public int getSectorQuality(int sector) {
        if (sector < 0 || sector >= MAX_DISPLAY_SECTORS) return 0;
        return sectorQualityData[sector].get();
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