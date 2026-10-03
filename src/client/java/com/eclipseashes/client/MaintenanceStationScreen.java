package com.eclipseashes.client;

import com.eclipseashes.block.MaintenanceStationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MaintenanceStationScreen extends StationScreen {

    public MaintenanceStationScreen(MaintenanceStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
