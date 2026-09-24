package com.eclipseashes.world;

import com.eclipseashes.EclipseAshes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModWorldGen {

    public static final ResourceKey<PlacedFeature> CRIMSON_IRON_ORE_PLACED =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    EclipseAshes.id("crimson_iron_ore")
            );

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(net.minecraft.tags.BiomeTags.IS_MOUNTAIN),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                CRIMSON_IRON_ORE_PLACED
        );
    }
}