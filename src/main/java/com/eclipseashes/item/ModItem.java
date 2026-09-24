package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModItem {

    public static final Item RAW_CRIMSON_IRON = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("raw_crimson_iron"),
            new Item(new Item.Properties())
    );

    public static final Item UNREFINED_CRIMSON_IRON = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("unrefined_crimson_iron"),
            new Item(new Item.Properties())
    );

    public static final Item REFINED_CRIMSON_IRON = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("refined_crimson_iron"),
            new Item(new Item.Properties())
    );

    public static final Item CRIMSON_IRON_INGOT = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_ingot"),
            new Item(new Item.Properties())
    );

    public static final Item CRIMSON_IRON_BAR = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_bar"),
            new Item(new Item.Properties())
    );

    public static void initialize() {
        // Loading this class registers the items above.
    }
}