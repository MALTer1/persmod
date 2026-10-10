package com.eclipseashes.client;

import com.eclipseashes.block.PartMakerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.world.entity.player.Inventory;

public class PartMakerScreen extends StationScreen<PartMakerMenu> {

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 198;

    private static final String[] CATEGORIES = {"Basic Part", "Head", "Handle", "Connector"};
    private static final String[] FAMILIES = {"Structural", "Utility", "Decorative", "Mechanical"};
    private static final String[] LENGTHS = {"Short", "Standard", "Long"};
    private static final String[] SHAPES = {"Straight", "Curved"};
    private static final String[] WIDTHS = {"Narrow", "Standard", "Wide"};

    private Button categoryButton, familyButton, lengthButton, shapeButton, widthButton, createButton;
    private Button[] categoryOptions, familyOptions, lengthOptions, shapeOptions, widthOptions;
    private int openDropdown = -1;

    public PartMakerScreen(PartMakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        categoryButton = addRenderableWidget(makeButton("Category: " + menu.getCategoryName() + " v", 4, 22, 82, 18, () -> toggleDropdown(0)));
        familyButton = addRenderableWidget(makeButton("Family: " + menu.getFamilyName() + " v", 4, 42, 82, 18, () -> toggleDropdown(1)));
        lengthButton = addRenderableWidget(makeButton("Length: " + menu.getLengthName() + " v", 4, 62, 82, 18, () -> toggleDropdown(2)));
        shapeButton = addRenderableWidget(makeButton("Shape: " + menu.getShapeName() + " v", 90, 22, 82, 18, () -> toggleDropdown(3)));
        widthButton = addRenderableWidget(makeButton("Width: " + menu.getWidthName() + " v", 90, 42, 82, 18, () -> toggleDropdown(4)));
        createButton = addRenderableWidget(makeButton("Create Mold", 90, 62, 82, 18, () -> pressMenuButton(PartMakerMenu.CREATE)));

        categoryOptions = createOptions(CATEGORIES, 0, 10);
        familyOptions = createOptions(FAMILIES, 1, 20);
        lengthOptions = createOptions(LENGTHS, 2, 30);
        shapeOptions = createOptions(SHAPES, 3, 40);
        widthOptions = createOptions(WIDTHS, 4, 50);
        closeAllDropdowns();
    }

    private Button makeButton(String text, int x, int y, int width, int height, Runnable action) {
        return Button.builder(Component.literal(text), button -> action.run())
                .bounds(leftPos + x, topPos + y, width, height).build();
    }

    private Button[] createOptions(String[] options, int dropdownId, int packetBase) {
        Button[] buttons = new Button[options.length + 1];
        int x = dropdownId >= 3 ? 90 : 4;
        int y = dropdownId == 0 || dropdownId == 3 ? 22
                : dropdownId == 1 || dropdownId == 4 ? 42 : 62;
        // Drop the list below the control that owns it; options overlay the panel intentionally.
        int optionY = y + 18;
        buttons[0] = addRenderableWidget(makeButton("None", x, optionY, 82, 16, () -> {
            pressMenuButton(packetBase - 1);
            closeAllDropdowns();
        }));
        for (int i = 0; i < options.length; i++) {
            final int option = i;
            buttons[i + 1] = addRenderableWidget(makeButton(options[i], x, optionY + (i + 1) * 16, 82, 16, () -> {
                pressMenuButton(packetBase + option);
                closeAllDropdowns();
            }));
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

    private Button[] getOptions(int id) {
        return switch (id) {
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
            minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, id));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        categoryButton.setMessage(Component.literal("Category: " + menu.getCategoryName() + " v"));
        familyButton.setMessage(Component.literal("Family: " + menu.getFamilyName() + " v"));
        lengthButton.setMessage(Component.literal("Length: " + menu.getLengthName() + " v"));
        shapeButton.setMessage(Component.literal("Shape: " + menu.getShapeName() + " v"));
        widthButton.setMessage(Component.literal("Width: " + menu.getWidthName() + " v"));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFFD0D0D0);
        graphics.fill(leftPos + 2, topPos + 18, leftPos + 174, topPos + 82, 0xFFB8B8B8);
        graphics.fill(leftPos + 2, topPos + 84, leftPos + 174, topPos + 120, 0xFF9E9E9E);
        graphics.fill(leftPos + 2, topPos + 120, leftPos + 174, topPos + 196, 0xFFB8B8B8);

        graphics.text(this.font, "Material", leftPos + 90, topPos + 88, 0xFF202020, false);
        graphics.text(this.font, "Mold Output", leftPos + 90, topPos + 106, 0xFF202020, false);
        graphics.text(this.font, "Inventory", leftPos + 4, topPos + 122, 0xFF202020, false);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlotBackground(graphics, leftPos + 7 + column * 18, topPos + 130 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlotBackground(graphics, leftPos + 7 + column * 18, topPos + 184);
        }
    }

    private void drawSlotBackground(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 16, y + 16, 0xFF202020);
        graphics.fill(x, y, x + 16, y + 1, 0xFF777777);
        graphics.fill(x, y + 15, x + 16, y + 16, 0xFF111111);
        graphics.fill(x, y, x + 1, y + 16, 0xFF777777);
        graphics.fill(x + 15, y, x + 16, y + 16, 0xFF111111);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 4, 8, 0xFF202020, false);
    }
}