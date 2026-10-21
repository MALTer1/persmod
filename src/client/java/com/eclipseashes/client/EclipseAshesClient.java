package com.eclipseashes.client;

import com.eclipseashes.block.ModMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class EclipseAshesClient implements ClientModInitializer {
    private static final Set<String> HIDE_GUIDE_ON_SCREENS = Set.of(
            "TitleScreen",
            "SelectWorldScreen",
            "CreateWorldScreen",
            "WorldCreationUi",
            "WorldCreationScreen",
            "EditGameRulesScreen",
            "DatapackSelectionScreen",
            "BackupConfirmScreen",
            "OptionsScreen",
            "LanguageSelectScreen",
            "PackSelectionScreen"
    );

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

        // Keep the quick-access guide in gameplay, but not on title-menu
        // or world-setup screens. Mod Menu provides the menu/settings entry.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof EclipseAshesGuideScreen || isMainMenuOrWorldSetup(screen)) {
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

    private static boolean isMainMenuOrWorldSetup(net.minecraft.client.gui.screens.Screen screen) {
        for (Class<?> type = screen.getClass(); type != null; type = type.getSuperclass()) {
            if (HIDE_GUIDE_ON_SCREENS.contains(type.getSimpleName())) {
                return true;
            }
        }
        return false;
    }
}
