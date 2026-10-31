package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import com.eclipseashes.item.WeaponStatCalculator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.level.block.Blocks;

/** Server-side recipes for the first playable metalworking loop. */
public final class MetalworkingRecipes {
    private MetalworkingRecipes() {}

    public static boolean castMold(Player player) {
        int moldSlot = find(player, ModItem.MOLD);
        if (moldSlot < 0) {
            message(player, "Bring a configured Mold to the Casting Station.");
            return true;
        }

        ItemStack mold = player.getInventory().getItem(moldSlot);
        CompoundTag moldData = customData(mold);
        String material = stringTag(moldData, "material");
        String category = stringTag(moldData, "category");
        if (!"1".equals(stringTag(moldData, "eclipse_ashes_mold"))
                || material.isBlank() || category.isBlank()) {
            message(player, "This Mold is not configured. Recreate it at the Part Maker.");
            return true;
        }
        int metalSlot = findHeatedMetal(player, material);
        if (metalSlot < 0) {
            message(player, "Heat one " + material + " in the Metal Forge first, then bring the Heated Metal here.");
            return true;
        }

        ItemStack cast = new ItemStack(ModItem.CAST_METAL);
        CustomData.set(DataComponents.CUSTOM_DATA, cast, moldData.copy());
        cast.set(DataComponents.CUSTOM_NAME, Component.literal(material + " Cast " + category));
        consume(player, metalSlot, 1);
        give(player, cast);
        message(player, "Cast complete. Take the cast piece to the Modded Anvil.");
        return true;
    }

    public static boolean shapeCast(Player player) {
        int castSlot = find(player, ModItem.CAST_METAL);
        if (castSlot < 0) return forgeBlock(player);
        if (find(player, ModItem.BLACKSMITH_HAMMER) < 0) {
            message(player, "You need a Blacksmith Hammer in your inventory to shape metal.");
            return true;
        }

        ItemStack cast = player.getInventory().getItem(castSlot);
        CompoundTag data = customData(cast);
        String category = stringTag(data, "category");
        String material = stringTag(data, "material");
        Item outputItem;
        String partName;
        switch (category) {
            case "Head" -> { outputItem = ModItem.FORGED_BLADE; partName = "Blade"; }
            case "Handle" -> { outputItem = ModItem.FORGED_HANDLE; partName = "Handle"; }
            case "Connector" -> { outputItem = ModItem.FORGED_GUARD; partName = "Guard"; }
            case "Basic Part" -> { outputItem = ModItem.METAL_PLATE; partName = "Plate"; }
            default -> {
                message(player, "This cast's category is not supported by the current shaping recipes.");
                return true;
            }
        }

        ItemStack part = new ItemStack(outputItem);
        CustomData.set(DataComponents.CUSTOM_DATA, part, data.copy());
        part.set(DataComponents.CUSTOM_NAME, Component.literal(material + " " + partName));
        consume(player, castSlot, 1);
        give(player, part);
        message(player, partName + " shaped successfully.");
        return true;
    }

    public static boolean assembleWeapon(Player player) {
        int bladeSlot = find(player, ModItem.FORGED_BLADE);
        int handleSlot = find(player, ModItem.FORGED_HANDLE);
        int guardSlot = find(player, ModItem.FORGED_GUARD);
        if (bladeSlot < 0 || handleSlot < 0 || guardSlot < 0) {
            message(player, "Assembly requires one forged Blade, Handle, and Guard.");
            return true;
        }

        CompoundTag bladeData = customData(player.getInventory().getItem(bladeSlot));
        CompoundTag handleData = customData(player.getInventory().getItem(handleSlot));
        CompoundTag guardData = customData(player.getInventory().getItem(guardSlot));
        String bladeMaterial = stringTag(bladeData, "material");
        String handleMaterial = stringTag(handleData, "material");
        String guardMaterial = stringTag(guardData, "material");
        if (bladeMaterial.isBlank() || handleMaterial.isBlank() || guardMaterial.isBlank()) {
            message(player, "Every weapon component must have material data. Recreate any unconfigured parts.");
            return true;
        }

        ItemStack weapon = new ItemStack(ModItem.UNFINISHED_WEAPON);
        CompoundTag data = new CompoundTag();
        data.putString("material", bladeMaterial);
        data.put("blade", bladeData.copy());
        data.put("handle", handleData.copy());
        data.put("guard", guardData.copy());
        data.putString("eclipseashes_weapon_schema", "1");
        CustomData.set(DataComponents.CUSTOM_DATA, weapon, data);
        weapon.set(DataComponents.CUSTOM_NAME, Component.literal("Unfinished Custom Sword"));
        consume(player, bladeSlot, 1);
        consume(player, handleSlot, 1);
        consume(player, guardSlot, 1);
        give(player, weapon);
        message(player, "Weapon assembled. Mixed materials are supported. Take it to the Maintenance Station for finishing.");
        return true;
    }

    public static boolean finishWeapon(Player player) {
        int weaponSlot = find(player, ModItem.UNFINISHED_WEAPON);
        if (weaponSlot < 0) {
            message(player, "Bring an assembled unfinished weapon to finish it.");
            return true;
        }
        int stoneSlot = find(player, ModItem.SHARPENING_STONE);
        if (stoneSlot < 0) {
            message(player, "You need a Sharpening Stone to finish the weapon.");
            return true;
        }
        int waterSlot = find(player, Items.WATER_BUCKET);
        if (waterSlot < 0) {
            message(player, "You need a Water Bucket to quench the weapon before sharpening.");
            return true;
        }

        CompoundTag unfinishedData = customData(player.getInventory().getItem(weaponSlot));
        CompoundTag bladeData = unfinishedData.getCompound("blade").orElse(new CompoundTag());
        CompoundTag handleData = unfinishedData.getCompound("handle").orElse(new CompoundTag());
        CompoundTag guardData = unfinishedData.getCompound("guard").orElse(new CompoundTag());
        if (!"1".equals(stringTag(unfinishedData, "eclipseashes_weapon_schema"))
                || stringTag(bladeData, "material").isBlank()
                || stringTag(handleData, "material").isBlank()
                || stringTag(guardData, "material").isBlank()) {
            message(player, "This unfinished weapon is missing component data and cannot be finished. Assemble a new weapon from configured parts.");
            return true;
        }

        WeaponStatCalculator.WeaponStats stats =
                WeaponStatCalculator.calculate(bladeData, handleData, guardData);
        String bladeMaterial = stringTag(bladeData, "material");
        String handleMaterial = stringTag(handleData, "material");
        String guardMaterial = stringTag(guardData, "material");

        // Use a distinct Eclipse Ashes item so its enchantment compatibility is controlled by our tags.
        ItemStack sword = new ItemStack(ModItem.FORGED_WEAPON);
        sword.set(DataComponents.CUSTOM_NAME, Component.literal(
                "Forged " + bladeMaterial + " Blade / " + handleMaterial + " Handle / " + guardMaterial + " Guard"));
        sword.set(DataComponents.MAX_DAMAGE, stats.durability());
        sword.set(DataComponents.DAMAGE, 0);
        sword.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                // Player base attack damage is 1.0, so subtract that base to make
                // the resulting total equal the calculated weapon damage.
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Identifier.fromNamespaceAndPath("eclipseashes", "forged_weapon_damage"),
                                stats.damage() - 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                // Player base attack speed is 4.0; this modifier makes the final value
                // equal the calculated attacks-per-second stat.
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Identifier.fromNamespaceAndPath("eclipseashes", "forged_weapon_speed"),
                                stats.attackSpeed() - 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                // Entity interaction range is a real attribute, unlike the old display-only
                // reach value. Minecraft's default player range is 3.0 blocks.
                .add(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(Identifier.fromNamespaceAndPath("eclipseashes", "forged_weapon_reach"),
                                stats.reach() - 3.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                // Keep Minecraft's modifier amounts out of the tooltip. The custom
                // Eclipse Ashes tooltip shows the final calculated stats instead.
                .build().withTooltip(false));

        CompoundTag finishedData = new CompoundTag();
        finishedData.putString("eclipseashes_finished_weapon", "1");
        finishedData.putString("eclipseashes_weapon_schema", "1");
        finishedData.put("blade", bladeData.copy());
        finishedData.put("handle", handleData.copy());
        finishedData.put("guard", guardData.copy());
        finishedData.putString("blade_material", bladeMaterial);
        finishedData.putString("handle_material", handleMaterial);
        finishedData.putString("guard_material", guardMaterial);
        finishedData.putDouble("damage", stats.damage());
        finishedData.putInt("durability", stats.durability());
        finishedData.putDouble("attack_speed", stats.attackSpeed());
        finishedData.putDouble("reach", stats.reach());
        finishedData.putDouble("handling", stats.handling());
        CustomData.set(DataComponents.CUSTOM_DATA, sword, finishedData);

        consume(player, weaponSlot, 1);
        consume(player, stoneSlot, 1);
        consume(player, waterSlot, 1);
        give(player, new ItemStack(Items.BUCKET));
        give(player, sword);
        message(player, "Weapon finished. Damage: " + stats.damage()
                + ", durability: " + stats.durability()
                + ", attack speed: " + stats.attackSpeed()
                + ", calculated reach: " + stats.reach()
                + ", handling: " + stats.handling() + ".");
        return true;
    }

    private static int findHeatedMetal(Player player, String material) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.is(ModItem.HEATED_METAL)) continue;
            if (stringTag(customData(stack), "material").equals(material)) return slot;
        }
        return -1;
    }

    private static boolean forgeBlock(Player player) {
        if (find(player, ModItem.BLACKSMITH_HAMMER) < 0) {
            message(player, "You need a Blacksmith Hammer to forge metal blocks.");
            return true;
        }
        Item[] ingots = {
                Items.IRON_INGOT, Items.COPPER_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.NETHERITE_INGOT,
                ModItem.CRIMSON_IRON, ModItem.MOONSTEEL, ModItem.SUNSTEEL, ModItem.DRAGONITE,
                ModItem.VOID_CRYSTAL, ModItem.CELESTIAL_ALLOY, ModItem.ECLIPSE
        };
        Item[] blocks = {
                Blocks.IRON_BLOCK.asItem(), BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("minecraft", "copper_block")).orElseThrow().value(), Blocks.GOLD_BLOCK.asItem(), Blocks.DIAMOND_BLOCK.asItem(), Blocks.NETHERITE_BLOCK.asItem(),
                ModBlocks.CRIMSON_IRON_BLOCK.asItem(), ModBlocks.MOONSTEEL_BLOCK.asItem(),
                ModBlocks.SUNSTEEL_BLOCK.asItem(), ModBlocks.DRAGONITE_BLOCK.asItem(),
                ModBlocks.VOID_CRYSTAL_BLOCK.asItem(), ModBlocks.CELESTIAL_ALLOY_BLOCK.asItem(),
                ModBlocks.ECLIPSE_BLOCK.asItem()
        };
        for (int i = 0; i < ingots.length; i++) {
            if (count(player, ingots[i]) >= 9) {
                consumeMatching(player, ingots[i], 9);
                give(player, new ItemStack(blocks[i]));
                message(player, "Forged a metal block from nine matching ingots.");
                return true;
            }
        }
        message(player, "Bring a cast piece and Blacksmith Hammer, or nine matching metal ingots to forge a block.");
        return true;
    }

    private static int count(Player player, Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private static void consumeMatching(Player player, Item item, int amount) {
        for (int i = 0; i < player.getInventory().getContainerSize() && amount > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(item)) continue;
            int taken = Math.min(amount, stack.getCount());
            stack.shrink(taken);
            amount -= taken;
            if (stack.isEmpty()) player.getInventory().setItem(i, ItemStack.EMPTY);
        }
    }

    private static String stringTag(CompoundTag tag, String key) {
        return tag.getString(key).orElse("");
    }

    private static int find(Player player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) return i;
        }
        return -1;
    }

    private static CompoundTag customData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    private static void consume(Player player, int slot, int count) {
        ItemStack stack = player.getInventory().getItem(slot);
        stack.shrink(count);
        if (stack.isEmpty()) player.getInventory().setItem(slot, ItemStack.EMPTY);
    }

    private static void give(Player player, ItemStack stack) {
        // Preserve crafted results even when the player's inventory is full.
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            player.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
        }
    }

    private static void message(Player player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }
}
