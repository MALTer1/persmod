package com.eclipseashes.item.tool;

import com.eclipseashes.EclipseAshes;
import com.eclipseashes.item.ModToolMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModTools {

    public static final ResourceKey<Item> CRIMSON_IRON_PICKAXE_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_pickaxe")
            );

    public static final ResourceKey<Item> CRIMSON_IRON_AXE_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_axe")
            );

    public static final ResourceKey<Item> CRIMSON_IRON_SHOVEL_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_shovel")
            );

    public static final ResourceKey<Item> CRIMSON_IRON_HOE_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_hoe")
            );

    public static final Item CRIMSON_IRON_PICKAXE = register(
            CRIMSON_IRON_PICKAXE_KEY,
            properties -> properties.pickaxe(
                    ModToolMaterials.CRIMSON_IRON,
                    1.0F,
                    -2.8F
            )
    );

    public static final Item CRIMSON_IRON_AXE = register(
            CRIMSON_IRON_AXE_KEY,
            properties -> properties.axe(
                    ModToolMaterials.CRIMSON_IRON,
                    5.0F,
                    -3.0F
            )
    );

    public static final Item CRIMSON_IRON_SHOVEL = register(
            CRIMSON_IRON_SHOVEL_KEY,
            properties -> properties.shovel(
                    ModToolMaterials.CRIMSON_IRON,
                    1.5F,
                    -3.0F
            )
    );

    public static final Item CRIMSON_IRON_HOE = register(
            CRIMSON_IRON_HOE_KEY,
            properties -> properties.hoe(
                    ModToolMaterials.CRIMSON_IRON,
                    0.0F,
                    -3.0F
            )
    );

    private static Item register(
            ResourceKey<Item> key,
            java.util.function.Function<Item.Properties, Item.Properties> factory
    ) {
        Item.Properties properties = factory.apply(
                new Item.Properties().setId(key)
        );

        Item item = new Item(properties);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                item
        );
    }

    public static void initialize() {
        // Loading this class registers the tools.
    }
}