package com.anchorstudios.playerstand;

import com.anchorstudios.playerstand.client.PlayerStandRenderer;
import com.anchorstudios.playerstand.entity.ModEntities;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlayerStand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PLAYER_STAND_ENTITY.get(), PlayerStandRenderer::new);
    }
}