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

public class ModWorkshopBlocks {
    public static final ResourceKey<Block> FORGE_FLOOR_KEY = blockKey("forge_floor");
    public static final ResourceKey<Block> BASIC_WORKSHOP_KEY = blockKey("basic_workshop");
    public static final ResourceKey<Block> FORGE_SECTION_KEY = blockKey("forge_section");
    public static final ResourceKey<Block> METAL_FORGE_KEY = blockKey("metal_forge");
    public static final ResourceKey<Block> ADVANCED_FORGE_KEY = blockKey("advanced_forge");
    public static final ResourceKey<Block> PART_MAKER_KEY = blockKey("part_maker");
    public static final ResourceKey<Block> CASTING_STATION_KEY = blockKey("casting_station");
    public static final ResourceKey<Block> MODDED_ANVIL_KEY = blockKey("modded_anvil");
    public static final ResourceKey<Block> ASSEMBLY_STATION_KEY = blockKey("assembly_station");
    public static final ResourceKey<Block> MAINTENANCE_STATION_KEY = blockKey("maintenance_station");

    public static final Block FORGE_FLOOR = registerBlock(FORGE_FLOOR_KEY, Block.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final Block BASIC_WORKSHOP = Registry.register(BuiltInRegistries.BLOCK, BASIC_WORKSHOP_KEY,
            new BasicWorkshopBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(BASIC_WORKSHOP_KEY)));
    public static final Block FORGE_SECTION = Registry.register(BuiltInRegistries.BLOCK, FORGE_SECTION_KEY,
            new ForgeSectionBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(FORGE_SECTION_KEY)));
    public static final Block METAL_FORGE = Registry.register(BuiltInRegistries.BLOCK, METAL_FORGE_KEY,
            new MetalForgeBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(METAL_FORGE_KEY)));
    public static final Block ADVANCED_FORGE = Registry.register(BuiltInRegistries.BLOCK, ADVANCED_FORGE_KEY,
            new AdvancedForgeBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ADVANCED_FORGE_KEY)));
    public static final Block PART_MAKER = Registry.register(BuiltInRegistries.BLOCK, PART_MAKER_KEY,
            new PartMakerBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(PART_MAKER_KEY)));
    public static final Block CASTING_STATION = Registry.register(BuiltInRegistries.BLOCK, CASTING_STATION_KEY,
            new CastingStationBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(CASTING_STATION_KEY)));
    public static final Block MODDED_ANVIL = Registry.register(BuiltInRegistries.BLOCK, MODDED_ANVIL_KEY,
            new ModdedAnvilBlock(Block.Properties.ofFullCopy(Blocks.ANVIL).setId(MODDED_ANVIL_KEY)));
    public static final Block ASSEMBLY_STATION = Registry.register(BuiltInRegistries.BLOCK, ASSEMBLY_STATION_KEY,
            new AssemblyStationBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(ASSEMBLY_STATION_KEY)));
    public static final Block MAINTENANCE_STATION = Registry.register(BuiltInRegistries.BLOCK, MAINTENANCE_STATION_KEY,
            new MaintenanceStationBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).setId(MAINTENANCE_STATION_KEY)));

    static {
        registerBlockItem(FORGE_FLOOR, "forge_floor");
        registerBlockItem(BASIC_WORKSHOP, "basic_workshop");
        registerBlockItem(FORGE_SECTION, "forge_section");
        registerBlockItem(METAL_FORGE, "metal_forge");
        registerBlockItem(ADVANCED_FORGE, "advanced_forge");
        registerBlockItem(PART_MAKER, "part_maker");
        registerBlockItem(CASTING_STATION, "casting_station");
        registerBlockItem(MODDED_ANVIL, "modded_anvil");
        registerBlockItem(ASSEMBLY_STATION, "assembly_station");
        registerBlockItem(MAINTENANCE_STATION, "maintenance_station");
    }

    private static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, EclipseAshes.id(name));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, EclipseAshes.id(name));
    }

    private static Block registerBlock(ResourceKey<Block> key, Block.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, key, new Block(properties.setId(key)));
    }

    private static Item registerBlockItem(Block block, String name) {
        ResourceKey<Item> key = itemKey(name);
        return Registry.register(BuiltInRegistries.ITEM, key,
                new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix()));
    }

    public static void initialize() {
        // Loading this class registers the workshop blocks.
    }
}
