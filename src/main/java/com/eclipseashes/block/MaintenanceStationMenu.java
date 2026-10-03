package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;

public class MaintenanceStationMenu extends StationMenu {

    public MaintenanceStationMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.MAINTENANCE_STATION, containerId);
    }
}