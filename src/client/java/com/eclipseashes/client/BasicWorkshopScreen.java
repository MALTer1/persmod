package com.eclipseashes.client;

import com.eclipseashes.block.BasicWorkshopMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicWorkshopScreen
        extends AbstractContainerScreen<BasicWorkshopMenu> {

    public BasicWorkshopScreen(
            BasicWorkshopMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(
                menu,
                inventory,
                title,
                230,
                220
        );
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        // Temporary clean workshop background.
        graphics.fill(
                this.leftPos,
                this.topPos,
                this.leftPos + this.imageWidth,
                this.topPos + this.imageHeight,
                0xFFBDBDBD
        );

        // Outer border.
        graphics.fill(
                this.leftPos,
                this.topPos,
                this.leftPos + this.imageWidth,
                this.topPos + 2,
                0xFF333333
        );
        graphics.fill(
                this.leftPos,
                this.topPos + this.imageHeight - 2,
                this.leftPos + this.imageWidth,
                this.topPos + this.imageHeight,
                0xFF333333
        );
        graphics.fill(
                this.leftPos,
                this.topPos,
                this.leftPos + 2,
                this.topPos + this.imageHeight,
                0xFF333333
        );
        graphics.fill(
                this.leftPos + this.imageWidth - 2,
                this.topPos,
                this.leftPos + this.imageWidth,
                this.topPos + this.imageHeight,
                0xFF333333
        );
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
        int x = 10;
        int y = 8;

        // Title.
        graphics.text(
                this.font,
                menu.getWorkshopName(),
                x,
                y,
                0xFF202020
        );

        y += 18;

        // Overall workshop information.
        graphics.text(
                this.font,
                "Forge Floors: " + menu.getFloorCount(),
                x,
                y,
                0xFF202020
        );
        y += 12;

        graphics.text(
                this.font,
                "Metal Forges: " + menu.getForgeCount(),
                x,
                y,
                0xFF202020
        );
        y += 12;

        y += 16;

        // Workshop quality.
        graphics.text(
                this.font,
                "Workshop Quality: " + menu.getQuality() + " / 100",
                x,
                y,
                0xFF202020
        );

        y += 12;

        int barWidth = 210;
        int barHeight = 7;

        graphics.fill(
                x,
                y,
                x + barWidth,
                y + barHeight,
                0xFF202020
        );

        int filled = menu.getQuality() * barWidth / 100;

        if (filled > 0) {
            graphics.fill(
                    x,
                    y,
                    x + filled,
                    y + barHeight,
                    0xFFB87333
            );
        }

        y += 16;

        // Sector list.
        graphics.text(
                this.font,
                "Sectors",
                x,
                y,
                0xFF202020
        );

        y += 13;

        int sectorCount = Math.min(menu.getSectorCount(), 16);

        for (int i = 0; i < sectorCount; i++) {
            int column = i % 2;
            int row = i / 2;

            int sectorX = column == 0 ? x : x + 108;
            int sectorY = y + row * 12;

            String text =
                    "S" + (i + 1)
                            + "  F" + menu.getSectorFloorCount(i)
                            + " M" + menu.getSectorForgeCount(i)
                            + " Q" + menu.getSectorQuality(i);

            graphics.text(
                    this.font,
                    text,
                    sectorX,
                    sectorY,
                    0xFF202020
            );
        }
    }
}
