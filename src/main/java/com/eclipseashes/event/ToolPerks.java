package com.eclipseashes.event;

import com.eclipseashes.block.ModBlocks;
import com.eclipseashes.item.ModItem;
import com.eclipseashes.item.tool.ModTools;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;

public class ToolPerks {

    private static final float EXTRA_DROP_CHANCE = 0.20F;
    private static final float VEIN_MINING_CHANCE = 0.20F;

    public static void initialize() {

        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {

            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }

            if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
                return;
            }

            ItemStack tool = serverPlayer.getMainHandItem();

            int tier = getToolTier(tool);

            if (tier == 0) {
                return;
            }

            Item rawDrop = getRawDrop(state);

            if (rawDrop == null) {
                return;
            }

            // Moonsteel and higher:
            // 20% chance for an additional raw ore drop.
            if (tier >= 2 && serverLevel.getRandom().nextFloat() < EXTRA_DROP_CHANCE) {
                spawnItem(serverLevel, pos, rawDrop);
            }

            // Sunsteel and higher:
            // Bonus mining XP.
            if (tier >= 3) {
                serverPlayer.giveExperiencePoints(2);
            }

            // Dragonite and higher:
            // 20% chance to vein mine up to 8 matching blocks.
            if (tier >= 4
                    && serverLevel.getRandom().nextFloat() < VEIN_MINING_CHANCE) {

                veinMine(
                        serverLevel,
                        serverPlayer,
                        pos,
                        state.getBlock(),
                        8
                );
            }

            // Celestial Alloy and higher:
            // One additional raw drop = double base drops.
            if (tier >= 6) {
                spawnItem(serverLevel, pos, rawDrop);
            }

            /*
             * Void Crystal auto-smelting is intentionally not implemented here.
             *
             * It will be connected to the Metal Forge processing system,
             * because that system defines:
             *
             * Raw -> Unrefined -> Refined -> Ingot -> Bar
             *
             * Eclipse will inherit the same auto-smelt behavior once the Forge exists.
             */
        });
    }

    private static int getToolTier(ItemStack stack) {

        if (stack.is(ModTools.ECLIPSE_PICKAXE)) return 7;
        if (stack.is(ModTools.CELESTIAL_ALLOY_PICKAXE)) return 6;
        if (stack.is(ModTools.VOID_CRYSTAL_PICKAXE)) return 5;
        if (stack.is(ModTools.DRAGONITE_PICKAXE)) return 4;
        if (stack.is(ModTools.SUNSTEEL_PICKAXE)) return 3;
        if (stack.is(ModTools.MOONSTEEL_PICKAXE)) return 2;
        if (stack.is(ModTools.CRIMSON_IRON_PICKAXE)) return 1;

        return 0;
    }

    private static Item getRawDrop(BlockState state) {

        if (state.is(ModBlocks.CRIMSON_IRON_ORE)) {
            return ModItem.RAW_CRIMSON_IRON;
        }

        if (state.is(ModBlocks.MOONSTEEL_ORE)) {
            return ModItem.RAW_MOONSTEEL;
        }

        if (state.is(ModBlocks.SUNSTEEL_ORE)) {
            return ModItem.RAW_SUNSTEEL;
        }

        if (state.is(ModBlocks.DRAGONITE_ORE)) {
            return ModItem.RAW_DRAGONITE;
        }

        if (state.is(ModBlocks.VOID_CRYSTAL_ORE)) {
            return ModItem.RAW_VOID_CRYSTAL;
        }

        if (state.is(ModBlocks.CELESTIAL_ALLOY_ORE)) {
            return ModItem.RAW_CELESTIAL_ALLOY;
        }

        if (state.is(ModBlocks.ECLIPSE_ORE)) {
            return ModItem.RAW_ECLIPSE;
        }

        return null;
    }

    private static void spawnItem(
            ServerLevel level,
            BlockPos pos,
            Item item
    ) {
        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                new ItemStack(item)
        );

        level.addFreshEntity(entity);
    }

    private static void veinMine(
            ServerLevel level,
            net.minecraft.server.level.ServerPlayer player,
            BlockPos origin,
            net.minecraft.world.level.block.Block targetBlock,
            int maximum
    ) {
        int broken = 0;

        for (int x = -1; x <= 1 && broken < maximum; x++) {
            for (int y = -1; y <= 1 && broken < maximum; y++) {
                for (int z = -1; z <= 1 && broken < maximum; z++) {

                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    BlockPos target = origin.offset(x, y, z);
                    BlockState targetState = level.getBlockState(target);

                    if (targetState.is(targetBlock)) {
                        if (level.destroyBlock(target, true, player)) {
                            broken++;
                        }
                    }
                }
            }
        }
    }
}