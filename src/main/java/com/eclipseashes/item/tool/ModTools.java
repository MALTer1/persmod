package com.eclipseashes.item.tool;

import com.eclipseashes.EclipseAshes;
import com.eclipseashes.item.ModToolMaterials;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModTools {

    public static final Item CRIMSON_IRON_PICKAXE = registerPickaxe("crimson_iron_pickaxe", ModToolMaterials.CRIMSON_IRON);
    public static final Item CRIMSON_IRON_AXE = registerAxe("crimson_iron_axe", ModToolMaterials.CRIMSON_IRON);
    public static final Item CRIMSON_IRON_SHOVEL = registerShovel("crimson_iron_shovel", ModToolMaterials.CRIMSON_IRON);
    public static final Item CRIMSON_IRON_HOE = registerHoe("crimson_iron_hoe", ModToolMaterials.CRIMSON_IRON);

    public static final Item MOONSTEEL_PICKAXE = registerPickaxe("moonsteel_pickaxe", ModToolMaterials.MOONSTEEL);
    public static final Item MOONSTEEL_AXE = registerAxe("moonsteel_axe", ModToolMaterials.MOONSTEEL);
    public static final Item MOONSTEEL_SHOVEL = registerShovel("moonsteel_shovel", ModToolMaterials.MOONSTEEL);
    public static final Item MOONSTEEL_HOE = registerHoe("moonsteel_hoe", ModToolMaterials.MOONSTEEL);

    public static final Item SUNSTEEL_PICKAXE = registerPickaxe("sunsteel_pickaxe", ModToolMaterials.SUNSTEEL);
    public static final Item SUNSTEEL_AXE = registerAxe("sunsteel_axe", ModToolMaterials.SUNSTEEL);
    public static final Item SUNSTEEL_SHOVEL = registerShovel("sunsteel_shovel", ModToolMaterials.SUNSTEEL);
    public static final Item SUNSTEEL_HOE = registerHoe("sunsteel_hoe", ModToolMaterials.SUNSTEEL);

    public static final Item DRAGONITE_PICKAXE = registerPickaxe("dragonite_pickaxe", ModToolMaterials.DRAGONITE);
    public static final Item DRAGONITE_AXE = registerAxe("dragonite_axe", ModToolMaterials.DRAGONITE);
    public static final Item DRAGONITE_SHOVEL = registerShovel("dragonite_shovel", ModToolMaterials.DRAGONITE);
    public static final Item DRAGONITE_HOE = registerHoe("dragonite_hoe", ModToolMaterials.DRAGONITE);

    public static final Item VOID_CRYSTAL_PICKAXE = registerPickaxe("void_crystal_pickaxe", ModToolMaterials.VOID_CRYSTAL);
    public static final Item VOID_CRYSTAL_AXE = registerAxe("void_crystal_axe", ModToolMaterials.VOID_CRYSTAL);
    public static final Item VOID_CRYSTAL_SHOVEL = registerShovel("void_crystal_shovel", ModToolMaterials.VOID_CRYSTAL);
    public static final Item VOID_CRYSTAL_HOE = registerHoe("void_crystal_hoe", ModToolMaterials.VOID_CRYSTAL);

    public static final Item CELESTIAL_ALLOY_PICKAXE = registerPickaxe("celestial_alloy_pickaxe", ModToolMaterials.CELESTIAL_ALLOY);
    public static final Item CELESTIAL_ALLOY_AXE = registerAxe("celestial_alloy_axe", ModToolMaterials.CELESTIAL_ALLOY);
    public static final Item CELESTIAL_ALLOY_SHOVEL = registerShovel("celestial_alloy_shovel", ModToolMaterials.CELESTIAL_ALLOY);
    public static final Item CELESTIAL_ALLOY_HOE = registerHoe("celestial_alloy_hoe", ModToolMaterials.CELESTIAL_ALLOY);

    public static final Item ECLIPSE_PICKAXE = registerPickaxe("eclipse_pickaxe", ModToolMaterials.ECLIPSE);
    public static final Item ECLIPSE_AXE = registerAxe("eclipse_axe", ModToolMaterials.ECLIPSE);
    public static final Item ECLIPSE_SHOVEL = registerShovel("eclipse_shovel", ModToolMaterials.ECLIPSE);
    public static final Item ECLIPSE_HOE = registerHoe("eclipse_hoe", ModToolMaterials.ECLIPSE);

    private static Item registerPickaxe(String name, net.minecraft.world.item.ToolMaterial material) {
        return register(
                name,
                properties -> properties.pickaxe(material, 1.0F, -2.8F)
        );
    }

    private static Item registerAxe(String name, net.minecraft.world.item.ToolMaterial material) {
        return register(
                name,
                properties -> properties.axe(material, 5.0F, -3.0F)
        );
    }

    private static Item registerShovel(String name, net.minecraft.world.item.ToolMaterial material) {
        return register(
                name,
                properties -> properties.shovel(material, 1.5F, -3.0F)
        );
    }

    private static Item registerHoe(String name, net.minecraft.world.item.ToolMaterial material) {
        return register(
                name,
                properties -> properties.hoe(material, 0.0F, -3.0F)
        );
    }

    private static Item register(
            String name,
            Function<Item.Properties, Item.Properties> factory
    ) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                EclipseAshes.id(name)
        );

        Item.Properties properties = factory.apply(
                new Item.Properties().setId(key)
        );

        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                new Item(properties)
        );
    }

    public static void initialize() {
        // Loading this class registers the tools.
    }
}