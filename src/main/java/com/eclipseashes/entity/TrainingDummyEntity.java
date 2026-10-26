package com.eclipseashes.entity;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;

public class TrainingDummyEntity extends ArmorStand {
    public TrainingDummyEntity(EntityType<? extends ArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            if (!level().isClientSide()) {
                ItemStack dummyItem = new ItemStack(ModItem.TRAINING_DUMMY);
                if (!player.getInventory().add(dummyItem)) {
                    player.drop(dummyItem, false);
                }
                discard();
            }
            return InteractionResult.SUCCESS;
        }

        return super.interactAt(player, hitPos, hand);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // Training dummies ignore normal attacks and environmental damage,
        // but fire can still destroy them.
        return !source.is(DamageTypeTags.IS_FIRE);
    }
}
