package com.anchorstudios.playerstand.server;

import com.anchorstudios.playerstand.PlayerStand;
import com.anchorstudios.playerstand.SkinFetcher;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;

@Mod.EventBusSubscriber(modid = PlayerStand.MODID)
public class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        SkinFetcher.checkAndSaveSkin(serverPlayer);
    }

}
