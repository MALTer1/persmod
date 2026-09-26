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
    }
}