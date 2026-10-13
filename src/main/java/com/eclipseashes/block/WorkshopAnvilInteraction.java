package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Gives vanilla anvils a workshop-only metalworking interaction.
 * Input items are represented by fixed ItemDisplay entities resting on the anvil.
 */
public final class WorkshopAnvilInteraction {
    private static final String TAG_PREFIX = "eclipseashes_anvil_";

    private WorkshopAnvilInteraction() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            if (!isAnvil(level, pos)) return InteractionResult.PASS;
            if (!WorkshopManager.isStationInWorkshop(level, pos)) return InteractionResult.PASS;

            ItemStack held = player.getItemInHand(hand);
            boolean metalInput = held.is(ModItem.HEATED_METAL);
            boolean moldInput = held.is(ModItem.MOLD);
            boolean hammerInput = held.is(ModItem.BLACKSMITH_HAMMER);
            if (!metalInput && !moldInput && !hammerInput) return InteractionResult.PASS;
            if (level.isClientSide()) return InteractionResult.SUCCESS;

            if (metalInput) {
                if (findDisplay(level, pos, "metal") != null) {
                    message(player, "The left side of the anvil already has heated metal.");
                    return InteractionResult.SUCCESS;
                }
                if (spawnDisplay(level, pos, held.copyWithCount(1), tagFor(pos, "metal"), true)) {
                    held.shrink(1);
                    message(player, "Heated metal placed on the left side of the anvil.");
                }
                return InteractionResult.SUCCESS;
            }

            if (moldInput) {
                if (findDisplay(level, pos, "mold") != null) {
                    message(player, "The right side of the anvil already has a mold.");
                    return InteractionResult.SUCCESS;
                }
                CompoundTag data = customData(held);
                if (!"1".equals(stringTag(data, "eclipse_ashes_mold"))
                        || stringTag(data, "material").isBlank()
                        || stringTag(data, "category").isBlank()) {
                    message(player, "This mold is not configured. Make a new one at the Part Maker.");
                    return InteractionResult.SUCCESS;
                }
                if (spawnDisplay(level, pos, held.copyWithCount(1), tagFor(pos, "mold"), false)) {
                    held.shrink(1);
                    message(player, "Mold placed on the right side of the anvil.");
                }
                return InteractionResult.SUCCESS;
            }

            ItemDisplay metalDisplay = findDisplay(level, pos, "metal");
            ItemDisplay moldDisplay = findDisplay(level, pos, "mold");
            if (metalDisplay == null || moldDisplay == null) {
                message(player, "Place heated metal on the left and a configured mold on the right first.");
                return InteractionResult.SUCCESS;
            }

            CompoundTag metalData = customData(metalDisplay.getItemStack());
            CompoundTag moldData = customData(moldDisplay.getItemStack());
            String actualMaterial = stringTag(metalData, "material");
            String requiredMaterial = stringTag(moldData, "material");
            if (actualMaterial.isBlank() || !actualMaterial.equals(requiredMaterial)) {
                message(player, "Material mismatch: this mold needs " + requiredMaterial
                        + ", but the heated metal is " + (actualMaterial.isBlank() ? "unknown" : actualMaterial) + ".");
                return InteractionResult.SUCCESS;
            }

            Item outputItem;
            String partName;
            switch (stringTag(moldData, "category")) {
                case "Head" -> { outputItem = ModItem.FORGED_BLADE; partName = "Blade"; }
                case "Handle" -> { outputItem = ModItem.FORGED_HANDLE; partName = "Handle"; }
                case "Connector" -> { outputItem = ModItem.FORGED_GUARD; partName = "Guard"; }
                case "Basic Part" -> { outputItem = ModItem.METAL_PLATE; partName = "Plate"; }
                default -> {
                    message(player, "This mold category is not supported by the current shaping recipes.");
                    return InteractionResult.SUCCESS;
                }
            }

            ItemStack result = new ItemStack(outputItem);
            CustomData.set(DataComponents.CUSTOM_DATA, result, moldData.copy());
            result.set(DataComponents.CUSTOM_NAME, Component.literal(requiredMaterial + " " + partName));
            metalDisplay.discard();
            moldDisplay.discard();
            if (!player.getInventory().add(result) && !result.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), result));
            }
            message(player, partName + " shaped successfully from " + requiredMaterial + ".");
            return InteractionResult.SUCCESS;
        });
    }

    private static boolean isAnvil(Level level, BlockPos pos) {
        var block = level.getBlockState(pos).getBlock();
        return block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL;
    }

    private static boolean spawnDisplay(Level level, BlockPos pos, ItemStack stack, String tag, boolean left) {
        ItemDisplay display = new ItemDisplay(EntityType.ITEM_DISPLAY, level);
        display.setItemStack(stack);
        display.addTag(tag);
        display.setPos(pos.getX() + (left ? 0.30 : 0.70), pos.getY() + 1.02, pos.getZ() + 0.50);
        display.setTransformation(new Transformation(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Quaternionf().rotationX((float) (Math.PI / 2.0)),
                new Vector3f(0.55f, 0.55f, 0.55f),
                new Quaternionf()
        ));
        return level.addFreshEntity(display);
    }

    private static ItemDisplay findDisplay(Level level, BlockPos pos, String side) {
        String tag = tagFor(pos, side);
        AABB area = new AABB(pos).inflate(0.65, 1.35, 0.65);
        return level.getEntitiesOfClass(ItemDisplay.class, area, entity -> entity.getTags().contains(tag))
                .stream().findFirst().orElse(null);
    }

    private static String tagFor(BlockPos pos, String side) {
        return TAG_PREFIX + pos.getX() + "_" + pos.getY() + "_" + pos.getZ() + "_" + side;
    }

    private static CompoundTag customData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    private static String stringTag(CompoundTag tag, String key) {
        return tag.getString(key).orElse("");
    }

    private static void message(Player player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }
}
