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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.Display;
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
import java.util.Locale;

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

            popup.y += 0.035D;
            popup.display.setPos(popup.x, popup.y, popup.z);

            // Approximate a fade by darkening the number over its short lifetime.
            int red = fadeChannel(popup.red, popup.age);
            int green = fadeChannel(popup.green, popup.age);
            int blue = fadeChannel(popup.blue, popup.age);
            int color = (red << 16) | (green << 8) | blue;
            popup.display.setText(Component.literal(popup.number)
                    .withStyle(Style.EMPTY.withColor(color).withBold(true)));
            popup.display.setTextOpacity((byte) Math.max(0, 255 - popup.age * 13));
        }
    }

    private static int fadeChannel(int original, int age) {
        return Math.max(25, original - age * 9);
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

        // Trigger the armor-stand hurt response without allowing ordinary hits to break it.
        level.broadcastEntityEvent(this, (byte) 2);
        float yawOffset = (getRandom().nextFloat() - 0.5F) * 8.0F;
        setYRot(getYRot() + yawOffset);
        level.playSound(null, getX(), getY(), getZ(),
                SoundEvents.ARMOR_STAND_HIT, SoundSource.BLOCKS, 0.8F,
                0.9F + getRandom().nextFloat() * 0.2F);

        level.sendParticles(ParticleTypes.ENCHANTED_HIT,
                getX(), getY() + getBbHeight() * 0.65D, getZ(),
                Math.max(3, Math.min(12, Math.round(amount * 1.5F))),
                0.22D, 0.3D, 0.22D, 0.05D);

        spawnDamagePopup(level, amount);
        return true;
    }

    private void spawnDamagePopup(ServerLevel level, float amount) {
        EntityType<?> textDisplayType = BuiltInRegistries.ENTITY_TYPE.get(
                Identifier.fromNamespaceAndPath("minecraft", "text_display"));
        var popupEntity = textDisplayType.create(level, EntitySpawnReason.TRIGGERED);
        if (!(popupEntity instanceof TextDisplay popup)) {
            return;
        }

        int color = amount < 3.0F ? 0x55FF55 : amount < 7.0F ? 0xFFAA00 : 0xFF4444;
        String number = amount == Math.round(amount)
                ? Integer.toString(Math.round(amount))
                : String.format(Locale.ROOT, "%.1f", amount);

        popup.setText(Component.literal(number)
                .withStyle(Style.EMPTY.withColor(color).withBold(true)));
        popup.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        popup.setBackgroundColor(0x00000000);
        popup.setTextOpacity((byte) 255);
        popup.setNoGravity(true);

        double x = getX() + (getRandom().nextDouble() - 0.5D) * 0.35D;
        double y = getY() + getBbHeight() + 0.25D;
        double z = getZ() + (getRandom().nextDouble() - 0.5D) * 0.35D;
        popup.setPos(x, y, z);

        if (level.addFreshEntity(popup)) {
            damagePopups.add(new Popup(popup, x, y, z, number,
                    (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF));
        }
    }

    private static final class Popup {
        private final TextDisplay display;
        private final double x;
        private double y;
        private final double z;
        private final String number;
        private final int red;
        private final int green;
        private final int blue;
        private int age;

        private Popup(TextDisplay display, double x, double y, double z, String number,
                      int red, int green, int blue) {
            this.display = display;
            this.x = x;
            this.y = y;
            this.z = z;
            this.number = number;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
