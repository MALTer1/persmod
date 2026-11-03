package com.eclipseashes.item;

import com.eclipseashes.entity.ModEntities;
import com.eclipseashes.entity.TrainingDummyEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class TrainingDummyItem extends Item {
    public TrainingDummyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());

        if (!level.getBlockState(placePos).getCollisionShape(level, placePos).isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            TrainingDummyEntity dummy = ModEntities.TRAINING_DUMMY.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (dummy == null) {
                return InteractionResult.FAIL;
            }

            dummy.setPos(placePos.getX() + 0.5D, placePos.getY(), placePos.getZ() + 0.5D);
            if (context.getPlayer() != null) {
                dummy.setYRot(context.getPlayer().getYRot());
            }

            if (!level.addFreshEntity(dummy)) {
                return InteractionResult.FAIL;
            }

            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
