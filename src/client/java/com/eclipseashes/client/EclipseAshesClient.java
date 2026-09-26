package com.eclipseashes.client;

import com.eclipseashes.block.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class EclipseAshesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        MenuScreens.register(
                ModMenuTypes.METAL_FORGE,
                MetalForgeScreen::new
        );
    }
}