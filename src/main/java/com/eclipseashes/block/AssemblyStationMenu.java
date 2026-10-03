package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;

public class AssemblyStationMenu extends StationMenu {

    public AssemblyStationMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.ASSEMBLY_STATION, containerId);
    }
}