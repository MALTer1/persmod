package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import com.eclipseashes.block.ModBlocks;
import com.eclipseashes.item.tool.ModTools;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import com.eclipseashes.block.ModWorkshopBlocks;

public class ModCreativeTab {

    public static final ResourceKey<CreativeModeTab> ECLIPSE_ASHES_TAB_KEY =
            ResourceKey.create(
                    BuiltInRegistries.CREATIVE_MODE_TAB.key(),
                    EclipseAshes.id("eclipse_ashes")
            );

    public static final CreativeModeTab ECLIPSE_ASHES_TAB =
            FabricCreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItem.CRIMSON_IRON))
                    .title(net.minecraft.network.chat.Component.translatable(
                            "creativeTab.eclipseashes"
                    ))
                    .displayItems((params, output) -> {

                        // Ores
                        output.accept(ModBlocks.CRIMSON_IRON_ORE);
                        output.accept(ModBlocks.MOONSTEEL_ORE);
                        output.accept(ModBlocks.SUNSTEEL_ORE);
                        output.accept(ModBlocks.DRAGONITE_ORE);
                        output.accept(ModBlocks.VOID_CRYSTAL_ORE);
                        output.accept(ModBlocks.CELESTIAL_ALLOY_ORE);
                        output.accept(ModBlocks.ECLIPSE_ORE);

                        // Raw materials
                        output.accept(ModItem.RAW_CRIMSON_IRON);
                        output.accept(ModItem.RAW_MOONSTEEL);
                        output.accept(ModItem.RAW_SUNSTEEL);
                        output.accept(ModItem.RAW_DRAGONITE);
                        output.accept(ModItem.RAW_VOID_CRYSTAL);
                        output.accept(ModItem.RAW_CELESTIAL_ALLOY);
                        output.accept(ModItem.RAW_ECLIPSE);

                        // Finished materials
                        output.accept(ModItem.CRIMSON_IRON);
                        output.accept(ModItem.MOONSTEEL);
                        output.accept(ModItem.SUNSTEEL);
                        output.accept(ModItem.DRAGONITE);
                        output.accept(ModItem.VOID_CRYSTAL);
                        output.accept(ModItem.CELESTIAL_ALLOY);
                        output.accept(ModItem.ECLIPSE);

                        // Storage blocks
                        output.accept(ModBlocks.CRIMSON_IRON_BLOCK);
                        output.accept(ModBlocks.MOONSTEEL_BLOCK);
                        output.accept(ModBlocks.SUNSTEEL_BLOCK);
                        output.accept(ModBlocks.DRAGONITE_BLOCK);
                        output.accept(ModBlocks.VOID_CRYSTAL_BLOCK);
                        output.accept(ModBlocks.CELESTIAL_ALLOY_BLOCK);
                        output.accept(ModBlocks.ECLIPSE_BLOCK);

                        // Crimson Iron tools
                        output.accept(ModTools.CRIMSON_IRON_PICKAXE);
                        output.accept(ModTools.CRIMSON_IRON_AXE);
                        output.accept(ModTools.CRIMSON_IRON_SHOVEL);
                        output.accept(ModTools.CRIMSON_IRON_HOE);

                        // Moonsteel tools
                        output.accept(ModTools.MOONSTEEL_PICKAXE);
                        output.accept(ModTools.MOONSTEEL_AXE);
                        output.accept(ModTools.MOONSTEEL_SHOVEL);
                        output.accept(ModTools.MOONSTEEL_HOE);

                        // Sunsteel tools
                        output.accept(ModTools.SUNSTEEL_PICKAXE);
                        output.accept(ModTools.SUNSTEEL_AXE);
                        output.accept(ModTools.SUNSTEEL_SHOVEL);
                        output.accept(ModTools.SUNSTEEL_HOE);

                        // Dragonite tools
                        output.accept(ModTools.DRAGONITE_PICKAXE);
                        output.accept(ModTools.DRAGONITE_AXE);
                        output.accept(ModTools.DRAGONITE_SHOVEL);
                        output.accept(ModTools.DRAGONITE_HOE);

                        // Void Crystal tools
                        output.accept(ModTools.VOID_CRYSTAL_PICKAXE);
                        output.accept(ModTools.VOID_CRYSTAL_AXE);
                        output.accept(ModTools.VOID_CRYSTAL_SHOVEL);
                        output.accept(ModTools.VOID_CRYSTAL_HOE);

                        // Celestial Alloy tools
                        output.accept(ModTools.CELESTIAL_ALLOY_PICKAXE);
                        output.accept(ModTools.CELESTIAL_ALLOY_AXE);
                        output.accept(ModTools.CELESTIAL_ALLOY_SHOVEL);
                        output.accept(ModTools.CELESTIAL_ALLOY_HOE);

                        // Eclipse tools
                        output.accept(ModTools.ECLIPSE_PICKAXE);
                        output.accept(ModTools.ECLIPSE_AXE);
                        output.accept(ModTools.ECLIPSE_SHOVEL);
                        output.accept(ModTools.ECLIPSE_HOE);
                        
                        // Workshop blocks
                        output.accept(ModWorkshopBlocks.FORGE_FLOOR);
                        output.accept(ModWorkshopBlocks.FORGE_SECTION);
                        output.accept(ModWorkshopBlocks.BASIC_WORKSHOP);
                        output.accept(ModWorkshopBlocks.METAL_FORGE);
                    })
                    .build();

    public static void initialize() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ECLIPSE_ASHES_TAB_KEY,
                ECLIPSE_ASHES_TAB
        );
    }
}