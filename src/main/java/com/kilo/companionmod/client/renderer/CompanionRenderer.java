package com.kilo.companionmod.client.renderer;

import com.kilo.companionmod.CompanionMod;
import com.kilo.companionmod.client.model.CompanionModel;
import com.kilo.companionmod.entity.CompanionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CompanionRenderer extends MobRenderer<CompanionEntity, CompanionModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CompanionMod.MOD_ID, "textures/entity/companion.png");

    public CompanionRenderer(EntityRendererProvider.Context context) {
        super(context, new CompanionModel(CompanionModel.createBodyLayer().bakeRoot()), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CompanionEntity entity) {
        return TEXTURE;
    }
}
