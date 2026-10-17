package com.eclipseashes.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Detail page opened from the Eclipse Ashes in-game guide. */
public final class EclipseAshesGuideDetailScreen extends Screen {
    private final Screen parent;
    private final String type;
    private final String id;

    public EclipseAshesGuideDetailScreen(Screen parent, String type, String id) {
        super(Component.literal("Eclipse Ashes Encyclopedia"));
        this.parent = parent;
        this.type = type;
        this.id = id;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        this.addRenderableWidget(Button.builder(Component.literal("Back to Catalogue"), button -> this.onClose())
                .bounds(center - 70, this.height - 28, 140, 20).build());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101018);
        int left = Math.max(12, this.width / 2 - 170);
        int right = Math.min(this.width - 12, this.width / 2 + 170);
        int top = 18;
        int bottom = this.height - 36;
        graphics.fill(left, top, right, bottom, 0xF0202028);
        graphics.fill(left, top, right, top + 2, 0xFFB58A45);
        graphics.text(this.font, this.title, left + 12, top + 12, 0xFFFFD98A);

        int x = left + 12;
        int y = top + 32;
        int textWidth = right - x - 12;

        if ("feature".equals(type)) {
            drawFeature(graphics, x, y, textWidth);
            return;
        }

        // Keep catalogue detail pages independent of ItemStack construction. Some client
        // registries/components may not be ready for safe stack rendering in this screen.
        // A simple category tile is reliable for every registered item and block.
        boolean blockEntry = "block".equals(type);
        graphics.fill(x, y, x + 48, y + 48, 0xFF34343C);
        graphics.fill(x, y, x + 48, y + 3, blockEntry ? 0xFF7FAF83 : 0xFFB58A45);
        String categoryMark = blockEntry ? "B" : "I";
        int markX = x + (48 - this.font.width(categoryMark)) / 2;
        graphics.text(this.font, categoryMark, markX, y + 18, 0xFFFFFFFF);
        graphics.text(this.font, prettyName(id), x + 58, y + 7, 0xFFFFD98A);

        int cursorY = y + 58;
        cursorY = drawLine(graphics, "Registry ID: eclipseashes:" + id, x, cursorY, 0xFFB8C7D9);
        cursorY = drawLine(graphics, "Category: " + ("block".equals(type) ? "Block" : "Item"), x, cursorY + 3, 0xFFFFFFFF);
        cursorY = drawSection(graphics, "ABOUT", descriptionFor(id, "block".equals(type)), x, cursorY + 7, textWidth);
        cursorY = drawSection(graphics, "RECIPE / ACQUISITION", recipeFor(id, "block".equals(type)), x, cursorY + 5, textWidth);
        drawSection(graphics, "USES", usesFor(id, "block".equals(type)), x, cursorY + 5, textWidth);
    }

    private void drawFeature(GuiGraphicsExtractor graphics, int x, int y, int width) {
        // Feature entries are text-only; avoid constructing an ItemStack for a generic topic.
        String heading = featureName(id);
        graphics.text(this.font, heading, x + 36, y + 12, 0xFFFFD98A);
        int cursorY = y + 42;
        cursorY = drawLine(graphics, "Status: " + featureStatus(id), x, cursorY, 0xFFB8C7D9);
        drawSection(graphics, "OVERVIEW", featureDescription(id), x, cursorY + 7, width);
    }

    private int drawSection(
            GuiGraphicsExtractor graphics, String heading, String body,
            int x, int y, int width
    ) {
        int cursorY = drawLine(graphics, heading, x, y, 0xFFFFB86B);
        for (String line : wrapText(body, width)) {
            cursorY = drawLine(graphics, line, x, cursorY + 3, 0xFFE4E4E4);
        }
        return cursorY;
    }

    private String descriptionFor(String itemId, boolean block) {
        if (block && itemId.endsWith("_ore")) {
            return "An Eclipse Ashes ore block. Drops and mining requirements are determined by the block's loot table and tool tags.";
        }
        if (block && itemId.endsWith("_block")) {
            return "A storage block for an Eclipse Ashes material. It can also be used as a building block.";
        }
        return switch (itemId) {
            case "mold" -> "A configurable workshop component. Its saved data identifies a material and a supported category.";
            case "heated_metal" -> "A heated material item used by the workshop casting process.";
            case "cast_metal" -> "A cast workshop component that retains the selected material and category.";
            case "blacksmith_hammer" -> "A specialist tool used by supported workshop metalworking interactions.";
            case "metal_plate" -> "A basic metalworking component for general construction and future recipes.";
            case "sharpening_stone" -> "A maintenance supply used by the current finishing system.";
            case "forged_blade", "forged_handle", "forged_guard", "unfinished_weapon" ->
                    "A component in the mod's weapon-related gameplay system. This page provides general reference information only.";
            default -> itemId.startsWith("raw_") ? "A raw material item for the Eclipse Ashes material progression."
                    : itemId.startsWith("unrefined_") ? "An unrefined material tier in the Eclipse Ashes progression."
                    : itemId.startsWith("refined_") ? "A refined material tier in the Eclipse Ashes progression."
                    : "A registered Eclipse Ashes item. Its behavior depends on the current implementation and available data files.";
        };
    }

    private String recipeFor(String itemId, boolean block) {
        if (itemId.equals("forged_blade") || itemId.equals("forged_handle")
                || itemId.equals("forged_guard") || itemId.equals("unfinished_weapon")) {
            return "Weapon-related recipes are not detailed on this page.";
        }
        if (itemId.equals("mold")) return "The Part Maker is the intended station for configuring molds. Check the station's current controls for supported options.";
        if (itemId.equals("cast_metal")) return "The Casting Station uses a configured Mold and matching Heated Metal. This is a station process rather than a crafting-table recipe.";
        if (itemId.equals("heated_metal")) return "Use the current in-game Metal Forge instructions to check supported heating behavior.";
        if (itemId.equals("metal_plate")) return "The current shaping system supports producing a plate from a cast configured as a Basic Part.";
        if (itemId.equals("blacksmith_hammer") || itemId.equals("sharpening_stone")) {
            return "A dedicated crafting recipe is not documented in the current source files.";
        }
        if (block && itemId.endsWith("_block")) {
            return "The current forge logic supports converting nine matching material items into a block when used with a Blacksmith Hammer.";
        }
        if (block && itemId.endsWith("_ore")) {
            return "Mine the ore with a suitable tool. Exact drops are defined by its block loot table.";
        }
        return "A dedicated recipe file is not present for this entry in the current source. This page avoids inventing a recipe that may not exist in-game.";
    }

    private String usesFor(String itemId, boolean block) {
        if (block && itemId.endsWith("_ore")) return "Provides its associated material according to the configured loot table.";
        if (block && itemId.endsWith("_block")) return "Material storage and building.";
        return switch (itemId) {
            case "mold" -> "Used by the Casting Station; configuration data carries material and category.";
            case "heated_metal" -> "Used by the Casting Station when its material matches the configured Mold.";
            case "cast_metal" -> "Input for supported workshop shaping operations.";
            case "blacksmith_hammer" -> "Used by supported metalworking interactions and block forging.";
            case "metal_plate" -> "General metalworking component.";
            case "sharpening_stone" -> "Used by the Maintenance Station's current finishing system.";
            case "forged_blade", "forged_handle", "forged_guard", "unfinished_weapon" ->
                    "Belongs to the mod's weapon-related gameplay progression.";
            default -> itemId.startsWith("raw_") || itemId.startsWith("unrefined_") || itemId.startsWith("refined_")
                    ? "Material progression; supported uses may expand as development continues."
                    : "No additional use is documented in the current source yet.";
        };
    }

    private String featureName(String featureId) {
        return switch (featureId) {
            case "ores" -> "Ores and Materials";
            case "tools" -> "Tools and Equipment";
            case "workshop" -> "Forge and Workshop";
            case "player" -> "Player Progression";
            case "world" -> "World Generation";
            case "story" -> "Creatures and Story";
            default -> "Eclipse Ashes Feature";
        };
    }

    private String featureStatus(String featureId) {
        return switch (featureId) {
            case "ores", "tools" -> "Implemented";
            case "workshop" -> "In Progress";
            default -> "Planned";
        };
    }

    private String featureDescription(String featureId) {
        return switch (featureId) {
            case "ores" -> "Custom ores, material items, storage blocks, loot tables, and tool tags form the material foundation.";
            case "tools" -> "Custom material tools are registered. Recipe documentation and balancing can be expanded as the mod develops.";
            case "workshop" -> "The workshop includes metalworking stations and staged material-processing interactions. Some systems are still being developed.";
            case "player" -> "Phase 3 is planned to introduce selectable race, rank, level, breathing style, and race-specific features.";
            case "world" -> "A later phase will focus on structures, points of interest, biomes, and dimensions.";
            case "story" -> "Later development will add creatures, NPCs, quests, boss encounters, and the campaign for the different gameplay paths.";
            default -> "No description is available for this entry yet.";
        };
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (this.font.width(candidate) > maxWidth && !line.isEmpty()) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return lines;
    }

    private String prettyName(String path) {
        StringBuilder result = new StringBuilder();
        for (String word : path.replace('_', ' ').split(" ")) {
            if (!result.isEmpty()) result.append(' ');
            if (!word.isEmpty()) result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    private int drawLine(GuiGraphicsExtractor graphics, String text, int x, int y, int color) {
        graphics.text(this.font, text, x, y, color);
        return y + 12;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
