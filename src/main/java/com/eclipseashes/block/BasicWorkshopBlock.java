package com.eclipseashes.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionResult;

public class BasicWorkshopBlock extends Block {

    public BasicWorkshopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!level.isClientSide()) {
            player.openMenu(
                    new SimpleMenuProvider(
                            (syncId, inventory, menuPlayer) ->
                                    ChestMenu.threeRows(syncId, inventory),
                            Component.translatable(
                                    "container.eclipseashes.basic_workshop"
                            )
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}