package com.eclipseashes.client;

import com.eclipseashes.block.PartMakerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;

public class PartMakerScreen extends StationScreen<PartMakerMenu> {

    private static final int PANEL_HEIGHT = 222;

    private static final String[] CATEGORIES = {
            "Basic Part", "Head", "Handle", "Connector"
    };
    private static final String[] FAMILIES = {
            "Structural", "Utility", "Decorative", "Mechanical"
    };
    private static final String[] LENGTHS = {
            "Short", "Standard", "Long"
    };
    private static final String[] SHAPES = {
            "Straight", "Curved"
    };
    private static final String[] WIDTHS = {
            "Narrow", "Standard", "Wide"
    };

    private Button categoryButton;
    private Button familyButton;
    private Button lengthButton;
    private Button shapeButton;
    private Button widthButton;
    private Button createButton;

    private Button[] categoryOptions;
    private Button[] familyOptions;
    private Button[] lengthOptions;
    private Button[] shapeOptions;
    private Button[] widthOptions;

    private int openDropdown = -1;

    public PartMakerScreen(PartMakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = PANEL_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        categoryButton = addRenderableWidget(Button.builder(
                Component.literal("Category: " + menu.getCategoryName() + " v"),
                button -> toggleDropdown(0)
        ).bounds(leftPos + 8, topPos + 26, 96, 20).build());

        familyButton = addRenderableWidget(Button.builder(
                Component.literal("Family: " + menu.getFamilyName() + " v"),
                button -> toggleDropdown(1)
        ).bounds(leftPos + 8, topPos + 50, 96, 20).build());

        lengthButton = addRenderableWidget(Button.builder(
                Component.literal("Length: " + menu.getLengthName() + " v"),
                button -> toggleDropdown(2)
        ).bounds(leftPos + 8, topPos + 74, 96, 20).build());

        shapeButton = addRenderableWidget(Button.builder(
                Component.literal("Shape: " + menu.getShapeName() + " v"),
                button -> toggleDropdown(3)
        ).bounds(leftPos + 8, topPos + 98, 96, 20).build());

        widthButton = addRenderableWidget(Button.builder(
                Component.literal("Width: " + menu.getWidthName() + " v"),
                button -> toggleDropdown(4)
        ).bounds(leftPos + 8, topPos + 122, 96, 20).build());

        createButton = addRenderableWidget(Button.builder(
                Component.literal("Create Mold"),
                button -> pressMenuButton(PartMakerMenu.CREATE)
        ).bounds(leftPos + 108, topPos + 122, 60, 20).build());

        categoryOptions = createOptions(CATEGORIES, 0, 10);
        familyOptions = createOptions(FAMILIES, 1, 20);
        lengthOptions = createOptions(LENGTHS, 2, 30);
        shapeOptions = createOptions(SHAPES, 3, 40);
        widthOptions = createOptions(WIDTHS, 4, 50);

        closeAllDropdowns();
    }

    private Button[] createOptions(String[] options, int dropdownId, int packetBase) {
        Button[] buttons = new Button[options.length + 1];
        int y = getDropdownY(dropdownId) + 20;

        buttons[0] = addRenderableWidget(Button.builder(
                Component.literal("None"),
                button -> {
                    pressMenuButton(packetBase - 1);
                    closeAllDropdowns();
                }
        ).bounds(leftPos + 8, topPos + y, 96, 20).build());

        for (int i = 0; i < options.length; i++) {
            final int option = i;

            buttons[i + 1] = addRenderableWidget(Button.builder(
                    Component.literal(options[i]),
                    button -> {
                        pressMenuButton(packetBase + option);
                        closeAllDropdowns();
                    }
            ).bounds(leftPos + 8, topPos + y + (i + 1) * 20, 96, 20).build());
        }

        return buttons;
    }

    private int getDropdownY(int dropdownId) {
        return switch (dropdownId) {
            case 0 -> 26;
            case 1 -> 50;
            case 2 -> 74;
            case 3 -> 98;
            case 4 -> 122;
            default -> 26;
        };
    }

    private void toggleDropdown(int dropdownId) {
        if (openDropdown == dropdownId) {
            closeAllDropdowns();
            return;
        }

        closeAllDropdowns();
        openDropdown = dropdownId;

        setBaseButtonsActive(false);

        for (Button option : getOptions(dropdownId)) {
            option.visible = true;
            option.active = true;
        }
    }

    private void setBaseButtonsActive(boolean active) {
        if (categoryButton != null) categoryButton.active = active;
        if (familyButton != null) familyButton.active = active;
        if (lengthButton != null) lengthButton.active = active;
        if (shapeButton != null) shapeButton.active = active;
        if (widthButton != null) widthButton.active = active;
        if (createButton != null) createButton.active = active;
    }

    private Button[] getOptions(int dropdownId) {
        return switch (dropdownId) {
            case 0 -> categoryOptions;
            case 1 -> familyOptions;
            case 2 -> lengthOptions;
            case 3 -> shapeOptions;
            case 4 -> widthOptions;
            default -> categoryOptions;
        };
    }

    private void closeAllDropdowns() {
        openDropdown = -1;
        setBaseButtonsActive(true);

        if (categoryOptions != null) setOptionsHidden(categoryOptions);
        if (familyOptions != null) setOptionsHidden(familyOptions);
        if (lengthOptions != null) setOptionsHidden(lengthOptions);
        if (shapeOptions != null) setOptionsHidden(shapeOptions);
        if (widthOptions != null) setOptionsHidden(widthOptions);
    }

    private void setOptionsHidden(Button[] options) {
        for (Button option : options) {
            option.visible = false;
            option.active = false;
        }
    }

    private void pressMenuButton(int id) {
        if (minecraft != null && minecraft.getConnection() != null) {
            minecraft.getConnection().send(
                    new ServerboundContainerButtonClickPacket(menu.containerId, id)
            );
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (categoryButton != null) {
            categoryButton.setMessage(Component.literal(
                    "Category: " + menu.getCategoryName() + " v"));
        }
        if (familyButton != null) {
            familyButton.setMessage(Component.literal(
                    "Family: " + menu.getFamilyName() + " v"));
        }
        if (lengthButton != null) {
            lengthButton.setMessage(Component.literal(
                    "Length: " + menu.getLengthName() + " v"));
        }
        if (shapeButton != null) {
            shapeButton.setMessage(Component.literal(
                    "Shape: " + menu.getShapeName() + " v"));
        }
        if (widthButton != null) {
            widthButton.setMessage(Component.literal(
                    "Width: " + menu.getWidthName() + " v"));
        }
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        graphics.fill(
                leftPos, topPos,
                leftPos + imageWidth, topPos + imageHeight,
                0xFFD0D0D0
        );

        graphics.fill(
                leftPos + 4, topPos + 20,
                leftPos + 172, topPos + 136,
                0xFFB8B8B8
        );

        graphics.fill(
                leftPos + 108, topPos + 20,
                leftPos + 168, topPos + 112,
                0xFF9E9E9E
        );

        graphics.fill(
                leftPos + 4, topPos + 140,
                leftPos + 172, topPos + 216,
                0xFFB8B8B8
        );

        graphics.text(this.font, "Material",
                leftPos + 112, topPos + 28, 0xFF202020, false);

        graphics.text(this.font, "Mold Output",
                leftPos + 104, topPos + 76, 0xFF202020, false);

        graphics.text(this.font, "Inventory",
                leftPos + 8, topPos + 142, 0xFF202020, false);
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        graphics.text(this.font, this.title, 8, 8, 0xFF202020, false);
    }
}
