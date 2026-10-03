package com.eclipseashes.client;

import com.eclipseashes.block.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import com.eclipseashes.block.ModMenuTypes;
import com.eclipseashes.client.BasicWorkshopScreen;

public class EclipseAshesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        MenuScreens.register(
                ModMenuTypes.METAL_FORGE,
                MetalForgeScreen::new
        );
        MenuScreens.register(
            ModMenuTypes.BASIC_WORKSHOP,
            BasicWorkshopScreen::new
        );
        MenuScreens.register(
                ModMenuTypes.PART_MAKER,
                PartMakerScreen::new
        );
        MenuScreens.register(
                ModMenuTypes.ASSEMBLY_STATION,
                AssemblyStationScreen::new
        );
        MenuScreens.register(
                ModMenuTypes.MAINTENANCE_STATION,
                MaintenanceStationScreen::new
        );
    }
}