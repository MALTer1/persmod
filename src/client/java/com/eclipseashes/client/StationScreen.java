package com.eclipseashes.client;

import com.eclipseashes.block.StationMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class StationScreen<M extends StationMenu> extends AbstractContainerScreen<M> {

    private static final Identifier BACKGROUND =
            Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

    public StationScreen(M menu, Inventory inventory, Component title) {
        this(menu, inventory, title, 176, 120);
    }

    protected StationScreen(
            M menu,
            Inventory inventory,
            Component title,
            int width,
            int height
    ) {
        super(menu, inventory, title, width, height);
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

        graphics.text(
                this.font,
                this.title,
                this.leftPos + 8,
                this.topPos + 8,
                0xFFFFFFFF
        );

        graphics.text(
                this.font,
                "Smithery station",
                this.leftPos + 8,
                this.topPos + 28,
                0xFFB0B0B0
        );

        graphics.text(
                this.font,
                "Workshop functionality will be added here.",
                this.leftPos + 8,
                this.topPos + 48,
                0xFFFFFFFF
        );
    }
}
