package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public class ModToolMaterials {

    public static final TagKey<Block> INCORRECT_FOR_CRIMSON_IRON =
            incorrectTag("incorrect_for_crimson_iron_tool");

    public static final TagKey<Block> INCORRECT_FOR_MOONSTEEL =
            incorrectTag("incorrect_for_moonsteel_tool");

    public static final TagKey<Block> INCORRECT_FOR_SUNSTEEL =
            incorrectTag("incorrect_for_sunsteel_tool");

    public static final TagKey<Block> INCORRECT_FOR_DRAGONITE =
            incorrectTag("incorrect_for_dragonite_tool");

    public static final TagKey<Block> INCORRECT_FOR_VOID_CRYSTAL =
            incorrectTag("incorrect_for_void_crystal_tool");

    public static final TagKey<Block> INCORRECT_FOR_CELESTIAL_ALLOY =
            incorrectTag("incorrect_for_celestial_alloy_tool");

    public static final TagKey<Block> INCORRECT_FOR_ECLIPSE =
            incorrectTag("incorrect_for_eclipse_tool");

    public static final ToolMaterial CRIMSON_IRON = new ToolMaterial(
            INCORRECT_FOR_CRIMSON_IRON,
            350,
            7.2F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial MOONSTEEL = new ToolMaterial(
            INCORRECT_FOR_MOONSTEEL,
            700,
            8.0F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial SUNSTEEL = new ToolMaterial(
            INCORRECT_FOR_SUNSTEEL,
            1100,
            10.0F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial DRAGONITE = new ToolMaterial(
            INCORRECT_FOR_DRAGONITE,
            1600,
            12.0F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial VOID_CRYSTAL = new ToolMaterial(
            INCORRECT_FOR_VOID_CRYSTAL,
            2200,
            14.0F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial CELESTIAL_ALLOY = new ToolMaterial(
            INCORRECT_FOR_CELESTIAL_ALLOY,
            2800,
            16.0F,
            0.0F,
            14,
            null
    );

    public static final ToolMaterial ECLIPSE = new ToolMaterial(
            INCORRECT_FOR_ECLIPSE,
            4000,
            24.0F,
            0.0F,
            14,
            null
    );

    private static TagKey<Block> incorrectTag(String name) {
        return TagKey.create(
                Registries.BLOCK,
                EclipseAshes.id(name)
        );
    }
}