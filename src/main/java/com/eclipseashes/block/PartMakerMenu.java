package com.eclipseashes.block;

import com.eclipseashes.item.ModItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class PartMakerMenu extends StationMenu {

    public static final int CATEGORY = 0;
    public static final int LENGTH = 1;
    public static final int SHAPE = 2;
    public static final int WIDTH = 3;
    public static final int MATERIAL = 4;
    public static final int CREATE = 5;

    private static final int OUTPUT_SLOT = 0;

    private static final String[] CATEGORIES = {
            "Basic Part",
            "Head",
            "Handle",
            "Connector"
    };

    private static final String[] LENGTHS = {
            "Short",
            "Standard",
            "Long"
    };

    private static final String[] SHAPES = {
            "Straight",
            "Curved"
    };

    private static final String[] WIDTHS = {
            "Narrow",
            "Standard",
            "Wide"
    };

    private static final Item[] MATERIAL_ITEMS = {
            Items.OAK_PLANKS,
            Items.COPPER_INGOT,
            Items.IRON_INGOT,
            Items.GOLD_INGOT,
            Items.DIAMOND,
            Items.NETHERITE_INGOT,
            ModItem.CRIMSON_IRON,
            ModItem.MOONSTEEL,
            ModItem.SUNSTEEL,
            ModItem.DRAGONITE,
            ModItem.VOID_CRYSTAL,
            ModItem.CELESTIAL_ALLOY,
            ModItem.ECLIPSE
    };

    private static final String[] MATERIAL_NAMES = {
            "Wood",
            "Copper",
            "Iron",
            "Gold",
            "Diamond",
            "Netherite",
            "Crimson Iron",
            "Moonsteel",
            "Sunsteel",
            "Dragonite",
            "Void Crystal",
            "Celestial Alloy",
            "Eclipse"
    };

    private final Container output = new SimpleContainer(1);
    private final Inventory playerInventory;

    private final DataSlot categoryData = DataSlot.standalone();
    private final DataSlot lengthData = DataSlot.standalone();
    private final DataSlot shapeData = DataSlot.standalone();
    private final DataSlot widthData = DataSlot.standalone();
    private final DataSlot materialData = DataSlot.standalone();

    public PartMakerMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.PART_MAKER, containerId);
        this.playerInventory = inventory;

        addDataSlot(categoryData);
        addDataSlot(lengthData);
        addDataSlot(shapeData);
        addDataSlot(widthData);
        addDataSlot(materialData);

        addSlot(new Slot(output, OUTPUT_SLOT, 144, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(
                    inventory,
                    i,
                    8 + i * 18,
                    140
            ));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        switch (id) {
            case CATEGORY -> cycle(categoryData, CATEGORIES.length);
            case LENGTH -> cycle(lengthData, LENGTHS.length);
            case SHAPE -> cycle(shapeData, SHAPES.length);
            case WIDTH -> cycle(widthData, WIDTHS.length);
            case MATERIAL -> cycle(materialData, MATERIAL_ITEMS.length);
            case CREATE -> createMold(player);
            default -> {
                return false;
            }
        }

        return true;
    }

    private void cycle(DataSlot slot, int size) {
        slot.set((slot.get() + 1) % size);
        broadcastChanges();
    }

    private void createMold(Player player) {
        if (!output.getItem(OUTPUT_SLOT).isEmpty()) {
            return;
        }

        Item material = MATERIAL_ITEMS[materialData.get()];
        int inventorySlot = findMaterial(player, material);

        if (inventorySlot < 0) {
            player.sendSystemMessage(
                    Component.literal("You need " + MATERIAL_NAMES[materialData.get()] + " to make this mold.")
            );
            return;
        }

        player.getInventory().removeItem(inventorySlot, 1);

        ItemStack mold = new ItemStack(ModItem.MOLD);

        CompoundTag tag = new CompoundTag();
        tag.putString("eclipse_ashes_mold", "1");
        tag.putString("category", CATEGORIES[categoryData.get()]);
        tag.putString("length", LENGTHS[lengthData.get()]);
        tag.putString("shape", SHAPES[shapeData.get()]);
        tag.putString("width", WIDTHS[widthData.get()]);
        tag.putString("material", MATERIAL_NAMES[materialData.get()]);

        CustomData.set(
                DataComponents.CUSTOM_DATA,
                mold,
                tag
        );

        mold.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(
                        MATERIAL_NAMES[materialData.get()]
                                + " "
                                + LENGTHS[lengthData.get()]
                                + " "
                                + SHAPES[shapeData.get()]
                                + " "
                                + WIDTHS[widthData.get()]
                                + " "
                                + CATEGORIES[categoryData.get()]
                                + " Mold"
                )
        );

        output.setItem(OUTPUT_SLOT, mold);
        broadcastChanges();
    }

    private int findMaterial(Player player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) {
                return i;
            }
        }

        return -1;
    }

    public String getCategoryName() {
        return CATEGORIES[categoryData.get()];
    }

    public String getLengthName() {
        return LENGTHS[lengthData.get()];
    }

    public String getShapeName() {
        return SHAPES[shapeData.get()];
    }

    public String getWidthName() {
        return WIDTHS[widthData.get()];
    }

    public String getMaterialName() {
        return MATERIAL_NAMES[materialData.get()];
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex == 0) {
            ItemStack stack = output.removeItemNoUpdate(OUTPUT_SLOT);

            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }
}
