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

        int x = this.leftPos + 10;
        int y = this.topPos + 10;

        graphics.text(this.font, menu.getWorkshopName(), x, y, 0xFFFFFFFF);
        y += 18;

        graphics.text(this.font, "Forge Floors: " + menu.getFloorCount(), x, y, 0xFFFFFFFF);
        y += 13;

        graphics.text(this.font, "Sectors: " + menu.getSectorCount(), x, y, 0xFFFFFFFF);
        y += 13;

        graphics.text(this.font, "Metal Forges: " + menu.getForgeCount(), x, y, 0xFFFFFFFF);
        y += 13;

        graphics.text(this.font, "Section Blocks: " + menu.getSectionCount(), x, y, 0xFFFFFFFF);
        y += 18;

        graphics.text(
                this.font,
                "Workshop Quality: " + menu.getQuality() + " / 100",
                x,
                y,
                0xFFFFFFFF
        );

        y += 16;

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

        graphics.text(this.font, "Sectors", x, y, 0xFFFFFFFF);
        y += 13;

        int sectorCount = Math.min(menu.getSectorCount(), 16);

        for (int i = 0; i < sectorCount; i++) {
            int column = i % 2;
            int row = i / 2;

            int sectorX = column == 0 ? x : x + 108;
            int sectorY = y + row * 14;

            String text =
                    "Sector " + (i + 1)
                            + ": F" + menu.getSectorFloorCount(i)
                            + " M" + menu.getSectorForgeCount(i)
                            + " Q" + menu.getSectorQuality(i);

            graphics.text(
                    this.font,
                    text,
                    sectorX,
                    sectorY,
                    0xFFFFFFFF
            );
        }
    }
}
