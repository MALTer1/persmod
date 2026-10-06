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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Prediction;

public class PartMakerMenu extends StationMenu {

    public static final int CATEGORY = 0;
    public static final int FAMILY = 1;
    public static final int LENGTH = 2;
    public static final int SHAPE = 3;
    public static final int WIDTH = 4;
    public static final int CREATE = 5;

    private static final int INPUT_CONTAINER_SLOT = 0;
    private static final int OUTPUT_CONTAINER_SLOT = 0;

    private static final int MENU_INPUT_SLOT = 0;
    private static final int MENU_OUTPUT_SLOT = 1;

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
    private final Inventory playerInventory;

    private final DataSlot categoryData = DataSlot.standalone();
    private final DataSlot familyData = DataSlot.standalone();
    private final DataSlot lengthData = DataSlot.standalone();
    private final DataSlot shapeData = DataSlot.standalone();
    private final DataSlot widthData = DataSlot.standalone();

    public PartMakerMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.PART_MAKER, containerId);
        this.playerInventory = inventory;

        addDataSlot(categoryData);
        addDataSlot(familyData);
        addDataSlot(lengthData);
        addDataSlot(shapeData);
        addDataSlot(widthData);

        addSlot(new Slot(input, INPUT_CONTAINER_SLOT, 116, 35) {
        @Override
        public boolean mayPlace(ItemStack stack) {
            return isMaterial(stack);
        }
    });

    addSlot(new Slot(output, OUTPUT_CONTAINER_SLOT, 144, 35) {
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
            case FAMILY -> cycle(familyData, FAMILIES.length);
            case LENGTH -> cycle(lengthData, LENGTHS.length);
            case SHAPE -> cycle(shapeData, SHAPES.length);
            case WIDTH -> cycle(widthData, WIDTHS.length);
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

        CustomData.set(
                DataComponents.CUSTOM_DATA,
                mold,
                tag
        );

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
                return stack;
            }
        }

        if (slotIndex >= 2 && slotIndex < 11) {
            Slot slot = this.slots.get(slotIndex);
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty() && isMaterial(stack)) {
                ItemStack copy = stack.copy();

                if (moveItemStackTo(stack, MENU_INPUT_SLOT, MENU_OUTPUT_SLOT, false)) {
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
