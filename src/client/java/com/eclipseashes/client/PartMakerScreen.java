package com.eclipseashes.client;

import com.eclipseashes.block.PartMakerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;

public class PartMakerScreen extends StationScreen<PartMakerMenu> {

    private static final int PANEL_WIDTH = 168;
    private static final int PANEL_HEIGHT = 166;

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
        super(menu, inventory, title, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        categoryButton = addRenderableWidget(Button.builder(
                Component.literal("Category: " + menu.getCategoryName() + " v"),
                button -> toggleDropdown(0)
        ).bounds(leftPos + 4, topPos + 22, 82, 18).build());

        familyButton = addRenderableWidget(Button.builder(
                Component.literal("Family: " + menu.getFamilyName() + " v"),
                button -> toggleDropdown(1)
        ).bounds(leftPos + 4, topPos + 42, 82, 18).build());

        lengthButton = addRenderableWidget(Button.builder(
                Component.literal("Length: " + menu.getLengthName() + " v"),
                button -> toggleDropdown(2)
        ).bounds(leftPos + 4, topPos + 62, 82, 18).build());

        shapeButton = addRenderableWidget(Button.builder(
                Component.literal("Shape: " + menu.getShapeName() + " v"),
                button -> toggleDropdown(3)
        ).bounds(leftPos + 88, topPos + 22, 76, 18).build());

        widthButton = addRenderableWidget(Button.builder(
                Component.literal("Width: " + menu.getWidthName() + " v"),
                button -> toggleDropdown(4)
        ).bounds(leftPos + 88, topPos + 42, 76, 18).build());

        createButton = addRenderableWidget(Button.builder(
                Component.literal("Create Mold"),
                button -> pressMenuButton(PartMakerMenu.CREATE)
        ).bounds(leftPos + 88, topPos + 62, 76, 18).build());

        categoryOptions = createOptions(CATEGORIES, 0, 10);
        familyOptions = createOptions(FAMILIES, 1, 20);
        lengthOptions = createOptions(LENGTHS, 2, 30);
        shapeOptions = createOptions(SHAPES, 3, 40);
        widthOptions = createOptions(WIDTHS, 4, 50);

        closeAllDropdowns();
    }

    private Button[] createOptions(String[] options, int dropdownId, int packetBase) {
        Button[] buttons = new Button[options.length + 1];
        int y = topPos + 4;

        buttons[0] = addRenderableWidget(Button.builder(
                Component.literal("None"),
                button -> {
                    pressMenuButton(packetBase - 1);
                    closeAllDropdowns();
                }
        ).bounds(leftPos + 4, y, 78, 16).build());

        for (int i = 0; i < options.length; i++) {
            final int option = i;

            buttons[i + 1] = addRenderableWidget(Button.builder(
                    Component.literal(options[i]),
                    button -> {
                        pressMenuButton(packetBase + option);
                        closeAllDropdowns();
                    }
            ).bounds(leftPos + 4, y + (i + 1) * 16, 78, 16).build());
        }

        return buttons;
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

        categoryButton.setMessage(Component.literal(
                "Category: " + menu.getCategoryName() + " v"));
        familyButton.setMessage(Component.literal(
                "Family: " + menu.getFamilyName() + " v"));
        lengthButton.setMessage(Component.literal(
                "Length: " + menu.getLengthName() + " v"));
        shapeButton.setMessage(Component.literal(
                "Shape: " + menu.getShapeName() + " v"));
        widthButton.setMessage(Component.literal(
                "Width: " + menu.getWidthName() + " v"));
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

        // Controls.
        graphics.fill(
                leftPos + 2, topPos + 18,
                leftPos + 166, topPos + 82,
                0xFFB8B8B8
        );

        // Material/output area.
        graphics.fill(
                leftPos + 88, topPos + 82,
                leftPos + 166, topPos + 82,
                0xFF9E9E9E
        );

        // Inventory area.
        graphics.fill(
                leftPos + 2, topPos + 82,
                leftPos + 166, topPos + 164,
                0xFFB8B8B8
        );

        graphics.text(this.font, "Material",
                leftPos + 92, topPos + 24, 0xFF202020, false);

        graphics.text(this.font, "Mold Output",
                leftPos + 92, topPos + 50, 0xFF202020, false);

        graphics.text(this.font, "Inventory",
                leftPos + 4, topPos + 82, 0xFF202020, false);

        // Main player inventory: 3 rows.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlotBackground(
                        graphics,
                        leftPos + 3 + column * 18,
                        topPos + 92 + row * 18
                );
            }
        }

        // Player hotbar.
        for (int column = 0; column < 9; column++) {
            drawSlotBackground(
                    graphics,
                    leftPos + 3 + column * 18,
                    topPos + 146
            );
        }
    }

    private void drawSlotBackground(
            GuiGraphicsExtractor graphics,
            int x,
            int y
    ) {
        graphics.fill(x, y, x + 16, y + 16, 0xFF202020);
        graphics.fill(x, y, x + 16, y + 1, 0xFF777777);
        graphics.fill(x, y + 15, x + 16, y + 16, 0xFF111111);
        graphics.fill(x, y, x + 1, y + 16, 0xFF777777);
        graphics.fill(x + 15, y, x + 16, y + 16, 0xFF111111);
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        graphics.text(this.font, this.title, 4, 8, 0xFF202020, false);
    }
}
