package com.eclipseashes.client;

import com.eclipseashes.block.AssemblyStationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AssemblyStationScreen extends StationScreen<AssemblyStationMenu> {

    public AssemblyStationScreen(AssemblyStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
