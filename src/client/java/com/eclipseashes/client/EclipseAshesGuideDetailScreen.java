package com.eclipseashes.client;

import java.util.ArrayList;
import java.util.List;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

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
    private static final Logger LOGGER = LogUtils.getLogger();
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

        // Try resolving assets during initialization. Some registry entries can
        // still reject ItemStack construction at this point; the resolver treats
        // that as a missing icon rather than letting the guide crash.
        if (!"feature".equals(type)) {
            this.displayStack = safeStackForEntry(type, id);
            this.recipeGrid = hasRecipeGrid(id) ? buildRecipeGrid(id) : null;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // Retry stack creation if the item components were not bound yet. Any
        // runtime failure in optional icon construction is contained by the resolver.
        if (!"feature".equals(type)) {
            if (displayStack == null) displayStack = safeStackForEntry(type, id);
            if (recipeGrid == null && hasRecipeGrid(id)) recipeGrid = buildRecipeGrid(id);
        }
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
        if (displayStack != null) graphics.fakeItem(displayStack, x + 13, y + 12);
        graphics.text(this.font, prettyName(id), x + 52, y + 5, 0xFFFFD98A);
        graphics.text(this.font, "Eclipse Ashes", x + 52, y + 19, 0xFFB8C7D9);
        graphics.text(this.font, "Registry: eclipseashes:" + id, x, y + 47, 0xFFB8C7D9);

        int cursorY = y + 62;
        cursorY = drawSection(graphics, "ABOUT", descriptionFor(id, "block".equals(type)), x, cursorY, textWidth);
        if (hasRecipeGrid(id)) {
            cursorY += 3;
            String gridHeading = compressionRecipe(id)
                    ? "FORGE CONVERSION — 9 matching material items"
                    : "ASSEMBLY STATION — REQUIRED COMPONENTS";
            drawLine(graphics, gridHeading, x, cursorY, 0xFFFFB86B);
            drawRecipeGrid(graphics, x, cursorY + 14);
            drawSection(graphics, "HOW TO USE IT", usesFor(id, compressionRecipe(id)), x, cursorY + 72, textWidth);
        } else {
            cursorY = drawSection(graphics, "RECIPE / HOW TO OBTAIN", recipeFor(id, "block".equals(type)), x, cursorY + 4, textWidth);
            drawSection(graphics, "USES", usesFor(id, "block".equals(type)), x, cursorY + 4, textWidth);
        }
    }

    private void drawRecipeGrid(GuiGraphicsExtractor graphics, int x, int y) {
        int slot = 17;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int sx = x + column * slot;
                int sy = y + row * slot;
                graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF45454F);
                graphics.fill(sx, sy, sx + 16, sy + 1, 0xFF777783);
                if (recipeGrid != null) {
                    ItemStack ingredient = recipeGrid[row * 3 + column];
                    if (ingredient != null) graphics.fakeItem(ingredient, sx, sy);
                }
            }
        }
        graphics.text(this.font, "→", x + 58, y + 22, 0xFFFFFFFF);
        graphics.fill(x + 77, y + 17, x + 109, y + 49, 0xFF34343C);
        if (displayStack != null) graphics.fakeItem(displayStack, x + 85, y + 25);
        graphics.text(this.font, prettyName(id), x + 114, y + 26, 0xFFE4E4E4);
        String instruction = compressionRecipe(id)
                ? "Use a Blacksmith Hammer to forge the block."
                : "Use one Blade, Handle, and Guard of the same material.";
        graphics.text(this.font, instruction, x, y + 55, 0xFFB8C7D9);
    }

    private boolean hasRecipeGrid(String itemId) {
        return compressionRecipe(itemId)
                || ("item".equals(type) && "unfinished_weapon".equals(itemId));
    }

    private ItemStack[] buildRecipeGrid(String entryId) {
        if (compressionRecipe(entryId)) {
            String materialId = entryId.substring(0, entryId.length() - "_block".length());
            ItemStack ingredient = safeStack(materialId);
            if (ingredient == null) return null;
            ItemStack[] grid = new ItemStack[9];
            for (int i = 0; i < grid.length; i++) grid[i] = ingredient.copy();
            return grid;
        }

        if ("item".equals(type) && "unfinished_weapon".equals(entryId)) {
            ItemStack[] grid = new ItemStack[9];
            grid[0] = safeStack("forged_blade");
            grid[1] = safeStack("forged_handle");
            grid[2] = safeStack("forged_guard");
            return grid;
        }

        return null;
    }

    private ItemStack safeStackForEntry(String entryType, String path) {
        Identifier identifier;
        try {
            identifier = Identifier.fromNamespaceAndPath("eclipseashes", path);
        } catch (RuntimeException exception) {
            LOGGER.warn("[Eclipse Ashes Guide] Invalid identifier for type='{}', path='{}'", entryType, path, exception);
            return null;
        }

        try {
            Item item;
            if ("block".equals(entryType)) {
                var block = BuiltInRegistries.BLOCK.get(identifier)
                        .map(reference -> reference.value())
                        .orElse(null);
                if (block == null) {
                    LOGGER.warn("[Eclipse Ashes Guide] Block registry lookup returned empty for {}", identifier);
                    return null;
                }
                item = block.asItem();
                LOGGER.info("[Eclipse Ashes Guide] Resolved block {} to item {}", identifier,
                        BuiltInRegistries.ITEM.getKey(item));
            } else {
                item = BuiltInRegistries.ITEM.get(identifier)
                        .map(reference -> reference.value())
                        .orElse(null);
                if (item == null) {
                    LOGGER.warn("[Eclipse Ashes Guide] Item registry lookup returned empty for {}", identifier);
                    return null;
                }
                LOGGER.info("[Eclipse Ashes Guide] Resolved item {}", identifier);
            }

            Item air = BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("minecraft", "air"))
                    .map(reference -> reference.value())
                    .orElse(null);
            if (item == air) {
                LOGGER.warn("[Eclipse Ashes Guide] Resolved {} to air; no icon will be drawn", identifier);
                return null;
            }

            ItemStack stack = new ItemStack(item);
            LOGGER.info("[Eclipse Ashes Guide] ItemStack created for {} (empty={})", identifier, stack.isEmpty());
            return stack;
        } catch (RuntimeException exception) {
            LOGGER.error("[Eclipse Ashes Guide] Failed to resolve/create ItemStack for type='{}', id='{}'",
                    entryType, identifier, exception);
            return null;
        }
    }

    private ItemStack safeStack(String itemId) {
        return safeStackForEntry("item", itemId);
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
        if (itemId.startsWith("raw_")) return "Starting material dropped by its matching ore. Put it in the Metal Forge with lava to process it into the Unrefined tier.";
        if (itemId.startsWith("unrefined_")) return "Intermediate material produced by processing its Raw form in the Metal Forge. Process it again with lava to obtain the Refined tier.";
        if (itemId.startsWith("refined_")) return "Processed material produced from its Unrefined form in the Metal Forge. A further forge cycle produces the finished ingot.";
        return switch (itemId) {
            case "mold" -> "A reusable workshop tool. Configure it with a material and supported part shape before using it at the Casting Station.";
            case "heated_metal" -> "Material-tagged hot metal produced by processing finished metal in the Metal Forge. Its material must match the configured Mold used for casting or anvil shaping.";
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
        if (block && itemId.endsWith("_ore")) {
            String material = itemId.substring(0, itemId.length() - "_ore".length());
            return "Mine this ore with a pickaxe that meets its tool requirement. The current loot table drops one Raw "
                    + prettyName(material) + ".";
        }
        if (block && itemId.endsWith("_block")) {
            return "The metalworking recipe logic requires nine matching ingots/material items and a Blacksmith Hammer to create this block. This is a separate block-forging operation, not the Metal Forge's lava-based 200-tick material-processing cycle.";
        }
        if (itemId.startsWith("raw_")) {
            return "Metal Forge: process one Raw material with lava to obtain its Unrefined version. The forge consumes 1,000 mB of lava and takes 200 ticks per item.";
        }
        if (itemId.startsWith("unrefined_")) {
            return "Metal Forge: process one Unrefined material with lava to obtain its Refined version. The forge consumes 1,000 mB of lava and takes 200 ticks per item.";
        }
        if (itemId.startsWith("refined_")) {
            return "Metal Forge: process one Refined material with lava to obtain the finished ingot. The forge consumes 1,000 mB of lava and takes 200 ticks per item.";
        }
        return switch (itemId) {
            case "mold" -> "Part Maker: use the station menu to configure a mold with a material and supported part category. The configured data is required by casting and anvil shaping.";
            case "heated_metal" -> "Metal Forge: process a finished metal ingot/material with lava. The output stores its material name so it can be matched to a configured Mold.";
            case "cast_metal" -> "Casting Station: bring a configured Mold and Heated Metal of the same material. The station consumes one Heated Metal and returns a Cast Metal piece carrying the mold's material and category.";
            case "metal_plate" -> "Shape a Cast Metal piece configured as Basic Part while carrying a Blacksmith Hammer, or use a matching Heated Metal and configured Mold on a workshop anvil.";
            case "forged_blade" -> "Shape a Cast Metal piece configured as Head while carrying a Blacksmith Hammer, or use matching Heated Metal and a configured Head Mold on a workshop anvil.";
            case "forged_handle" -> "Shape a Cast Metal piece configured as Handle while carrying a Blacksmith Hammer, or use matching Heated Metal and a configured Handle Mold on a workshop anvil.";
            case "forged_guard" -> "Shape a Cast Metal piece configured as Connector while carrying a Blacksmith Hammer, or use matching Heated Metal and a configured Connector Mold on a workshop anvil.";
            case "unfinished_weapon" -> "Assembly Station: combine one Forged Blade, one Forged Handle, and one Forged Guard. All three components must have the same material.";
            case "sharpening_stone" -> "The current source registers this item and consumes it at the Maintenance Station, but does not define a crafting recipe for it yet.";
            case "blacksmith_hammer" -> "The current source uses this tool for cast shaping and block forging, but does not define a crafting recipe for it yet.";
            default -> "No recipe or acquisition method is confirmed for this entry in the current source.";
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
            case "forged_blade", "forged_handle", "forged_guard", "unfinished_weapon" -> "Part of the implemented weapon-making loop: shape matching material parts, assemble a Blade, Handle, and Guard, then finish the unfinished weapon.";
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