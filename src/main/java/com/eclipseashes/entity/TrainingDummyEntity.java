package com.eclipseashes.entity;

import com.eclipseashes.item.ModItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TrainingDummyEntity extends ArmorStand {
    public TrainingDummyEntity(EntityType<? extends ArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 hitPos) {
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!level().isClientSide()) {
                ItemStack dummyItem = new ItemStack(ModItem.TRAINING_DUMMY);
                if (!player.getInventory().add(dummyItem)) {
                    ItemEntity dropped = new ItemEntity(level(), getX(), getY(), getZ(), dummyItem);
                    level().addFreshEntity(dropped);
                }
                discard();
            }
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand, hitPos);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // Normal attacks cannot break the dummy. Fire damage is still allowed.
        if (!source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurtServer(level, source, amount);
    }
}
