package com.eclipseashes.block;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlagSet;

public class ModMenuTypes {

    public static final ResourceKey<MenuType<?>> METAL_FORGE_KEY =
            ResourceKey.create(
                    BuiltInRegistries.MENU.key(),
                    EclipseAshes.id("metal_forge")
            );

    public static final MenuType<MetalForgeMenu> METAL_FORGE =
            Registry.register(
                    BuiltInRegistries.MENU,
                    METAL_FORGE_KEY,
                    new MenuType<>(
                            MetalForgeMenu::new,
                            FeatureFlagSet.of()
                    )
            );

    public static void initialize() {
        // Loading this class registers the menus.
    }
}