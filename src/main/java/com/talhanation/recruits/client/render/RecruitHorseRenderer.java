package com.talhanation.recruits.client.render;

import com.talhanation.recruits.client.models.RecruitHorseModel;
import com.talhanation.recruits.entities.RecruitHorseEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RecruitHorseRenderer extends MobRenderer<RecruitHorseEntity, RecruitHorseModel<RecruitHorseEntity>> {
    private static final ResourceLocation[] TEXTURE = new ResourceLocation[]{
            new ResourceLocation("textures/entity/horse/horse_white.png"),
            new ResourceLocation("textures/entity/horse/horse_white.png"),
            new ResourceLocation("textures/entity/horse/horse_creamy.png"),
            new ResourceLocation("textures/entity/horse/horse_chestnut.png"),
            new ResourceLocation("textures/entity/horse/horse_brown.png"),
            new ResourceLocation("textures/entity/horse/horse_black.png"),
            new ResourceLocation("textures/entity/horse/horse_gray.png"),
            new ResourceLocation("textures/entity/horse/horse_darkbrown.png")};


    public RecruitHorseRenderer(EntityRendererProvider.Context manager) {
        super(manager, new RecruitHorseModel(manager.bakeLayer(ModelLayers.HORSE)), 1);

    }

    public ResourceLocation getTextureLocation(RecruitHorseEntity horse) {
        return TEXTURE[horse.getTypeVariant()];
    }

}