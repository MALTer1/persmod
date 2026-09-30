package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class WorkshopManager {

    private static final int MAX_SEARCH_DISTANCE = 32;

    /*
     * Blocks that are considered part of a workshop.
     */
    public static boolean isWorkshopBlock(
            Level level,
            BlockPos pos
    ) {

        Block block = level.getBlockState(pos).getBlock();

        return block == ModWorkshopBlocks.FORGE_FLOOR
                || block == ModWorkshopBlocks.FORGE_SECTION
                || block == ModWorkshopBlocks.METAL_FORGE
                || block == ModWorkshopBlocks.BASIC_WORKSHOP;
    }

    /*
     * Finds every connected workshop block.
     */
    public static Set<BlockPos> findWorkshop(
            Level level,
            BlockPos start
    ) {

        Set<BlockPos> result =
                new HashSet<>();

        Queue<BlockPos> queue =
                new ArrayDeque<>();

        queue.add(start);
        result.add(start);

        while (!queue.isEmpty()) {

            BlockPos current =
                    queue.remove();

            for (BlockPos next :
                    neighbors(current)) {

                if (result.contains(next)) {
                    continue;
                }

                if (!withinSearchDistance(start, next)) {
                    continue;
                }

                if (!isWorkshopBlock(level, next)) {
                    continue;
                }

                result.add(next);
                queue.add(next);
            }
        }

        return result;
    }

    /*
     * Finds the workshop containing the Basic Workshop.
     */
    public static Set<BlockPos> findWorkshopAround(
            Level level,
            BlockPos workshopPos
    ) {

        Set<BlockPos> result =
                new HashSet<>();

        for (BlockPos neighbor :
                neighbors(workshopPos)) {

            if (isWorkshopBlock(level, neighbor)) {

                result.addAll(
                        findWorkshop(
                                level,
                                neighbor
                        )
                );
            }
        }

        return result;
    }

    /*
     * Counts a particular block.
     */
    public static int countBlock(
            Level level,
            Set<BlockPos> workshop,
            Block block
    ) {

        int count = 0;

        for (BlockPos pos : workshop) {

            if (level.getBlockState(pos).getBlock() == block) {
                count++;
            }
        }

        return count;
    }

    /*
     * Gets the number of Forge Floor blocks.
     */
    public static int getForgeFloorCount(
            Level level,
            Set<BlockPos> workshop
    ) {

        return countBlock(
                level,
                workshop,
                ModWorkshopBlocks.FORGE_FLOOR
        );
    }

    /*
     * Gets the number of Forge Sections.
     */
    public static int getForgeSectionCount(
            Level level,
            Set<BlockPos> workshop
    ) {

        return countBlock(
                level,
                workshop,
                ModWorkshopBlocks.FORGE_SECTION
        );
    }

    /*
     * Gets the number of Metal Forges.
     */
    public static int getMetalForgeCount(
            Level level,
            Set<BlockPos> workshop
    ) {

        return countBlock(
                level,
                workshop,
                ModWorkshopBlocks.METAL_FORGE
        );
    }

    /*
     * Gets the number of Basic Workshops.
     */
    public static int getBasicWorkshopCount(
            Level level,
            Set<BlockPos> workshop
    ) {

        return countBlock(
                level,
                workshop,
                ModWorkshopBlocks.BASIC_WORKSHOP
        );
    }

    /*
     * Temporary quality calculation.
     *
     * This is NOT the final craftsmanship system.
     */
    public static int getWorkshopQuality(
            Level level,
            Set<BlockPos> workshop
    ) {

        int floor =
                getForgeFloorCount(
                        level,
                        workshop
                );

        int forge =
                getMetalForgeCount(
                        level,
                        workshop
                );

        int sections =
                getForgeSectionCount(
                        level,
                        workshop
                );

        int quality =
                floor
                        + forge * 5
                        + sections * 2;

        return Math.min(
                100,
                quality
        );
    }

    /*
     * Six-direction block neighbors.
     */
    private static List<BlockPos> neighbors(
            BlockPos pos
    ) {

        return List.of(
                pos.above(),
                pos.below(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        );
    }

    private static boolean withinSearchDistance(
            BlockPos start,
            BlockPos pos
    ) {

        return Math.abs(
                pos.getX() - start.getX()
        ) <= MAX_SEARCH_DISTANCE

                &&

                Math.abs(
                        pos.getY() - start.getY()
                ) <= MAX_SEARCH_DISTANCE

                &&

                Math.abs(
                        pos.getZ() - start.getZ()
                ) <= MAX_SEARCH_DISTANCE;
    }
}