package com.eclipseashes.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class StationMenuProvider implements MenuProvider {

    private final MenuType<? extends AbstractContainerMenu> menuType;
    private final Component name;

    public StationMenuProvider(
            MenuType<? extends AbstractContainerMenu> menuType,
            Component name
    ) {
        this.menuType = menuType;
        this.name = name;
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
    ) {
        if (menuType == ModMenuTypes.PART_MAKER) {
            return new PartMakerMenu(containerId, inventory);
        }
        if (menuType == ModMenuTypes.ASSEMBLY_STATION) {
            return new AssemblyStationMenu(containerId, inventory);
        }
        return new MaintenanceStationMenu(containerId, inventory);
    }
}