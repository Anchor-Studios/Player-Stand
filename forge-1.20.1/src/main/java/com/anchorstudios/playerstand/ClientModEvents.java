package com.anchorstudios.playerstand.client;

import com.anchorstudios.playerstand.PlayerStand;
import com.anchorstudios.playerstand.entity.ModEntities;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlayerStand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PLAYER_STAND_ENTITY.get(),
                context -> new MobRenderer<PlayerStandEntity, HumanoidModel<PlayerStandEntity>>(
                        context,
                        new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)),
                        0.5f
                ) {
                    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PlayerStand.MODID, "textures/entity/player_stand_entity.png");

                    @Override
                    public ResourceLocation getTextureLocation(PlayerStandEntity entity) {
                        return TEXTURE;
                    }
                }
        );
    }
}