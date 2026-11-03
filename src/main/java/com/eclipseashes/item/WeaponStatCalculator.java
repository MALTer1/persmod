package com.eclipseashes.item;

import net.minecraft.nbt.CompoundTag;

/**
 * Central, deterministic weapon-stat calculations for the first weapon-system pass.
 *
 * Iron is the 1.00x reference. These starting values are balance data, not claims
 * about real-world metallurgy; they can be tuned after in-game playtests.
 */
public final class WeaponStatCalculator {
    private WeaponStatCalculator() {}

    public record WeaponStats(double damage, int durability, double attackSpeed,
                              double reach, double handling) {}

    private record MaterialProfile(double hardness, double toughness,
                                   double weight, double workability) {}

    public static WeaponStats calculate(CompoundTag blade, CompoundTag handle, CompoundTag guard) {
        MaterialProfile bladeMaterial = material(stringTag(blade, "material"));
        MaterialProfile handleMaterial = material(stringTag(handle, "material"));
        MaterialProfile guardMaterial = material(stringTag(guard, "material"));

        String length = stringTag(blade, "length");
        String shape = stringTag(blade, "shape");
        String width = stringTag(blade, "width");

        double damageDesign = 1.0;
        if ("Long".equals(length)) damageDesign *= 1.05;
        if ("Short".equals(length)) damageDesign *= 0.96;
        if ("Curved".equals(shape)) damageDesign *= 0.96;
        if ("Wide".equals(width)) damageDesign *= 1.08;
        if ("Narrow".equals(width)) damageDesign *= 0.95;
        double damage = 5.0 * bladeMaterial.hardness() * damageDesign;

        double durabilityDesign = "Wide".equals(width) ? 1.05 : 1.0;
        if ("Long".equals(length)) durabilityDesign *= 0.95;
        double combinedToughness = bladeMaterial.toughness() * 0.45
                + handleMaterial.toughness() * 0.35
                + guardMaterial.toughness() * 0.20;
        int durability = (int) Math.round(250.0 * combinedToughness * durabilityDesign);
        durability = Math.max(32, Math.min(4000, durability));

        double attackSpeed = 1.6;
        if ("Long".equals(length)) attackSpeed -= 0.15;
        if ("Short".equals(length)) attackSpeed += 0.20;
        if ("Wide".equals(width)) attackSpeed -= 0.10;
        if ("Narrow".equals(width)) attackSpeed += 0.05;
        if ("Curved".equals(shape)) attackSpeed += 0.10;
        attackSpeed = clamp(attackSpeed, 0.8, 2.4);

        double reach = 3.0;
        if ("Long".equals(length)) reach += 0.35;
        if ("Short".equals(length)) reach -= 0.25;
        if ("Curved".equals(shape)) reach += 0.10;
        if ("Wide".equals(width)) reach -= 0.05;

        double combinedWeight = bladeMaterial.weight() * 0.50
                + handleMaterial.weight() * 0.30
                + guardMaterial.weight() * 0.20;
        double handling = clamp(1.0 / combinedWeight, 0.65, 1.35);
        if ("Long".equals(length)) handling *= 0.94;
        if ("Short".equals(length)) handling *= 1.06;
        if ("Wide".equals(width)) handling *= 0.94;
        if ("Curved".equals(shape)) handling *= 1.03;
        handling = clamp(handling, 0.60, 1.40);

        return new WeaponStats(round2(damage), durability, round2(attackSpeed),
                round2(reach), round2(handling));
    }

    /** Returns the material profile relative to iron's 1.00x baseline. */
    public static double[] materialMultipliers(String materialName) {
        MaterialProfile p = material(materialName);
        return new double[] { p.hardness(), p.toughness(), p.weight(), p.workability() };
    }

    private static MaterialProfile material(String name) {
        return switch (name) {
            case "Copper" -> new MaterialProfile(0.80, 0.75, 0.90, 1.20);
            case "Iron" -> new MaterialProfile(1.00, 1.00, 1.00, 1.00);
            case "Gold" -> new MaterialProfile(0.65, 0.55, 0.70, 1.50);
            case "Diamond" -> new MaterialProfile(1.50, 1.00, 0.80, 0.35);
            case "Netherite" -> new MaterialProfile(1.45, 1.65, 1.25, 0.45);
            case "Crimson Iron" -> new MaterialProfile(1.10, 1.15, 1.00, 0.90);
            case "Moonsteel" -> new MaterialProfile(1.25, 1.10, 0.85, 0.80);
            case "Sunsteel" -> new MaterialProfile(1.35, 1.00, 0.90, 0.70);
            case "Dragonite" -> new MaterialProfile(1.40, 1.35, 1.10, 0.55);
            case "Void Crystal" -> new MaterialProfile(1.50, 0.90, 0.70, 0.40);
            case "Celestial Alloy" -> new MaterialProfile(1.55, 1.50, 1.00, 0.35);
            case "Eclipse" -> new MaterialProfile(1.80, 1.80, 0.90, 0.25);
            default -> new MaterialProfile(1.00, 1.00, 1.00, 1.00);
        };
    }

    private static String stringTag(CompoundTag tag, String key) {
        return tag.getString(key).orElse("");
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
