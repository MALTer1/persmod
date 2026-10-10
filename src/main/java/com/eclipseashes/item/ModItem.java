package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItem {
    public static final Item CRIMSON_IRON = register("crimson_iron");
    public static final Item RAW_CRIMSON_IRON = register("raw_crimson_iron");
    public static final Item MOONSTEEL = register("moonsteel");
    public static final Item RAW_MOONSTEEL = register("raw_moonsteel");
    public static final Item SUNSTEEL = register("sunsteel");
    public static final Item RAW_SUNSTEEL = register("raw_sunsteel");
    public static final Item DRAGONITE = register("dragonite");
    public static final Item RAW_DRAGONITE = register("raw_dragonite");
    public static final Item VOID_CRYSTAL = register("void_crystal");
    public static final Item RAW_VOID_CRYSTAL = register("raw_void_crystal");
    public static final Item CELESTIAL_ALLOY = register("celestial_alloy");
    public static final Item RAW_CELESTIAL_ALLOY = register("raw_celestial_alloy");
    public static final Item ECLIPSE = register("eclipse");
    public static final Item RAW_ECLIPSE = register("raw_eclipse");

    public static final Item UNREFINED_CRIMSON_IRON = register("unrefined_crimson_iron");
    public static final Item REFINED_CRIMSON_IRON = register("refined_crimson_iron");
    public static final Item UNREFINED_MOONSTEEL = register("unrefined_moonsteel");
    public static final Item REFINED_MOONSTEEL = register("refined_moonsteel");
    public static final Item UNREFINED_SUNSTEEL = register("unrefined_sunsteel");
    public static final Item REFINED_SUNSTEEL = register("refined_sunsteel");
    public static final Item UNREFINED_DRAGONITE = register("unrefined_dragonite");
    public static final Item REFINED_DRAGONITE = register("refined_dragonite");
    public static final Item UNREFINED_VOID_CRYSTAL = register("unrefined_void_crystal");
    public static final Item REFINED_VOID_CRYSTAL = register("refined_void_crystal");
    public static final Item UNREFINED_CELESTIAL_ALLOY = register("unrefined_celestial_alloy");
    public static final Item REFINED_CELESTIAL_ALLOY = register("refined_celestial_alloy");
    public static final Item UNREFINED_ECLIPSE = register("unrefined_eclipse");
    public static final Item REFINED_ECLIPSE = register("refined_eclipse");

    // Configured mold metadata determines the metal and shape of a cast piece.
    public static final Item MOLD = register("mold");
    public static final Item CAST_METAL = register("cast_metal");
    public static final Item BLACKSMITH_HAMMER = register("blacksmith_hammer");
    public static final Item METAL_PLATE = register("metal_plate");
    public static final Item FORGED_BLADE = register("forged_blade");
    public static final Item FORGED_HANDLE = register("forged_handle");
    public static final Item FORGED_GUARD = register("forged_guard");
    public static final Item UNFINISHED_WEAPON = register("unfinished_weapon");
    public static final Item SHARPENING_STONE = register("sharpening_stone");

    private static Item register(String name) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, EclipseAshes.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key)));
    }

    public static void initialize() {
        // Loading this class registers the items.
    }
}
