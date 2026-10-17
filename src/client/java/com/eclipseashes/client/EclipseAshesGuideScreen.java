package com.eclipseashes.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Read-only in-game reference for Eclipse Ashes.
 * The item and block catalogues are populated from Minecraft's live registries.
 */
public final class EclipseAshesGuideScreen extends Screen {
    private static final int PAGE_SIZE = 8;

    private final Screen parent;
    private final boolean browseAll;
    private final String section;
    private final int page;

    public EclipseAshesGuideScreen(Screen parent, boolean browseAll) {
        this(parent, browseAll, "features", 0);
    }

    private EclipseAshesGuideScreen(Screen parent, boolean browseAll, String section, int page) {
        super(Component.literal("Eclipse Ashes Guide"));
        this.parent = parent;
        this.browseAll = browseAll;
        this.section = section;
        this.page = Math.max(0, page);
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int buttonY = this.height - 28;

        if (browseAll) {
            addSectionButton("Features", "features", center - 151);
            addSectionButton("Items", "items", center - 76);
            addSectionButton("Blocks", "blocks", center - 1);

            if ("features".equals(section)) {
                String[][] features = {
                        {"ores", "Ores and Materials"},
                        {"tools", "Tools and Equipment"},
                        {"workshop", "Forge and Workshop"},
                        {"player", "Player Progression"},
                        {"world", "World Generation"},
                        {"story", "Creatures and Story"}
                };
                int rowY = 78;
                for (String[] feature : features) {
                    String featureId = feature[0];
                    this.addRenderableWidget(Button.builder(Component.literal(feature[1]), button ->
                            Minecraft.getInstance().gui.setScreen(
                                    new EclipseAshesGuideDetailScreen(this, "feature", featureId)
                            )).bounds(center - 150, rowY, 300, 14).build());
                    rowY += 15;
                }
            } else {
                List<String> entries = catalogueEntries();
                int start = page * PAGE_SIZE;
                int end = Math.min(entries.size(), start + PAGE_SIZE);
                int rowY = 78;
                for (int i = start; i < end; i++) {
                    String entry = entries.get(i);
                    String entryId = entry.toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
                    String type = "items".equals(section) ? "item" : "block";
                    this.addRenderableWidget(Button.builder(Component.literal(entry), button ->
                            Minecraft.getInstance().gui.setScreen(
                                    new EclipseAshesGuideDetailScreen(this, type, entryId)
                            )).bounds(center - 150, rowY, 300, 14).build());
                    rowY += 15;
                }
            }

            List<String> entries = catalogueEntries();
            int maxPage = Math.max(0, (entries.size() - 1) / PAGE_SIZE);
            if (page > 0) {
                this.addRenderableWidget(Button.builder(Component.literal("Previous"), button ->
                        Minecraft.getInstance().gui.setScreen(
                                new EclipseAshesGuideScreen(parent, true, section, page - 1)
                        )).bounds(center - 104, this.height - 53, 100, 20).build());
            }
            if (page < maxPage) {
                this.addRenderableWidget(Button.builder(Component.literal("Next"), button ->
                        Minecraft.getInstance().gui.setScreen(
                                new EclipseAshesGuideScreen(parent, true, section, page + 1)
                        )).bounds(center + 4, this.height - 53, 100, 20).build());
            }
            this.addRenderableWidget(Button.builder(Component.literal("Current Screen"), button ->
                    Minecraft.getInstance().gui.setScreen(new EclipseAshesGuideScreen(parent, false))
            ).bounds(center - 104, buttonY, 100, 20).build());
        } else {
            this.addRenderableWidget(Button.builder(Component.literal("Browse All"), button ->
                    Minecraft.getInstance().gui.setScreen(
                            new EclipseAshesGuideScreen(parent, true, "features", 0)
                    )).bounds(center - 104, buttonY, 100, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Back"), button -> this.onClose())
                .bounds(center + 4, buttonY, 100, 20).build());
    }

    private void addSectionButton(String label, String target, int x) {
        this.addRenderableWidget(Button.builder(Component.literal(label), button ->
                Minecraft.getInstance().gui.setScreen(
                        new EclipseAshesGuideScreen(parent, true, target, 0)
                )).bounds(x, 42, 72, 20).build());
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
        int panelBottom = this.height - 36;
        graphics.fill(panelLeft, panelTop, panelRight, panelBottom, 0xF0202028);
        graphics.fill(panelLeft, panelTop, panelRight, panelTop + 2, 0xFFB58A45);
        graphics.text(this.font, this.title, panelLeft + 12, panelTop + 12, 0xFFFFD98A);

        if (browseAll) {
            drawBrowseSection(graphics, panelLeft + 12, panelTop + 54, panelRight - 12, panelBottom);
        } else {
            int y = panelTop + 32;
            y = drawLine(graphics, "CURRENT SCREEN", panelLeft + 12, y, 0xFFFFFFFF);
            y = drawLine(graphics, screenHeading(), panelLeft + 12, y + 6, 0xFFFFD98A);
            for (String line : screenDescription()) {
                y = drawLine(graphics, line, panelLeft + 12, y + 4, 0xFFE4E4E4);
            }
            if (hasDeveloperAccess()) {
                y = drawLine(graphics, "DEVELOPER NOTES", panelLeft + 12, y + 10, 0xFFFFB86B);
                drawLine(graphics, "Use playtests to check labels, controls, and expected behavior.", panelLeft + 12, y + 4, 0xFFFFD7B0);
            }
        }
    }

    private void drawBrowseSection(
            GuiGraphicsExtractor graphics, int x, int y, int right, int bottom
    ) {
        if ("features".equals(section)) {
            drawLine(graphics, "FEATURE DIRECTORY — select a topic below", x, y, 0xFFFFFFFF);
            return;
        }

        List<String> entries = catalogueEntries();
        String heading = "items".equals(section) ? "ECLIPSE ASHES ITEMS" : "ECLIPSE ASHES BLOCKS";
        drawLine(graphics, heading + " (" + entries.size() + ") — select an entry below", x, y, 0xFFFFFFFF);
        int maxPage = Math.max(0, (entries.size() - 1) / PAGE_SIZE);
        drawLine(graphics, "Page " + (Math.min(page, maxPage) + 1) + " of " + (maxPage + 1),
                x, bottom - 15, 0xFFB8B8B8);
    }

    private List<String> catalogueEntries() {
        List<String> entries = new ArrayList<>();
        if ("items".equals(section)) {
            for (Item item : BuiltInRegistries.ITEM) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                if (id != null && "eclipseashes".equals(id.getNamespace())) {
                    entries.add(prettyName(id.getPath()));
                }
            }
        } else if ("blocks".equals(section)) {
            for (Block block : BuiltInRegistries.BLOCK) {
                Identifier id = BuiltInRegistries.BLOCK.getKey(block);
                if (id != null && "eclipseashes".equals(id.getNamespace())) {
                    entries.add(prettyName(id.getPath()));
                }
            }
        }
        entries.sort(Comparator.naturalOrder());
        return entries;
    }

    private String prettyName(String path) {
        String[] words = path.replace('_', ' ').split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) result.append(' ');
            if (!word.isEmpty()) result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    private int drawLine(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        graphics.text(this.font, text, x, y, color);
        return y + 12;
    }

    private String screenHeading() {
        if (parent == null) return "Eclipse Ashes overview";
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
        if (parent == null) return new String[] {
                "This guide explains Eclipse Ashes features and their current status.",
                "Choose Browse All to inspect features, items, and blocks."
        };
        String name = parent.getClass().getSimpleName();
        return switch (name) {
            case "MetalForgeScreen" -> new String[] {
                    "Purpose: a modded station for working with materials.",
                    "Check the station labels and displayed controls.",
                    "Record unexpected behavior during playtests."
            };
            case "BasicWorkshopScreen" -> new String[] {
                    "Purpose: a general workshop interface.",
                    "Use the screen labels to understand available functions.",
                    "Some planned functions may not be implemented yet."
            };
            case "PartMakerScreen" -> new String[] {
                    "Purpose: a dedicated interface for a workshop process.",
                    "Check its displayed inputs, outputs, and instructions."
            };
            case "AssemblyStationScreen" -> new String[] {
                    "Purpose: a station for combining compatible components.",
                    "Review its visible slots and labels before testing."
            };
            case "MaintenanceStationScreen" -> new String[] {
                    "Purpose: a station interface for maintenance-related features.",
                    "Check the current labels and available controls."
            };
            default -> new String[] {
                    "This screen does not have a dedicated Eclipse Ashes entry yet.",
                    "Choose Browse All for the feature directory and catalogue."
            };
        };
    }

    private boolean hasDeveloperAccess() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && client.player.isCreative();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(this.parent);
    }
}
