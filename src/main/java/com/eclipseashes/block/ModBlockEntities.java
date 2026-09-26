package com.eclipseashes.block;

import com.eclipseashes.EclipseAshes;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final ResourceKey<BlockEntityType<?>> METAL_FORGE_KEY =
            ResourceKey.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    EclipseAshes.id("metal_forge")
            );

    public static final BlockEntityType<MetalForgeBlockEntity> METAL_FORGE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    METAL_FORGE_KEY,
                    FabricBlockEntityTypeBuilder
                            .<MetalForgeBlockEntity>create(
                                    MetalForgeBlockEntity::new,
                                    ModWorkshopBlocks.METAL_FORGE
                            )
                            .build()
            );

    public static void initialize() {
        // Loading this class registers the block entity.
    }
}