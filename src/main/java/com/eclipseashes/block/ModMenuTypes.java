package com.eclipseashes.block;

import com.eclipseashes.EclipseAshes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    // -----------------------------
    // Metal Forge
    // -----------------------------

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


    // -----------------------------
    // Basic Workshop
    // -----------------------------

    public static final ResourceKey<MenuType<?>> BASIC_WORKSHOP_KEY =
            ResourceKey.create(
                    BuiltInRegistries.MENU.key(),
                    EclipseAshes.id("basic_workshop")
            );

    public static final MenuType<BasicWorkshopMenu> BASIC_WORKSHOP =
            Registry.register(
                    BuiltInRegistries.MENU,
                    BASIC_WORKSHOP_KEY,
                    new MenuType<>(
                            BasicWorkshopMenu::new,
                            FeatureFlagSet.of()
                    )
            );


    // -----------------------------
    // Initialization
    // -----------------------------

    public static void initialize() {
        // Loading this class registers the menus.
    }
}