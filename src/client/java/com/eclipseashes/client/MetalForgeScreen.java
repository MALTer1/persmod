package com.eclipseashes.client;

import com.eclipseashes.block.MetalForgeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class MetalForgeScreen extends AbstractContainerScreen<MetalForgeMenu> {

    private static final Identifier BACKGROUND =
            Identifier.withDefaultNamespace(
                    "textures/gui/container/dispenser.png"
            );

    public MetalForgeScreen(
            MetalForgeMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        // -------------------------------------------------
        // Background
        // -------------------------------------------------

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

        // -------------------------------------------------
        // Forge material slots
        // -------------------------------------------------

        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 3; column++) {

                int x =
                        this.leftPos
                                + 62
                                + column * 18;

                int y =
                        this.topPos
                                + 16
                                + row * 18;

                drawSlotBackground(
                        graphics,
                        x,
                        y
                );
            }
        }

        // -------------------------------------------------
        // Lava input
        // -------------------------------------------------

        drawSlotBackground(
                graphics,
                this.leftPos + 44,
                this.topPos + 70
        );

        // -------------------------------------------------
        // Empty bucket output
        // -------------------------------------------------

        drawSlotBackground(
                graphics,
                this.leftPos + 116,
                this.topPos + 70
        );

        // -------------------------------------------------
        // Forge output
        // -------------------------------------------------

        drawSlotBackground(
                graphics,
                this.leftPos + 152,
                this.topPos + 35
        );

        // -------------------------------------------------
        // Hotbar
        // -------------------------------------------------

        for (int i = 0; i < 9; i++) {
            drawSlotBackground(
                    graphics,
                    this.leftPos + 8 + i * 18,
                    this.topPos + 140
            );
        }

        // -------------------------------------------------
        // Labels
        // -------------------------------------------------

        graphics.text(
                this.font,
                "Lava",
                this.leftPos + 20,
                this.topPos + 58,
                0xFFFFFFFF
        );

        graphics.text(
                this.font,
                "Process",
                this.leftPos + 20,
                this.topPos + 96,
                0xFFFFFFFF
        );

        // -------------------------------------------------
        // Lava display
        // -------------------------------------------------

        drawLavaBar(graphics);

        // -------------------------------------------------
        // Processing display
        // -------------------------------------------------

        drawProcessingBar(graphics);
    }

    // =====================================================
    // SLOT BACKGROUND
    // =====================================================

    private void drawSlotBackground(
            GuiGraphicsExtractor graphics,
            int x,
            int y
    ) {

        // Dark inside
        graphics.fill(
                x,
                y,
                x + 16,
                y + 16,
                0xFF202020
        );

        // Simple border
        graphics.fill(
                x,
                y,
                x + 16,
                y + 1,
                0xFF777777
        );

        graphics.fill(
                x,
                y + 15,
                x + 16,
                y + 16,
                0xFF111111
        );

        graphics.fill(
                x,
                y,
                x + 1,
                y + 16,
                0xFF777777
        );

        graphics.fill(
                x + 15,
                y,
                x + 16,
                y + 16,
                0xFF111111
        );
    }

    // =====================================================
    // LAVA BAR
    // =====================================================

    private void drawLavaBar(
            GuiGraphicsExtractor graphics
    ) {

        int x = this.leftPos + 20;
        int y = this.topPos + 70;
        int segmentWidth = 20;
        int segmentHeight = 8;
        int gap = 3;

        int lava = menu.getLavaAmount();
        int max = menu.getMaxLava();

        float percent = max <= 0 ? 0.0F : (float) lava / max;
        percent = Math.max(0.0F, Math.min(1.0F, percent));

        // Four development-stage levels:
        // 0-25% little, 25-50% medium, 50-75% high, 75-100% full.
        for (int i = 0; i < 4; i++) {
            int segmentX = x + i * (segmentWidth + gap);
            float segmentStart = i / 4.0F;
            float segmentEnd = (i + 1) / 4.0F;

            int fillWidth = 0;

            if (percent >= segmentEnd) {
                fillWidth = segmentWidth;
            } else if (percent > segmentStart) {
                fillWidth = (int) (segmentWidth *
                        ((percent - segmentStart) / (segmentEnd - segmentStart)));
            }

            graphics.fill(
                    segmentX,
                    y,
                    segmentX + segmentWidth,
                    y + segmentHeight,
                    0xFF321616
            );

            if (fillWidth > 0) {
                graphics.fill(
                        segmentX,
                        y,
                        segmentX + fillWidth,
                        y + segmentHeight,
                        0xFFE05220
                );
            }
        }

        graphics.text(
                this.font,
                lava + " / " + max + " mB",
                x + 92,
                y,
                0xFFFFFFFF
        );
    }

    // =====================================================
    // PROCESSING BAR
    // =====================================================

    private void drawProcessingBar(
            GuiGraphicsExtractor graphics
    ) {

        int x =
                this.leftPos + 20;

        int y =
                this.topPos + 88;

        int width = 90;

        int height = 6;

        int progress =
                menu.getProcessingProgress();

        int max =
                menu.getProcessingTime();

        float percent =
                max <= 0
                        ? 0.0F
                        : (float) progress / max;

        percent =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                percent
                        )
                );

        int filled =
                (int) (width * percent);

        // Border
        graphics.fill(
                x - 1,
                y - 1,
                x + width + 1,
                y + height + 1,
                0xFF111111
        );

        // Empty
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF292929
        );

        // Progress
        if (filled > 0) {

            graphics.fill(
                    x,
                    y,
                    x + filled,
                    y + height,
                    0xFFE0A52A
            );
        }
    }
}