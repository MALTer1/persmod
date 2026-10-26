package com.eclipseashes.entity;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrainingDummyEntity extends ArmorStand {
    private final List<Popup> damagePopups = new ArrayList<>();

    public TrainingDummyEntity(EntityType<? extends ArmorStand> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            return;
        }

        Iterator<Popup> iterator = damagePopups.iterator();
        while (iterator.hasNext()) {
            Popup popup = iterator.next();
            popup.age++;
            if (popup.display.isRemoved() || popup.age >= 20) {
                popup.display.discard();
                iterator.remove();
                continue;
            }

            double rise = 0.035D;
            popup.display.setPos(popup.x, popup.y + rise, popup.z);
            popup.y += rise;
            int opacity = Math.max(0, 255 - popup.age * 13);
            popup.display.setTextOpacity((byte) opacity);
        }
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
        if (source.is(DamageTypeTags.IS_FIRE)) {
            return super.hurtServer(level, source, amount);
        }

        if (amount <= 0.0F || isRemoved()) {
            return false;
        }

        // Play the normal hurt flash/animation without allowing the stand to break.
        level.broadcastEntityEvent(this, (byte) 2);
        float yawOffset = (level.random.nextFloat() - 0.5F) * 8.0F;
        setYRot(getYRot() + yawOffset);
        level.playSound(null, getX(), getY(), getZ(),
                SoundEvents.ARMOR_STAND_HIT, SoundSource.BLOCKS, 0.8F,
                0.9F + level.random.nextFloat() * 0.2F);

        level.sendParticles(ParticleTypes.ENCHANTED_HIT,
                getX(), getY() + getBbHeight() * 0.65D, getZ(),
                Math.max(3, Math.min(12, Math.round(amount * 1.5F))),
                0.22D, 0.3D, 0.22D, 0.05D);

        spawnDamagePopup(level, amount);
        return true;
    }

    private void spawnDamagePopup(ServerLevel level, float amount) {
        TextDisplay display = EntityType.TEXT_DISPLAY.create(level, EntitySpawnReason.TRIGGERED);
        if (display == null) {
            return;
        }

        int color = amount < 3.0F ? 0x55FF55 : amount < 7.0F ? 0xFFAA00 : 0xFF4444;
        String number = amount == Math.round(amount)
                ? Integer.toString(Math.round(amount))
                : String.format(java.util.Locale.ROOT, "%.1f", amount);

        display.setText(Component.literal(number)
                .withStyle(Style.EMPTY.withColor(color).withBold(true)));
        display.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        display.setBackgroundColor(0x00000000);
        display.setTextOpacity((byte) 255);
        display.setShadow(true);
        display.setSeeThrough(true);
        display.setNoGravity(true);
        double x = getX() + (level.random.nextDouble() - 0.5D) * 0.35D;
        double y = getY() + getBbHeight() + 0.25D;
        double z = getZ() + (level.random.nextDouble() - 0.5D) * 0.35D;
        display.setPos(x, y, z);

        if (level.addFreshEntity(display)) {
            damagePopups.add(new Popup(display, x, y, z));
        }
    }

    private static final class Popup {
        private final TextDisplay display;
        private final double x;
        private double y;
        private final double z;
        private int age;

        private Popup(TextDisplay display, double x, double y, double z) {
            this.display = display;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
