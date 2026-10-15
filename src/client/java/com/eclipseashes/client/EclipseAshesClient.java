package com.eclipseashes.client;

import com.eclipseashes.block.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

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

        // Add the guide button to all screens, including vanilla screens.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof EclipseAshesGuideScreen) {
                return;
            }

            Button guideButton = Button.builder(
                    Component.literal("EA"),
                    button -> client.gui.setScreen(new EclipseAshesGuideScreen(screen, false))
            ).bounds(
                    Math.max(4, scaledWidth - 28),
                    4,
                    24,
                    18
            ).build();

            Screens.getWidgets(screen).add(guideButton);
        });
    }
}
