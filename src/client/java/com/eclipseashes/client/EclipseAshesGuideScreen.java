package com.eclipseashes.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Read-only in-game reference for Eclipse Ashes.
 * The guide intentionally contains no world-changing developer actions.
 */
public final class EclipseAshesGuideScreen extends Screen {

    private final Screen parent;
    private final boolean browseAll;

    public EclipseAshesGuideScreen(Screen parent, boolean browseAll) {
        super(Component.literal("Eclipse Ashes Guide"));
        this.parent = parent;
        this.browseAll = browseAll;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int buttonY = this.height - 34;

        if (this.browseAll) {
            this.addRenderableWidget(Button.builder(
                    Component.literal("Current Screen"),
                    button -> Minecraft.getInstance().setScreen(
                            new EclipseAshesGuideScreen(this.parent, false)
                    )
            ).bounds(center - 104, buttonY, 100, 20).build());
        } else {
            this.addRenderableWidget(Button.builder(
                    Component.literal("Browse All"),
                    button -> Minecraft.getInstance().setScreen(
                            new EclipseAshesGuideScreen(this.parent, true)
                    )
            ).bounds(center - 104, buttonY, 100, 20).build());
        }

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                button -> this.onClose()
        ).bounds(center + 4, buttonY, 100, 20).build());
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101018);

        int panelLeft = Math.max(12, this.width / 2 - 170);
        int panelRight = Math.min(this.width - 12, this.width / 2 + 170);
        int panelTop = 18;
        int panelBottom = this.height - 42;

        graphics.fill(panelLeft, panelTop, panelRight, panelBottom, 0xF0202028);
        graphics.fill(panelLeft, panelTop, panelRight, panelTop + 2, 0xFFB58A45);

        graphics.text(this.font, this.title, panelLeft + 12, panelTop + 12, 0xFFFFD98A);

        int y = panelTop + 32;
        if (this.browseAll) {
            y = drawLine(graphics, "ECLIPSE ASHES — FEATURE DIRECTORY", panelLeft + 12, y, 0xFFFFFFFF);
            y = drawLine(graphics, "Items and materials", panelLeft + 12, y + 6, 0xFFE4D5BC);
            y = drawLine(graphics, "Tools and equipment", panelLeft + 12, y + 3, 0xFFE4D5BC);
            y = drawLine(graphics, "Forge and workshop stations", panelLeft + 12, y + 3, 0xFFE4D5BC);
            y = drawLine(graphics, "Player progression (planned for a later phase)", panelLeft + 12, y + 3, 0xFFB8C7D9);
            y = drawLine(graphics, "World generation and settlements (planned)", panelLeft + 12, y + 3, 0xFFB8C7D9);
            y = drawLine(graphics, "Creatures, quests, and story (planned)", panelLeft + 12, y + 3, 0xFFB8C7D9);
            drawLine(graphics, "Entries will expand as features are implemented and tested.", panelLeft + 12, y + 8, 0xFFB8B8B8);
        } else {
            y = drawLine(graphics, "CURRENT SCREEN", panelLeft + 12, y, 0xFFFFFFFF);
            y = drawLine(graphics, screenHeading(), panelLeft + 12, y + 6, 0xFFFFD98A);
            for (String line : screenDescription()) {
                y = drawLine(graphics, line, panelLeft + 12, y + 4, 0xFFE4E4E4);
            }

            if (hasDeveloperAccess()) {
                y = drawLine(graphics, "DEVELOPER NOTES", panelLeft + 12, y + 10, 0xFFFFB86B);
                drawLine(graphics, "Use this entry during playtests to check labels, controls, and expected screen behavior.", panelLeft + 12, y + 4, 0xFFFFD7B0);
            }
        }
    }

    private int drawLine(
            GuiGraphicsExtractor graphics,
            String text,
            int x,
            int y,
            int color
    ) {
        graphics.text(this.font, text, x, y, color);
        return y + 12;
    }

    private String screenHeading() {
        if (parent == null) {
            return "Eclipse Ashes overview";
        }

        String name = parent.getClass().getSimpleName();
        return switch (name) {
            case "MetalForgeScreen" -> "Metal Forge";
            case "BasicWorkshopScreen" -> "Basic Workshop";
            case "PartMakerScreen" -> "Part Maker";
            case "AssemblyStationScreen" -> "Assembly Station";
            case "MaintenanceStationScreen" -> "Maintenance Station";
            case "InventoryScreen" -> "Player Inventory";
            case "CraftingScreen" -> "Crafting Table";
            case "TitleScreen" -> "Main Menu";
            case "PauseScreen" -> "Game Menu";
            default -> "Minecraft screen: " + name;
        };
    }

    private String[] screenDescription() {
        if (parent == null) {
            return new String[] {
                    "This guide explains Eclipse Ashes features and their current status.",
                    "Choose Browse All to see the feature directory."
            };
        }

        String name = parent.getClass().getSimpleName();
        return switch (name) {
            case "MetalForgeScreen" -> new String[] {
                    "Purpose: a modded station for working with materials.",
                    "Read the station labels and check its displayed controls.",
                    "If something behaves unexpectedly, record the steps for a playtest."
            };
            case "BasicWorkshopScreen" -> new String[] {
                    "Purpose: a general workshop interface.",
                    "Use the screen labels to understand the currently available functions.",
                    "Some planned functions may not be implemented yet."
            };
            case "PartMakerScreen" -> new String[] {
                    "Purpose: a dedicated interface for a workshop process.",
                    "Check the displayed inputs, outputs, and instructions.",
                    "The guide will be expanded as this feature is finalized."
            };
            case "AssemblyStationScreen" -> new String[] {
                    "Purpose: a station interface for combining compatible components.",
                    "Review its visible slots and labels before testing.",
                    "Not every planned function is necessarily implemented yet."
            };
            case "MaintenanceStationScreen" -> new String[] {
                    "Purpose: a station interface for maintenance-related features.",
                    "Check the current screen labels and available controls.",
                    "Use playtests to identify missing or confusing instructions."
            };
            default -> new String[] {
                    "This screen does not have a dedicated Eclipse Ashes entry yet.",
                    "Choose Browse All for the current feature directory.",
                    "Guide entries will be added as features are implemented."
            };
        };
    }

    private boolean hasDeveloperAccess() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null
                && (client.player.isCreative() || client.player.hasPermissions(2));
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }
}
