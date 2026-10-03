package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class WorkshopManager {

    private static final int MAX_SEARCH_DISTANCE = 32;

    public static boolean isWorkshopBlock(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();

        return block == ModWorkshopBlocks.FORGE_FLOOR
                || block == ModWorkshopBlocks.FORGE_SECTION
                || block == ModWorkshopBlocks.METAL_FORGE
                || block == ModWorkshopBlocks.BASIC_WORKSHOP
                || block == ModWorkshopBlocks.PART_MAKER
                || block == ModWorkshopBlocks.ASSEMBLY_STATION
                || block == ModWorkshopBlocks.MAINTENANCE_STATION;
    }

    public static Set<BlockPos> findWorkshop(Level level, BlockPos start) {
        Set<BlockPos> result = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(start);
        result.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.remove();

            for (BlockPos next : neighbors(current)) {
                if (result.contains(next)) continue;
                if (!withinSearchDistance(start, next)) continue;
                if (!isWorkshopBlock(level, next)) continue;

                result.add(next);
                queue.add(next);
            }
        }

        return result;
    }

    public static Set<BlockPos> findWorkshopAround(
            Level level,
            BlockPos workshopPos
    ) {
        Set<BlockPos> result = new HashSet<>();

        for (BlockPos neighbor : neighbors(workshopPos)) {
            if (isWorkshopBlock(level, neighbor)) {
                result.addAll(findWorkshop(level, neighbor));
            }
        }

        return result;
    }

    /**
     * Splits a workshop into sectors. Forge Section blocks are boundaries,
     * so they belong to the workshop but not to an individual sector.
     */
    public static List<Set<BlockPos>> findSectors(
            Level level,
            Set<BlockPos> workshop
    ) {
        List<Set<BlockPos>> sectors = new ArrayList<>();
        Set<BlockPos> unassigned = new HashSet<>();

        for (BlockPos pos : workshop) {
            if (!isSection(level, pos)) {
                unassigned.add(pos);
            }
        }

        while (!unassigned.isEmpty()) {
            BlockPos start = unassigned.iterator().next();
            Set<BlockPos> sector = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();

            queue.add(start);
            unassigned.remove(start);

            while (!queue.isEmpty()) {
                BlockPos current = queue.remove();
                sector.add(current);

                for (BlockPos next : neighbors(current)) {
                    if (!unassigned.contains(next)) continue;
                    if (isSection(level, next)) continue;
                    if (!workshop.contains(next)) continue;

                    unassigned.remove(next);
                    queue.add(next);
                }
            }

            if (!sector.isEmpty()) {
                sectors.add(sector);
            }
        }

        return sectors;
    }

    public static Set<BlockPos> findSector(
            Level level,
            Set<BlockPos> workshop,
            BlockPos position
    ) {
        for (Set<BlockPos> sector : findSectors(level, workshop)) {
            if (sector.contains(position)) {
                return sector;
            }
        }

        return Collections.emptySet();
    }

    public static int countBlock(
            Level level,
            Set<BlockPos> positions,
            Block block
    ) {
        int count = 0;

        for (BlockPos pos : positions) {
            if (level.getBlockState(pos).getBlock() == block) {
                count++;
            }
        }

        return count;
    }

    public static int countSectorBlock(
            Level level,
            Set<BlockPos> sector,
            Block block
    ) {
        return countBlock(level, sector, block);
    }

    /**
     * Forge Sections count as Forge Floor for the overall workshop total.
     * They are still reported separately as Section Blocks and remain
     * excluded from individual sector contents because they are boundaries.
     */
    public static int getForgeFloorCount(
            Level level,
            Set<BlockPos> workshop
    ) {
        return countBlock(level, workshop, ModWorkshopBlocks.FORGE_FLOOR)
                + countBlock(level, workshop, ModWorkshopBlocks.FORGE_SECTION);
    }

    public static int getForgeSectionCount(
            Level level,
            Set<BlockPos> workshop
    ) {
        return countBlock(level, workshop, ModWorkshopBlocks.FORGE_SECTION);
    }

    public static int getMetalForgeCount(
            Level level,
            Set<BlockPos> workshop
    ) {
        return countBlock(level, workshop, ModWorkshopBlocks.METAL_FORGE);
    }

    public static int getBasicWorkshopCount(
            Level level,
            Set<BlockPos> workshop
    ) {
        return countBlock(level, workshop, ModWorkshopBlocks.BASIC_WORKSHOP);
    }

    public static int getSectorForgeFloorCount(
            Level level,
            Set<BlockPos> sector
    ) {
        return countSectorBlock(level, sector, ModWorkshopBlocks.FORGE_FLOOR);
    }

    public static int getSectorForgeCount(
            Level level,
            Set<BlockPos> sector
    ) {
        return countSectorBlock(level, sector, ModWorkshopBlocks.METAL_FORGE);
    }

    public static int getSectorBasicWorkshopCount(
            Level level,
            Set<BlockPos> sector
    ) {
        return countSectorBlock(level, sector, ModWorkshopBlocks.BASIC_WORKSHOP);
    }

    /**
     * Finds functional groups inside a sector. A group starts from an anchor
     * station and gathers nearby compatible stations. Forge Floors and Forge
     * Sections are infrastructure, not group members.
     */
    public static List<Set<BlockPos>> findGroups(
            Level level,
            Set<BlockPos> sector
    ) {
        List<Set<BlockPos>> groups = new ArrayList<>();
        Set<BlockPos> unassigned = new HashSet<>();

        for (BlockPos pos : sector) {
            if (getGroupType(level.getBlockState(pos).getBlock()) != null) {
                unassigned.add(pos);
            }
        }

        while (!unassigned.isEmpty()) {
            BlockPos anchor = unassigned.iterator().next();
            String groupType = getGroupType(level.getBlockState(anchor).getBlock());

            Set<BlockPos> group = new HashSet<>();

            for (BlockPos pos : new HashSet<>(unassigned)) {
                String type = getGroupType(level.getBlockState(pos).getBlock());

                if (!groupType.equals(type)) {
                    continue;
                }

                if (withinGroupDistance(anchor, pos)) {
                    group.add(pos);
                }
            }

            if (group.isEmpty()) {
                group.add(anchor);
            }

            unassigned.removeAll(group);
            groups.add(group);
        }

        return groups;
    }

    public static List<Set<BlockPos>> findWorkshopGroups(
            Level level,
            Set<BlockPos> workshop
    ) {
        List<Set<BlockPos>> groups = new ArrayList<>();

        for (Set<BlockPos> sector : findSectors(level, workshop)) {
            groups.addAll(findGroups(level, sector));
        }

        return groups;
    }

    private static String getGroupType(Block block) {
        if (block == ModWorkshopBlocks.METAL_FORGE) {
            return "forge";
        }

        if (block == ModWorkshopBlocks.BASIC_WORKSHOP) {
            return "workshop";
        }

        if (block == ModWorkshopBlocks.PART_MAKER) {
            return "part_maker";
        }

        if (block == ModWorkshopBlocks.ASSEMBLY_STATION) {
            return "assembly";
        }

        if (block == ModWorkshopBlocks.MAINTENANCE_STATION) {
            return "maintenance";
        }

        return null;
    }

    private static boolean withinGroupDistance(
            BlockPos anchor,
            BlockPos pos
    ) {
        int dx = pos.getX() - anchor.getX();
        int dy = pos.getY() - anchor.getY();
        int dz = pos.getZ() - anchor.getZ();

        return dx * dx + dy * dy + dz * dz <= 36;
    }

    public static int getWorkshopQuality(
            Level level,
            Set<BlockPos> workshop
    ) {
        int floor = getForgeFloorCount(level, workshop);
        int forge = getMetalForgeCount(level, workshop);
        int sections = getForgeSectionCount(level, workshop);

        int quality = floor + forge * 5 + sections * 2;
        return Math.min(100, quality);
    }

    private static boolean isSection(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock()
                == ModWorkshopBlocks.FORGE_SECTION;
    }

    private static List<BlockPos> neighbors(BlockPos pos) {
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
        return Math.abs(pos.getX() - start.getX()) <= MAX_SEARCH_DISTANCE
                && Math.abs(pos.getY() - start.getY()) <= MAX_SEARCH_DISTANCE
                && Math.abs(pos.getZ() - start.getZ()) <= MAX_SEARCH_DISTANCE;
    }
}
