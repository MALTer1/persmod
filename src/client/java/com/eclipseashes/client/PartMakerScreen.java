package com.eclipseashes.client;

import com.eclipseashes.block.PartMakerMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PartMakerScreen extends StationScreen<PartMakerMenu> {

    private Button categoryButton;
    private Button familyButton;
    private Button lengthButton;
    private Button shapeButton;
    private Button widthButton;
    private Button createButton;

    public PartMakerScreen(PartMakerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        categoryButton = addRenderableWidget(Button.builder(
                Component.literal("Category: " + menu.getCategoryName()),
                button -> pressMenuButton(PartMakerMenu.CATEGORY)
        ).bounds(leftPos + 8, topPos + 22, 82, 20).build());

        familyButton = addRenderableWidget(Button.builder(
                Component.literal("Family: " + menu.getFamilyName()),
                button -> pressMenuButton(PartMakerMenu.FAMILY)
        ).bounds(leftPos + 94, topPos + 22, 82, 20).build());

        lengthButton = addRenderableWidget(Button.builder(
                Component.literal("Length: " + menu.getLengthName()),
                button -> pressMenuButton(PartMakerMenu.LENGTH)
        ).bounds(leftPos + 8, topPos + 44, 82, 20).build());

        shapeButton = addRenderableWidget(Button.builder(
                Component.literal("Shape: " + menu.getShapeName()),
                button -> pressMenuButton(PartMakerMenu.SHAPE)
        ).bounds(leftPos + 94, topPos + 44, 82, 20).build());

        widthButton = addRenderableWidget(Button.builder(
                Component.literal("Width: " + menu.getWidthName()),
                button -> pressMenuButton(PartMakerMenu.WIDTH)
        ).bounds(leftPos + 8, topPos + 66, 82, 20).build());

        createButton = addRenderableWidget(Button.builder(
                Component.literal("Create Mold"),
                button -> pressMenuButton(PartMakerMenu.CREATE)
        ).bounds(leftPos + 94, topPos + 66, 82, 20).build());
    }

    private void pressMenuButton(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButton(menu.containerId, id);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (categoryButton != null) {
            categoryButton.setMessage(
                    Component.literal("Category: " + menu.getCategoryName())
            );
        }

        if (familyButton != null) {
            familyButton.setMessage(
                    Component.literal("Family: " + menu.getFamilyName())
            );
        }

        if (lengthButton != null) {
            lengthButton.setMessage(
                    Component.literal("Length: " + menu.getLengthName())
            );
        }

        if (shapeButton != null) {
            shapeButton.setMessage(
                    Component.literal("Shape: " + menu.getShapeName())
            );
        }

        if (widthButton != null) {
            widthButton.setMessage(
                    Component.literal("Width: " + menu.getWidthName())
            );
        }
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        graphics.text(
                this.font,
                "Material",
                this.leftPos + 111,
                this.topPos + 22,
                0xFFFFFFFF
        );
    }
}
