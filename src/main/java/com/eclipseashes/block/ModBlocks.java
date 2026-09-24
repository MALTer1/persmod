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

public class ModBlocks {

    public static final ResourceKey<Block> CRIMSON_IRON_ORE_KEY =
            ResourceKey.create(
                    Registries.BLOCK,
                    EclipseAshes.id("crimson_iron_ore")
            );

    public static final ResourceKey<Block> CRIMSON_IRON_BLOCK_KEY =
            ResourceKey.create(
                    Registries.BLOCK,
                    EclipseAshes.id("crimson_iron_block")
            );

    public static final ResourceKey<Item> CRIMSON_IRON_ORE_ITEM_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_ore")
            );

    public static final ResourceKey<Item> CRIMSON_IRON_BLOCK_ITEM_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    EclipseAshes.id("crimson_iron_block")
            );

    public static final Block CRIMSON_IRON_ORE = Registry.register(
            BuiltInRegistries.BLOCK,
            CRIMSON_IRON_ORE_KEY,
            new Block(
                    Block.Properties.ofFullCopy(Blocks.IRON_ORE)
                            .setId(CRIMSON_IRON_ORE_KEY)
            )
    );

    public static final Block CRIMSON_IRON_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK,
            CRIMSON_IRON_BLOCK_KEY,
            new Block(
                    Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                            .setId(CRIMSON_IRON_BLOCK_KEY)
            )
    );

    public static final Item CRIMSON_IRON_ORE_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            CRIMSON_IRON_ORE_ITEM_KEY,
            new BlockItem(
                    CRIMSON_IRON_ORE,
                    new Item.Properties()
                            .setId(CRIMSON_IRON_ORE_ITEM_KEY)
                            .useBlockDescriptionPrefix()
            )
    );

    public static final Item CRIMSON_IRON_BLOCK_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            CRIMSON_IRON_BLOCK_ITEM_KEY,
            new BlockItem(
                    CRIMSON_IRON_BLOCK,
                    new Item.Properties()
                            .setId(CRIMSON_IRON_BLOCK_ITEM_KEY)
                            .useBlockDescriptionPrefix()
            )
    );

    public static void initialize() {
        // Loading this class registers the blocks.
    }
}