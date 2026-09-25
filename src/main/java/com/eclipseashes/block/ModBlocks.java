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

    public static final ResourceKey<Block> CRIMSON_IRON_ORE_KEY = blockKey("crimson_iron_ore");
    public static final ResourceKey<Block> MOONSTEEL_ORE_KEY = blockKey("moonsteel_ore");
    public static final ResourceKey<Block> SUNSTEEL_ORE_KEY = blockKey("sunsteel_ore");
    public static final ResourceKey<Block> DRAGONITE_ORE_KEY = blockKey("dragonite_ore");
    public static final ResourceKey<Block> VOID_CRYSTAL_ORE_KEY = blockKey("void_crystal_ore");
    public static final ResourceKey<Block> CELESTIAL_ALLOY_ORE_KEY = blockKey("celestial_alloy_ore");
    public static final ResourceKey<Block> ECLIPSE_ORE_KEY = blockKey("eclipse_ore");

    public static final ResourceKey<Block> CRIMSON_IRON_BLOCK_KEY = blockKey("crimson_iron_block");
    public static final ResourceKey<Block> MOONSTEEL_BLOCK_KEY = blockKey("moonsteel_block");
    public static final ResourceKey<Block> SUNSTEEL_BLOCK_KEY = blockKey("sunsteel_block");
    public static final ResourceKey<Block> DRAGONITE_BLOCK_KEY = blockKey("dragonite_block");
    public static final ResourceKey<Block> VOID_CRYSTAL_BLOCK_KEY = blockKey("void_crystal_block");
    public static final ResourceKey<Block> CELESTIAL_ALLOY_BLOCK_KEY = blockKey("celestial_alloy_block");
    public static final ResourceKey<Block> ECLIPSE_BLOCK_KEY = blockKey("eclipse_block");

    public static final Block CRIMSON_IRON_ORE = registerOre(CRIMSON_IRON_ORE_KEY);
    public static final Block MOONSTEEL_ORE = registerOre(MOONSTEEL_ORE_KEY);
    public static final Block SUNSTEEL_ORE = registerOre(SUNSTEEL_ORE_KEY);
    public static final Block DRAGONITE_ORE = registerOre(DRAGONITE_ORE_KEY);
    public static final Block VOID_CRYSTAL_ORE = registerOre(VOID_CRYSTAL_ORE_KEY);
    public static final Block CELESTIAL_ALLOY_ORE = registerOre(CELESTIAL_ALLOY_ORE_KEY);
    public static final Block ECLIPSE_ORE = registerOre(ECLIPSE_ORE_KEY);

    public static final Block CRIMSON_IRON_BLOCK = registerBlock(
            CRIMSON_IRON_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block MOONSTEEL_BLOCK = registerBlock(
            MOONSTEEL_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block SUNSTEEL_BLOCK = registerBlock(
            SUNSTEEL_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block DRAGONITE_BLOCK = registerBlock(
            DRAGONITE_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block VOID_CRYSTAL_BLOCK = registerBlock(
            VOID_CRYSTAL_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block CELESTIAL_ALLOY_BLOCK = registerBlock(
            CELESTIAL_ALLOY_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    public static final Block ECLIPSE_BLOCK = registerBlock(
            ECLIPSE_BLOCK_KEY,
            Block.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    );

    static {
        registerBlockItem(CRIMSON_IRON_ORE, "crimson_iron_ore");
        registerBlockItem(MOONSTEEL_ORE, "moonsteel_ore");
        registerBlockItem(SUNSTEEL_ORE, "sunsteel_ore");
        registerBlockItem(DRAGONITE_ORE, "dragonite_ore");
        registerBlockItem(VOID_CRYSTAL_ORE, "void_crystal_ore");
        registerBlockItem(CELESTIAL_ALLOY_ORE, "celestial_alloy_ore");
        registerBlockItem(ECLIPSE_ORE, "eclipse_ore");

        registerBlockItem(CRIMSON_IRON_BLOCK, "crimson_iron_block");
        registerBlockItem(MOONSTEEL_BLOCK, "moonsteel_block");
        registerBlockItem(SUNSTEEL_BLOCK, "sunsteel_block");
        registerBlockItem(DRAGONITE_BLOCK, "dragonite_block");
        registerBlockItem(VOID_CRYSTAL_BLOCK, "void_crystal_block");
        registerBlockItem(CELESTIAL_ALLOY_BLOCK, "celestial_alloy_block");
        registerBlockItem(ECLIPSE_BLOCK, "eclipse_block");
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

    private static Block registerOre(ResourceKey<Block> key) {
        return registerBlock(
                key,
                Block.Properties.ofFullCopy(Blocks.IRON_ORE)
                        .requiresCorrectToolForDrops()
        );
    }

    private static Block registerBlock(
            ResourceKey<Block> key,
            Block.Properties properties
    ) {
        return Registry.register(
                BuiltInRegistries.BLOCK,
                key,
                new Block(
                        properties.setId(key)
                )
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
        // Loading this class registers the blocks.
    }
}