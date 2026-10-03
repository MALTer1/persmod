package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class AssemblyStationBlock extends Block {

    public AssemblyStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
            net.minecraft.world.level.block.state.BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide()) {
            if (!WorkshopManager.isStationInWorkshop(level, pos)) {
                player.displayClientMessage(
                        Component.literal("Assembly Station must be placed in a workshop."),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            player.openMenu(new StationMenuProvider(
                    ModMenuTypes.ASSEMBLY_STATION,
                    Component.literal("Assembly Station")
            ));
        }

        return InteractionResult.SUCCESS;
    }
}