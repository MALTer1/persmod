package com.eclipseashes.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Detail page opened from the Eclipse Ashes in-game guide. */
public final class EclipseAshesGuideDetailScreen extends Screen {
    private final Screen parent;
    private final String type;
    private final String id;
    private ItemStack displayStack;
    private ItemStack[] recipeGrid;

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

        // Resolve stacks during initialization, not while drawing the screen.
        if (!"feature".equals(type)) {
            this.displayStack = safeStack(id);
            this.recipeGrid = compressionRecipe(id) ? buildCompressionGrid(id) : null;
        }
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

        graphics.fill(x, y, x + 42, y + 42, 0xFF34343C);
        graphics.fill(x, y, x + 42, y + 3, "block".equals(type) ? 0xFF7FAF83 : 0xFFB58A45);
        if (displayStack != null) graphics.item(displayStack, x + 13, y + 12);
        graphics.text(this.font, prettyName(id), x + 52, y + 5, 0xFFFFD98A);
        graphics.text(this.font, "Eclipse Ashes", x + 52, y + 19, 0xFFB8C7D9);
        graphics.text(this.font, "Registry: eclipseashes:" + id, x, y + 47, 0xFFB8C7D9);

        int cursorY = y + 62;
        cursorY = drawSection(graphics, "ABOUT", descriptionFor(id, "block".equals(type)), x, cursorY, textWidth);
        if (compressionRecipe(id)) {
            cursorY += 3;
            drawLine(graphics, "FORGE CONVERSION — 9 matching material items", x, cursorY, 0xFFFFB86B);
            drawCompressionRecipe(graphics, x, cursorY + 14);
            drawSection(graphics, "HOW TO USE IT", usesFor(id, true), x, cursorY + 72, textWidth);
        } else {
            cursorY = drawSection(graphics, "RECIPE / HOW TO OBTAIN", recipeFor(id, "block".equals(type)), x, cursorY + 4, textWidth);
            drawSection(graphics, "USES", usesFor(id, "block".equals(type)), x, cursorY + 4, textWidth);
        }
    }

    private void drawCompressionRecipe(GuiGraphicsExtractor graphics, int x, int y) {
        int slot = 17;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int sx = x + column * slot;
                int sy = y + row * slot;
                graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF45454F);
                graphics.fill(sx, sy, sx + 16, sy + 1, 0xFF777783);
                if (recipeGrid != null) {
                    ItemStack ingredient = recipeGrid[row * 3 + column];
                    if (ingredient != null) graphics.item(ingredient, sx, sy);
                }
            }
        }
        graphics.text(this.font, "→", x + 58, y + 22, 0xFFFFFFFF);
        graphics.fill(x + 77, y + 17, x + 109, y + 49, 0xFF34343C);
        if (displayStack != null) graphics.item(displayStack, x + 85, y + 25);
        graphics.text(this.font, prettyName(id), x + 114, y + 26, 0xFFE4E4E4);
        graphics.text(this.font, "Use the Blacksmith Hammer at the forge.", x, y + 55, 0xFFB8C7D9);
    }

    private ItemStack[] buildCompressionGrid(String blockId) {
        String materialId = blockId.substring(0, blockId.length() - "_block".length());
        ItemStack ingredient = safeStack(materialId);
        if (ingredient == null) return null;
        ItemStack[] grid = new ItemStack[9];
        for (int i = 0; i < grid.length; i++) grid[i] = ingredient.copy();
        return grid;
    }

    private ItemStack safeStack(String itemId) {
        try {
            Identifier identifier = Identifier.fromNamespaceAndPath("eclipseashes", itemId);
            Item item = BuiltInRegistries.ITEM.get(identifier)
                    .map(reference -> reference.value())
                    .orElse(null);
            Item air = BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("minecraft", "air"))
                    .map(reference -> reference.value())
                    .orElse(null);
            if (item == null || item == air) return null;
            return new ItemStack(item);
        } catch (IllegalStateException exception) {
            // Item components may not be bound during an early initialization state.
            return null;
        }
    }

    private boolean compressionRecipe(String itemId) {
        return "block".equals(type) && itemId.endsWith("_block");
    }

    private void drawFeature(GuiGraphicsExtractor graphics, int x, int y, int width) {
        graphics.text(this.font, featureName(id), x, y + 4, 0xFFFFD98A);
        int cursorY = drawLine(graphics, "Status: " + featureStatus(id), x, y + 24, 0xFFB8C7D9);
        drawSection(graphics, "OVERVIEW", featureDescription(id), x, cursorY + 4, width);
    }

    private int drawSection(GuiGraphicsExtractor graphics, String heading, String body, int x, int y, int width) {
        int cursorY = drawLine(graphics, heading, x, y, 0xFFFFB86B);
        for (String line : wrapText(body, width)) cursorY = drawLine(graphics, line, x, cursorY + 2, 0xFFE4E4E4);
        return cursorY;
    }

    private String descriptionFor(String itemId, boolean block) {
        if (block && itemId.endsWith("_ore")) {
            return prettyName(itemId.substring(0, itemId.length() - 4)) + " ore. Mine it with a pickaxe that meets the block's tool requirement. The exact drops are controlled by the ore's loot table.";
        }
        if (block && itemId.endsWith("_block")) {
            return "A compact storage and building block made from " + prettyName(itemId.substring(0, itemId.length() - 6)) + ". The current forge conversion uses nine matching material items and a Blacksmith Hammer.";
        }
        if (block) return switch (itemId) {
            case "metal_forge" -> "A workstation for the mod's metalworking progression.";
            case "basic_workshop" -> "A workshop station that provides access to supported early metalworking operations.";
            case "part_maker" -> "A station used to configure molds and select a component shape.";
            case "casting_station" -> "Casts matching heated metal using a configured mold.";
            case "assembly_station" -> "A workstation for combining compatible forged components.";
            case "maintenance_station" -> "A workstation for supported weapon maintenance and finishing.";
            case "forge_floor", "forge_section", "advanced_forge" -> "A structural part of the Eclipse Ashes forge setup.";
            default -> "A registered Eclipse Ashes block. Its behavior depends on the block implementation and data files.";
        };
        if (itemId.startsWith("raw_")) return "Raw " + prettyName(itemId.substring(4)) + " material. This is the starting material tier; check the matching ore's loot table to see what it drops.";
        if (itemId.startsWith("unrefined_")) return "Unrefined " + prettyName(itemId.substring(10)) + ". An intermediate material tier intended for further processing.";
        if (itemId.startsWith("refined_")) return "Refined " + prettyName(itemId.substring(8)) + ". This is the processed material tier for the metal progression.";
        return switch (itemId) {
            case "mold" -> "A reusable workshop tool. Configure it with a material and supported part shape before using it at the Casting Station.";
            case "heated_metal" -> "Metal prepared for casting. It must match the material selected in the mold used at the Casting Station.";
            case "cast_metal" -> "The output of casting matching Heated Metal with a configured Mold. Its saved data identifies the material and selected category.";
            case "blacksmith_hammer" -> "A metalworking tool used by supported forge interactions, including converting nine matching material items into a storage block.";
            case "metal_plate" -> "A shaped metalworking component produced from a cast configured as a Basic Part.";
            case "sharpening_stone" -> "A maintenance supply used by the current finishing system.";
            case "forged_blade" -> "A forged weapon component intended to become part of an assembled weapon.";
            case "forged_handle" -> "A handle component used in the weapon assembly progression.";
            case "forged_guard" -> "A guard component used in the weapon assembly progression.";
            case "unfinished_weapon" -> "An intermediate weapon made from compatible forged components; it is not yet the finished weapon.";
            default -> "A registered Eclipse Ashes item. Its supported behavior and recipes depend on the current implementation.";
        };
    }

    private String recipeFor(String itemId, boolean block) {
        if (block && itemId.endsWith("_ore")) return "Not crafted. Find the ore in the world and mine it with a suitable pickaxe. The block's loot table determines the exact drop.";
        if (itemId.startsWith("raw_")) return "Obtained from the corresponding ore when its loot table drops this raw material. No crafting-table recipe is implemented yet.";
        if (itemId.startsWith("unrefined_") || itemId.startsWith("refined_")) return "This material belongs to the processing progression. The specific process still needs to be designed and implemented.";
        return switch (itemId) {
            case "mold" -> "Configure this at the Part Maker. This is a station-based process, not a crafting-table recipe.";
            case "heated_metal" -> "Use the planned Metal Forge heating process. Recipe behavior still needs to be finalized.";
            case "cast_metal" -> "Casting Station: provide Heated Metal and a Mold configured for the matching material and part category.";
            case "metal_plate" -> "Part Maker / shaping process: use a cast configured as a Basic Part.";
            case "blacksmith_hammer", "sharpening_stone" -> "No recipe is implemented yet. We should decide the ingredients before adding its recipe data.";
            case "forged_blade", "forged_handle", "forged_guard", "unfinished_weapon" -> "Use the planned forge and assembly progression. Exact inputs and outputs still need to be decided.";
            default -> "No recipe is implemented yet. This page will display a 3×3 grid when we define and add a real recipe.";
        };
    }

    private String usesFor(String itemId, boolean block) {
        if (block && itemId.endsWith("_ore")) return "Mine it for the material specified by its loot table. Tool requirements are enforced by the block.";
        if (block && itemId.endsWith("_block")) return "Stores nine matching material items in one block and can be used as a building block.";
        if (block) return "Used as part of the Eclipse Ashes workshop or forge setup.";
        if (itemId.startsWith("raw_")) return "Starting material tier; follow its processing path through the forge/workshop.";
        if (itemId.startsWith("unrefined_")) return "Intermediate material tier for further processing.";
        if (itemId.startsWith("refined_")) return "Processed material tier for supported crafting and forging progression.";
        return switch (itemId) {
            case "mold" -> "Configure material and part category for the Casting Station.";
            case "heated_metal" -> "Input to the Casting Station when it matches the configured Mold.";
            case "cast_metal" -> "Input to supported workshop shaping operations.";
            case "blacksmith_hammer" -> "Used by supported metalworking interactions and forge block conversion.";
            case "metal_plate" -> "A metalworking component for supported crafting and assembly.";
            case "sharpening_stone" -> "Used by the Maintenance Station's current finishing system.";
            case "forged_blade", "forged_handle", "forged_guard", "unfinished_weapon" -> "Part of the mod's weapon forging and assembly progression.";
            default -> "No additional use is documented in the current source yet.";
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
            case "ores" -> "Custom ores, material items, storage blocks, loot tables, and tool requirements form the material foundation. Check each ore page for mining guidance.";
            case "tools" -> "Custom material tools are registered. Recipe documentation and balancing can expand as the mod develops.";
            case "workshop" -> "The workshop includes metalworking stations and staged material-processing interactions. Station-based processes are described separately from crafting-table recipes.";
            case "player" -> "Phase 3 is planned to introduce selectable race, rank, level, breathing style, and race-specific features.";
            case "world" -> "A later phase will focus on structures, points of interest, biomes, and dimensions.";
            case "story" -> "Later development will add creatures, NPCs, quests, boss encounters, and campaigns for the different gameplay paths.";
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
            } else line = new StringBuilder(candidate);
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