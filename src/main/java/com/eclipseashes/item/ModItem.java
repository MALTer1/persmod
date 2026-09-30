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

    public static final Item UNREFINED_CRIMSON_IRON =
        register("unrefined_crimson_iron");

    public static final Item REFINED_CRIMSON_IRON =
            register("refined_crimson_iron");

    public static final Item UNREFINED_MOONSTEEL =
            register("unrefined_moonsteel");

    public static final Item REFINED_MOONSTEEL =
            register("refined_moonsteel");

    public static final Item UNREFINED_SUNSTEEL =
            register("unrefined_sunsteel");

    public static final Item REFINED_SUNSTEEL =
            register("refined_sunsteel");

    private static Item register(String name) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                EclipseAshes.id(name)
        );

        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                new Item(
                        new Item.Properties().setId(key)
                )
        );
    }


    public static void initialize() {
        // Loading this class registers the items.
    }
}