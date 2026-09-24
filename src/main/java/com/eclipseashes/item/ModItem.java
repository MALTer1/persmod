package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItem {

    public static final ResourceKey<Item> RAW_CRIMSON_IRON_KEY =
            ResourceKey.create(Registries.ITEM, EclipseAshes.id("raw_crimson_iron"));

    public static final ResourceKey<Item> UNREFINED_CRIMSON_IRON_KEY =
            ResourceKey.create(Registries.ITEM, EclipseAshes.id("unrefined_crimson_iron"));

    public static final ResourceKey<Item> REFINED_CRIMSON_IRON_KEY =
            ResourceKey.create(Registries.ITEM, EclipseAshes.id("refined_crimson_iron"));

    public static final ResourceKey<Item> CRIMSON_IRON_INGOT_KEY =
            ResourceKey.create(Registries.ITEM, EclipseAshes.id("crimson_iron_ingot"));

    public static final ResourceKey<Item> CRIMSON_IRON_BAR_KEY =
            ResourceKey.create(Registries.ITEM, EclipseAshes.id("crimson_iron_bar"));

    public static final Item RAW_CRIMSON_IRON = register(
            RAW_CRIMSON_IRON_KEY
    );

    public static final Item UNREFINED_CRIMSON_IRON = register(
            UNREFINED_CRIMSON_IRON_KEY
    );

    public static final Item REFINED_CRIMSON_IRON = register(
            REFINED_CRIMSON_IRON_KEY
    );

    public static final Item CRIMSON_IRON_INGOT = register(
            CRIMSON_IRON_INGOT_KEY
    );

    public static final Item CRIMSON_IRON_BAR = register(
            CRIMSON_IRON_BAR_KEY
    );

    private static Item register(ResourceKey<Item> key) {
        Item item = new Item(
                new Item.Properties().setId(key)
        );

        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                item
        );
    }

    public static void initialize() {
        // Loading this class registers the items.
    }
}