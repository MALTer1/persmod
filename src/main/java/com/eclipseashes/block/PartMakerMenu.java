package com.eclipseashes.block;

import net.minecraft.world.entity.player.Inventory;

public class PartMakerMenu extends StationMenu {

    public PartMakerMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.PART_MAKER, containerId);
    }
}