package com.eclipseashes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Set;

public class BasicWorkshopBlockEntity extends BlockEntity implements MenuProvider {

    private String workshopName = "Basic Workshop";

    public BasicWorkshopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_WORKSHOP, pos, state);
    }

    public String getWorkshopName() { return workshopName; }

    public void setWorkshopName(String name) {
        workshopName = (name == null || name.isBlank()) ? "Basic Workshop" : name;
        setChanged();
    }

    public Set<BlockPos> getWorkshop() {
        if (level == null) return Set.of();
        return WorkshopManager.findWorkshopAround(level, worldPosition);
    }

    public List<Set<BlockPos>> getSectors() {
        if (level == null) return List.of();
        return WorkshopManager.findSectors(level, getWorkshop());
    }

    public int getSectorCount() { return getSectors().size(); }

    public int getForgeFloorCount() {
        if (level == null) return 0;
        return WorkshopManager.getForgeFloorCount(level, getWorkshop());
    }

    public int getForgeSectionCount() {
        if (level == null) return 0;
        return WorkshopManager.getForgeSectionCount(level, getWorkshop());
    }

    public int getMetalForgeCount() {
        if (level == null) return 0;
        return WorkshopManager.getMetalForgeCount(level, getWorkshop());
    }

    public int getGroupCount() {
        if (level == null) return 0;
        return WorkshopManager.findWorkshopGroups(level, getWorkshop()).size();
    }

    public int getWorkshopQuality() {
        if (level == null) return 0;
        return WorkshopManager.getWorkshopQuality(level, getWorkshop());
    }

    public int getSectorForgeFloorCount(int sector) {
        List<Set<BlockPos>> sectors = getSectors();
        if (level == null || sector < 0 || sector >= sectors.size()) return 0;
        return WorkshopManager.getForgeFloorCount(level, sectors.get(sector));
    }

    public int getSectorForgeCount(int sector) {
        List<Set<BlockPos>> sectors = getSectors();
        if (level == null || sector < 0 || sector >= sectors.size()) return 0;
        return WorkshopManager.getMetalForgeCount(level, sectors.get(sector));
    }

    public int getSectorQuality(int sector) {
        List<Set<BlockPos>> sectors = getSectors();
        if (level == null || sector < 0 || sector >= sectors.size()) return 0;
        return WorkshopManager.getWorkshopQuality(level, sectors.get(sector));
    }

    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public Component getDisplayName() { return Component.literal(workshopName); }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BasicWorkshopMenu(containerId, inventory, this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        workshopName = input.getStringOr("WorkshopName", "Basic Workshop");
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putString("WorkshopName", workshopName);
        super.saveAdditional(output);
    }
}
