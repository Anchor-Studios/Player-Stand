package com.anchorstudios.playerstand.client;

import com.anchorstudios.playerstand.PlayerStand;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class PlayerStandRenderer extends LivingEntityRenderer<PlayerStandEntity, HumanoidModel<PlayerStandEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PlayerStand.MODID, "textures/entity/player_stand_entity.png");

    public PlayerStandRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerStandEntity entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(PlayerStandEntity entity) {
        entity.setCustomNameVisible(false);
        return entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity;
    }
}
