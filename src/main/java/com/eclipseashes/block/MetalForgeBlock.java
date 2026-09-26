package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MetalForgeBlock extends BaseEntityBlock {

    public MetalForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return new MetalForgeBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof MetalForgeBlockEntity forge) {

            player.openMenu(forge);
        }

        return InteractionResult.SUCCESS;
    }
}