package com.eclipseashes.block;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.eclipseashes.block.MetalForgeBlock;

public class ModWorkshopBlocks {

    public static final ResourceKey<Block> FORGE_FLOOR_KEY = blockKey("forge_floor");
    public static final ResourceKey<Block> BASIC_WORKSHOP_KEY = blockKey("basic_workshop");

    public static final ResourceKey<Block> FORGE_SECTION_KEY = blockKey("forge_section");
    public static final ResourceKey<Block> METAL_FORGE_KEY = blockKey("metal_forge");

    public static final Block FORGE_FLOOR = registerBlock(
            FORGE_FLOOR_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block BASIC_WORKSHOP = Registry.register(
        BuiltInRegistries.BLOCK,
        BASIC_WORKSHOP_KEY,
        new BasicWorkshopBlock(
                Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .setId(BASIC_WORKSHOP_KEY)
        )
    );

    public static final Block FORGE_SECTION = registerBlock(
        FORGE_SECTION_KEY,
        Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block METAL_FORGE = Registry.register(
        BuiltInRegistries.BLOCK,
        METAL_FORGE_KEY,
        new MetalForgeBlock(
                Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .setId(METAL_FORGE_KEY)
        )
);

    static {
        registerBlockItem(FORGE_FLOOR, "forge_floor");
        registerBlockItem(BASIC_WORKSHOP, "basic_workshop");
        registerBlockItem(FORGE_SECTION, "forge_section");
        registerBlockItem(METAL_FORGE, "metal_forge");
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(
                Registries.BLOCK,
                EclipseAshes.id(name)
        );
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(
                Registries.ITEM,
                EclipseAshes.id(name)
        );
    }

    private static Block registerBlock(
            ResourceKey<Block> key,
            Block.Properties properties
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK,
                key,
                new Block(properties.setId(key))
        );
    }

    private static Item registerBlockItem(Block block, String name) {
        ResourceKey<Item> key = itemKey(name);

        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                new BlockItem(
                        block,
                        new Item.Properties()
                                .setId(key)
                                .useBlockDescriptionPrefix()
                )
        );
    }

    public static void initialize() {
        // Loading this class registers the workshop blocks.
    }
}