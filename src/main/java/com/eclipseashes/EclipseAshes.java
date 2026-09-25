package com.eclipseashes;

import com.eclipseashes.block.ModBlocks;
import com.eclipseashes.item.ModItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eclipseashes.item.tool.ModTools;
import com.eclipseashes.world.ModWorldGen;
import com.eclipseashes.item.ModCreativeTab;

public class EclipseAshes implements ModInitializer {

    public static final String MOD_ID = "eclipseashes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ModItem.initialize();
        ModBlocks.initialize();
        ModTools.initialize();
        ModCreativeTab.initialize();
        //ModWorldGen.initialize();
        //ToolPerks.initialize();

        LOGGER.info("Eclipse Ashes initialized.");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}