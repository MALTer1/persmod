package com.eclipseashes.block;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModBlocks {

    public static final Block CRIMSON_IRON_ORE = Registry.register(
            BuiltInRegistries.BLOCK,
            EclipseAshes.id("crimson_iron_ore"),
            new Block(Block.Properties.ofFullCopy(Blocks.IRON_ORE))
    );

    public static final Block CRIMSON_IRON_BLOCK = Registry.register(
            BuiltInRegistries.BLOCK,
            EclipseAshes.id("crimson_iron_block"),
            new Block(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK))
    );

    public static final Item CRIMSON_IRON_BLOCK_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            EclipseAshes.id("crimson_iron_block"),
            new BlockItem(
                    CRIMSON_IRON_BLOCK,
                    new Item.Properties()
            )
    );

    public static void initialize() {
        // Loading this class registers the blocks above.
    }
}