package com.eclipseashes.client;

import com.eclipseashes.block.BasicWorkshopMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BasicWorkshopScreen
        extends AbstractContainerScreen<BasicWorkshopMenu> {

    private static final Identifier BACKGROUND =
            Identifier.withDefaultNamespace(
                    "textures/gui/container/dispenser.png"
            );

    public BasicWorkshopScreen(
            BasicWorkshopMenu menu,
            Inventory inventory,
            Component title
    ) {

        super(
                menu,
                inventory,
                title,
                176,
                166
        );
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                this.leftPos,
                this.topPos,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256
        );

        int x =
                this.leftPos + 12;

        int y =
                this.topPos + 12;

        graphics.text(
                this.font,
                menu.getWorkshopName(),
                x,
                y,
                0xFFFFFFFF
        );

        y += 20;

        graphics.text(
                this.font,
                "Forge Floor: "
                        + menu.getFloorCount(),
                x,
                y,
                0xFFFFFFFF
        );

        y += 15;

        graphics.text(
                this.font,
                "Sections: "
                        + menu.getSectionCount(),
                x,
                y,
                0xFFFFFFFF
        );

        y += 15;

        graphics.text(
                this.font,
                "Metal Forges: "
                        + menu.getForgeCount(),
                x,
                y,
                0xFFFFFFFF
        );

        y += 25;

        graphics.text(
                this.font,
                "Workshop Quality",
                x,
                y,
                0xFFFFFFFF
        );

        y += 12;

        int width = 140;
        int height = 8;

        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF202020
        );

        int filled =
                menu.getQuality()
                        * width
                        / 100;

        if (filled > 0) {

            graphics.fill(
                    x,
                    y,
                    x + filled,
                    y + height,
                    0xFFB87333
            );
        }

        y += 20;

        graphics.text(
                this.font,
                menu.getQuality()
                        + " / 100",
                x,
                y,
                0xFFFFFFFF
        );
    }
}

