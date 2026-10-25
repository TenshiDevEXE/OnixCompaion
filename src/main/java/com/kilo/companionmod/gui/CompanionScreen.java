package com.kilo.companionmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CompanionScreen extends Screen {
    public CompanionScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Seguir"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Orden: Seguir"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY - 60, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Quedarse"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Orden: Quedarse"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY - 35, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Vagar"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Orden: Vagar"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY - 10, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Combate: Pasivo"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Modo: Pasivo"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY + 15, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Combate: Defensivo"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Modo: Defensivo"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY + 40, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Combate: Agresivo"), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("Modo: Agresivo"), true);
                this.onClose();
            }
        }).bounds(centerX - 100, centerY + 65, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
