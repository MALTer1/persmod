package com.eclipseashes.item;

import com.eclipseashes.EclipseAshes;
import com.eclipseashes.block.ModBlocks;
import com.eclipseashes.item.tool.ModTools;
import com.eclipseashes.block.ModWorkshopBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;

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
                        output.accept(ModBlocks.CRIMSON_IRON_ORE);
                        output.accept(ModBlocks.MOONSTEEL_ORE);
                        output.accept(ModBlocks.SUNSTEEL_ORE);
                        output.accept(ModBlocks.DRAGONITE_ORE);
                        output.accept(ModBlocks.VOID_CRYSTAL_ORE);
                        output.accept(ModBlocks.CELESTIAL_ALLOY_ORE);
                        output.accept(ModBlocks.ECLIPSE_ORE);

                        output.accept(ModItem.RAW_CRIMSON_IRON);
                        output.accept(ModItem.RAW_MOONSTEEL);
                        output.accept(ModItem.RAW_SUNSTEEL);
                        output.accept(ModItem.RAW_DRAGONITE);
                        output.accept(ModItem.RAW_VOID_CRYSTAL);
                        output.accept(ModItem.RAW_CELESTIAL_ALLOY);
                        output.accept(ModItem.RAW_ECLIPSE);

                        output.accept(ModItem.UNREFINED_CRIMSON_IRON);
                        output.accept(ModItem.REFINED_CRIMSON_IRON);
                        output.accept(ModItem.UNREFINED_MOONSTEEL);
                        output.accept(ModItem.REFINED_MOONSTEEL);
                        output.accept(ModItem.UNREFINED_SUNSTEEL);
                        output.accept(ModItem.REFINED_SUNSTEEL);
                        output.accept(ModItem.UNREFINED_DRAGONITE);
                        output.accept(ModItem.REFINED_DRAGONITE);
                        output.accept(ModItem.UNREFINED_VOID_CRYSTAL);
                        output.accept(ModItem.REFINED_VOID_CRYSTAL);
                        output.accept(ModItem.UNREFINED_CELESTIAL_ALLOY);
                        output.accept(ModItem.REFINED_CELESTIAL_ALLOY);
                        output.accept(ModItem.UNREFINED_ECLIPSE);
                        output.accept(ModItem.REFINED_ECLIPSE);

                        output.accept(ModItem.CRIMSON_IRON);
                        output.accept(ModItem.MOONSTEEL);
                        output.accept(ModItem.SUNSTEEL);
                        output.accept(ModItem.DRAGONITE);
                        output.accept(ModItem.VOID_CRYSTAL);
                        output.accept(ModItem.CELESTIAL_ALLOY);
                        output.accept(ModItem.ECLIPSE);
                        output.accept(ModItem.MOLD);

                        output.accept(ModBlocks.CRIMSON_IRON_BLOCK);
                        output.accept(ModBlocks.MOONSTEEL_BLOCK);
                        output.accept(ModBlocks.SUNSTEEL_BLOCK);
                        output.accept(ModBlocks.DRAGONITE_BLOCK);
                        output.accept(ModBlocks.VOID_CRYSTAL_BLOCK);
                        output.accept(ModBlocks.CELESTIAL_ALLOY_BLOCK);
                        output.accept(ModBlocks.ECLIPSE_BLOCK);

                        output.accept(ModTools.CRIMSON_IRON_PICKAXE);
                        output.accept(ModTools.CRIMSON_IRON_AXE);
                        output.accept(ModTools.CRIMSON_IRON_SHOVEL);
                        output.accept(ModTools.CRIMSON_IRON_HOE);
                        output.accept(ModTools.MOONSTEEL_PICKAXE);
                        output.accept(ModTools.MOONSTEEL_AXE);
                        output.accept(ModTools.MOONSTEEL_SHOVEL);
                        output.accept(ModTools.MOONSTEEL_HOE);
                        output.accept(ModTools.SUNSTEEL_PICKAXE);
                        output.accept(ModTools.SUNSTEEL_AXE);
                        output.accept(ModTools.SUNSTEEL_SHOVEL);
                        output.accept(ModTools.SUNSTEEL_HOE);
                        output.accept(ModTools.DRAGONITE_PICKAXE);
                        output.accept(ModTools.DRAGONITE_AXE);
                        output.accept(ModTools.DRAGONITE_SHOVEL);
                        output.accept(ModTools.DRAGONITE_HOE);
                        output.accept(ModTools.VOID_CRYSTAL_PICKAXE);
                        output.accept(ModTools.VOID_CRYSTAL_AXE);
                        output.accept(ModTools.VOID_CRYSTAL_SHOVEL);
                        output.accept(ModTools.VOID_CRYSTAL_HOE);
                        output.accept(ModTools.CELESTIAL_ALLOY_PICKAXE);
                        output.accept(ModTools.CELESTIAL_ALLOY_AXE);
                        output.accept(ModTools.CELESTIAL_ALLOY_SHOVEL);
                        output.accept(ModTools.CELESTIAL_ALLOY_HOE);
                        output.accept(ModTools.ECLIPSE_PICKAXE);
                        output.accept(ModTools.ECLIPSE_AXE);
                        output.accept(ModTools.ECLIPSE_SHOVEL);
                        output.accept(ModTools.ECLIPSE_HOE);

                        output.accept(ModWorkshopBlocks.FORGE_FLOOR);
                        output.accept(ModWorkshopBlocks.FORGE_SECTION);
                        output.accept(ModWorkshopBlocks.BASIC_WORKSHOP);
                        output.accept(ModWorkshopBlocks.METAL_FORGE);
                        output.accept(ModWorkshopBlocks.ADVANCED_FORGE);
                        output.accept(ModWorkshopBlocks.PART_MAKER);
                        output.accept(ModWorkshopBlocks.ASSEMBLY_STATION);
                        output.accept(ModWorkshopBlocks.MAINTENANCE_STATION);
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
