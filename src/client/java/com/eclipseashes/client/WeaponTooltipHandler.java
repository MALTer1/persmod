package com.eclipseashes.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Shows the calculated properties stored on finished Eclipse Ashes weapons.
 * These are informational values; reach and handling are not combat mechanics yet.
 */
public final class WeaponTooltipHandler {
    private WeaponTooltipHandler() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return;
            }

            CompoundTag data = customData.copyTag();
            if (!"1".equals(data.getString("eclipseashes_finished_weapon").orElse(""))) {
                return;
            }

            lines.add(Component.literal("Forged Weapon"));
            lines.add(Component.literal("Damage: " + format(data.getDouble("damage").orElse(0.0))));
            lines.add(Component.literal("Attack Speed: " + format(data.getDouble("attack_speed").orElse(0.0)) + " attacks/sec"));
            lines.add(Component.literal("Durability: " + data.getInt("durability").orElse(0)));
            lines.add(Component.literal("Entity Reach: " + format(data.getDouble("reach").orElse(0.0)) + " blocks"));
            lines.add(Component.literal("Handling: " + format(data.getDouble("handling").orElse(1.0)) + " (display only for now)"));
            lines.add(Component.literal("Blade: " + data.getString("blade_material").orElse("Unknown")));
            lines.add(Component.literal("Handle: " + data.getString("handle_material").orElse("Unknown")));
            lines.add(Component.literal("Guard: " + data.getString("guard_material").orElse("Unknown")));
        });
    }

    private static String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
