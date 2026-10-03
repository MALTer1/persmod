package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class PartMakerBlock extends Block {

    public PartMakerBlock(Properties properties) {
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
                player.sendSystemMessage(
                        Component.literal("Part Maker must be placed in a workshop.")
                );
                return InteractionResult.SUCCESS;
            }

            player.openMenu(new StationMenuProvider(
                    ModMenuTypes.PART_MAKER,
                    Component.literal("Part Maker")
            ));
        }

        return InteractionResult.SUCCESS;
    }
}