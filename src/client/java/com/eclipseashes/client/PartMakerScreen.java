package com.eclipseashes.client;

import com.eclipseashes.block.PartMakerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartMakerScreen extends StationScreen<PartMakerMenu> {

    public PartMakerScreen(PartMakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
