package com.kilo.companionmod.gui;

import com.kilo.companionmod.entity.CompanionOrder;
import com.kilo.companionmod.entity.CombatMode;
import com.kilo.companionmod.network.CompanionOrderPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;

public class CompanionScreen extends Screen {
    private final int entityId;

    public CompanionScreen(Component title, int entityId) {
        super(title);
        this.entityId = entityId;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        addOrderButton(centerX, centerY - 60, "Seguir", CompanionOrder.FOLLOW, -1);
        addOrderButton(centerX, centerY - 35, "Quedarse", CompanionOrder.STAY, -1);
        addOrderButton(centerX, centerY - 10, "Vagar", CompanionOrder.WANDER, -1);
        addOrderButton(centerX, centerY + 15, "Patrullar", CompanionOrder.PATROL, -1);
        addCombatButton(centerX, centerY + 45, "Combate: Pasivo", CombatMode.PASSIVE);
        addCombatButton(centerX, centerY + 70, "Combate: Defensivo", CombatMode.DEFENSIVE);
        addCombatButton(centerX, centerY + 95, "Combate: Agresivo", CombatMode.AGGRESSIVE);
    }

    private void addOrderButton(int centerX, int y, String label, CompanionOrder order, int combatMode) {
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.connection.send(new ServerboundCustomPayloadPacket(new CompanionOrderPacket(entityId, order.ordinal(), combatMode)));
                this.minecraft.player.displayClientMessage(Component.literal("Orden: " + label), true);
                this.onClose();
            }
        }).bounds(centerX - 100, y, 200, 20).build());
    }

    private void addCombatButton(int centerX, int y, String label, CombatMode mode) {
        this.addRenderableWidget(Button.builder(Component.literal(label), b -> {
            if (this.minecraft.player != null) {
                this.minecraft.player.connection.send(new ServerboundCustomPayloadPacket(new CompanionOrderPacket(entityId, -1, mode.ordinal())));
                this.minecraft.player.displayClientMessage(Component.literal(label), true);
                this.onClose();
            }
        }).bounds(centerX - 100, y, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
