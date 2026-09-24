package com.eclipseashes.item.tool;

import com.eclipseashes.EclipseAshes;
import com.eclipseashes.item.ModToolMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ModTools {

    public static final Item CRIMSON_IRON_PICKAXE = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_pickaxe"),
            new Item(
                    new Item.Properties().pickaxe(
                            ModToolMaterials.CRIMSON_IRON,
                            1.0F,
                            -2.8F
                    )
            )
    );

    public static final Item CRIMSON_IRON_AXE = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_axe"),
            new Item(
                    new Item.Properties().axe(
                            ModToolMaterials.CRIMSON_IRON,
                            5.0F,
                            -3.0F
                    )
            )
    );

    public static final Item CRIMSON_IRON_SHOVEL = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_shovel"),
            new Item(
                    new Item.Properties().shovel(
                            ModToolMaterials.CRIMSON_IRON,
                            1.5F,
                            -3.0F
                    )
            )
    );

    public static final Item CRIMSON_IRON_HOE = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_hoe"),
            new Item(
                    new Item.Properties().hoe(
                            ModToolMaterials.CRIMSON_IRON,
                            0.0F,
                            -3.0F
                    )
            )
    );

    public static void initialize() {
        // Loading this class registers the tools above.
    }
}