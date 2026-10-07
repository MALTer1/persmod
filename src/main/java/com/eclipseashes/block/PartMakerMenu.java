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
import net.minecraft.util.Prediction;

public class PartMakerMenu extends StationMenu {

    public static final int CATEGORY = 0;
    public static final int FAMILY = 1;
    public static final int LENGTH = 2;
    public static final int SHAPE = 3;
    public static final int WIDTH = 4;
    public static final int CREATE = 60;

    private static final int CATEGORY_OPTION_BASE = 10;
    private static final int FAMILY_OPTION_BASE = 20;
    private static final int LENGTH_OPTION_BASE = 30;
    private static final int SHAPE_OPTION_BASE = 40;
    private static final int WIDTH_OPTION_BASE = 50;

    private static final int INPUT_CONTAINER_SLOT = 0;
    private static final int OUTPUT_CONTAINER_SLOT = 0;

    private static final int MENU_INPUT_SLOT = 0;
    private static final int MENU_OUTPUT_SLOT = 1;
    private static final int PLAYER_INVENTORY_START = 2;
    private static final int PLAYER_INVENTORY_END = 38;

    private static final String[] CATEGORIES = {
            "Basic Part",
            "Head",
            "Handle",
            "Connector"
    };

    private static final String[] FAMILIES = {
            "Structural",
            "Utility",
            "Decorative",
            "Mechanical"
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

    private final Container input = new SimpleContainer(1);
    private final Container output = new SimpleContainer(1);

    private final DataSlot categoryData = DataSlot.standalone();
    private final DataSlot familyData = DataSlot.standalone();
    private final DataSlot lengthData = DataSlot.standalone();
    private final DataSlot shapeData = DataSlot.standalone();
    private final DataSlot widthData = DataSlot.standalone();

    public PartMakerMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.PART_MAKER, containerId);

        addDataSlot(categoryData);
        addDataSlot(familyData);
        addDataSlot(lengthData);
        addDataSlot(shapeData);
        addDataSlot(widthData);

        addSlot(new Slot(input, INPUT_CONTAINER_SLOT, 130, 40) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isMaterial(stack);
            }
        });

        addSlot(new Slot(output, OUTPUT_CONTAINER_SLOT, 130, 88) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        inventory,
                        row * 9 + column + 9,
                        8 + column * 18,
                        180 + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    198
            ));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == CREATE) {
            createMold(player);
            return true;
        }

        if (id >= CATEGORY_OPTION_BASE
                && id < CATEGORY_OPTION_BASE + CATEGORIES.length) {
            categoryData.set(id - CATEGORY_OPTION_BASE);
            broadcastChanges();
            return true;
        }

        if (id >= FAMILY_OPTION_BASE
                && id < FAMILY_OPTION_BASE + FAMILIES.length) {
            familyData.set(id - FAMILY_OPTION_BASE);
            broadcastChanges();
            return true;
        }

        if (id >= LENGTH_OPTION_BASE
                && id < LENGTH_OPTION_BASE + LENGTHS.length) {
            lengthData.set(id - LENGTH_OPTION_BASE);
            broadcastChanges();
            return true;
        }

        if (id >= SHAPE_OPTION_BASE
                && id < SHAPE_OPTION_BASE + SHAPES.length) {
            shapeData.set(id - SHAPE_OPTION_BASE);
            broadcastChanges();
            return true;
        }

        if (id >= WIDTH_OPTION_BASE
                && id < WIDTH_OPTION_BASE + WIDTHS.length) {
            widthData.set(id - WIDTH_OPTION_BASE);
            broadcastChanges();
            return true;
        }

        return false;
    }

    private void createMold(Player player) {
        if (!output.getItem(OUTPUT_CONTAINER_SLOT).isEmpty()) {
            return;
        }

        ItemStack materialStack = input.getItem(INPUT_CONTAINER_SLOT);

        if (!isMaterial(materialStack)) {
            player.sendSystemMessage(
                    Component.literal("Put a usable material in the material slot first.")
            );
            return;
        }

        String materialName = materialStack.getHoverName().getString();
        input.removeItem(INPUT_CONTAINER_SLOT, 1);

        ItemStack mold = new ItemStack(ModItem.MOLD);

        CompoundTag tag = new CompoundTag();
        tag.putString("eclipse_ashes_mold", "1");
        tag.putString("category", CATEGORIES[categoryData.get()]);
        tag.putString("family", FAMILIES[familyData.get()]);
        tag.putString("length", LENGTHS[lengthData.get()]);
        tag.putString("shape", SHAPES[shapeData.get()]);
        tag.putString("width", WIDTHS[widthData.get()]);
        tag.putString("material", materialName);

        CustomData.set(DataComponents.CUSTOM_DATA, mold, tag);

        mold.set(
                DataComponents.CUSTOM_NAME,
                Component.literal(
                        materialName
                                + " "
                                + FAMILIES[familyData.get()]
                                + " "
                                + CATEGORIES[categoryData.get()]
                                + " Mold"
                )
        );

        output.setItem(OUTPUT_CONTAINER_SLOT, mold);
        broadcastChanges();
    }

    private boolean isMaterial(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (Item item : MATERIAL_ITEMS) {
            if (stack.is(item)) {
                return true;
            }
        }

        return false;
    }

    public String getCategoryName() {
        return CATEGORIES[categoryData.get()];
    }

    public String getFamilyName() {
        return FAMILIES[familyData.get()];
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

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex == MENU_OUTPUT_SLOT) {
            ItemStack stack = output.removeItemNoUpdate(OUTPUT_CONTAINER_SLOT);

            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack, Prediction.SERVER_ONLY);
                broadcastChanges();
                return stack;
            }
        }

        if (slotIndex >= PLAYER_INVENTORY_START && slotIndex < PLAYER_INVENTORY_END) {
            Slot slot = this.slots.get(slotIndex);
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty() && isMaterial(stack)) {
                ItemStack copy = stack.copy();

                if (moveItemStackTo(
                        stack,
                        MENU_INPUT_SLOT,
                        MENU_INPUT_SLOT + 1,
                        false
                )) {
                    if (stack.isEmpty()) {
                        slot.setByPlayer(ItemStack.EMPTY);
                    } else {
                        slot.setChanged();
                    }

                    return copy;
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
