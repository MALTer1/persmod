package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

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
        String material = moldData.getString("material");
        Item metal = findMetal(material);
        if (metal == null) {
            message(player, "This mold's material is not supported by the current casting recipes.");
            return true;
        }

        int metalSlot = find(player, metal);
        if (metalSlot < 0) {
            message(player, "You need one " + material + " ingot to fill this mold.");
            return true;
        }

        ItemStack cast = new ItemStack(ModItem.CAST_METAL);
        CustomData.set(DataComponents.CUSTOM_DATA, cast, CustomData.of(moldData.copy()));
        cast.set(DataComponents.CUSTOM_NAME, Component.literal(material + " Cast " + moldData.getString("category")));
        consume(player, metalSlot, 1);
        give(player, cast);
        message(player, "Cast complete. Take the cast piece to the Modded Anvil.");
        return true;
    }

    public static boolean shapeCast(Player player) {
        int castSlot = find(player, ModItem.CAST_METAL);
        if (castSlot < 0) {
            message(player, "Bring a cast piece to the Modded Anvil.");
            return true;
        }
        if (find(player, ModItem.BLACKSMITH_HAMMER) < 0) {
            message(player, "You need a Blacksmith Hammer in your inventory to shape metal.");
            return true;
        }

        ItemStack cast = player.getInventory().getItem(castSlot);
        CompoundTag data = customData(cast);
        String category = data.getString("category");
        String material = data.getString("material");
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
        CustomData.set(DataComponents.CUSTOM_DATA, part, CustomData.of(data.copy()));
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

        String bladeMaterial = customData(player.getInventory().getItem(bladeSlot)).getString("material");
        String handleMaterial = customData(player.getInventory().getItem(handleSlot)).getString("material");
        String guardMaterial = customData(player.getInventory().getItem(guardSlot)).getString("material");
        if (!bladeMaterial.equals(handleMaterial) || !bladeMaterial.equals(guardMaterial)) {
            message(player, "The Blade, Handle, and Guard must use the same metal.");
            return true;
        }

        ItemStack weapon = new ItemStack(ModItem.UNFINISHED_WEAPON);
        CompoundTag data = new CompoundTag();
        data.putString("material", bladeMaterial);
        CustomData.set(DataComponents.CUSTOM_DATA, weapon, CustomData.of(data));
        weapon.set(DataComponents.CUSTOM_NAME, Component.literal("Unfinished " + bladeMaterial + " Sword"));
        consume(player, bladeSlot, 1);
        consume(player, handleSlot, 1);
        consume(player, guardSlot, 1);
        give(player, weapon);
        message(player, "Weapon assembled. Take it to the Maintenance Station for finishing.");
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

        String material = customData(player.getInventory().getItem(weaponSlot)).getString("material");
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        sword.set(DataComponents.CUSTOM_NAME, Component.literal("Forged " + material + " Sword"));
        CompoundTag data = new CompoundTag();
        data.putString("material", material);
        data.putString("eclipseashes_finished_weapon", "1");
        CustomData.set(DataComponents.CUSTOM_DATA, sword, CustomData.of(data));
        consume(player, weaponSlot, 1);
        consume(player, stoneSlot, 1);
        give(player, sword);
        message(player, "Quenching and sharpening complete. Your sword is ready to use.");
        return true;
    }

    private static Item findMetal(String material) {
        if (material.equals("Iron Ingot")) return Items.IRON_INGOT;
        if (material.equals("Copper Ingot")) return Items.COPPER_INGOT;
        if (material.equals("Gold Ingot")) return Items.GOLD_INGOT;
        if (material.equals("Crimson Iron")) return ModItem.CRIMSON_IRON;
        if (material.equals("Moonsteel")) return ModItem.MOONSTEEL;
        if (material.equals("Sunsteel")) return ModItem.SUNSTEEL;
        if (material.equals("Dragonite")) return ModItem.DRAGONITE;
        if (material.equals("Void Crystal")) return ModItem.VOID_CRYSTAL;
        if (material.equals("Celestial Alloy")) return ModItem.CELESTIAL_ALLOY;
        if (material.equals("Eclipse")) return ModItem.ECLIPSE;
        return null;
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
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    private static void message(Player player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }
}
