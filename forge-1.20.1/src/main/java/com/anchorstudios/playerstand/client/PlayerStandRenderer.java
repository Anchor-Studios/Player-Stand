package com.anchorstudios.playerstand.client;

import com.anchorstudios.playerstand.PlayerStand;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStandRenderer extends LivingEntityRenderer<PlayerStandEntity, HumanoidModel<PlayerStandEntity>> {
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();
    private static final ResourceLocation FALLBACK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PlayerStand.MODID, "textures/entity/player_stand_entity.png");

    public PlayerStandRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerStandEntity entity) {
        String playerName = entity.getPlayerName();
        return getSkinForName(playerName);
    }

    private ResourceLocation getSkinForName(String name) {
        if (name == "NOT PLAYER"){
            return FALLBACK_TEXTURE;
        }

        if (SKIN_CACHE.containsKey(name)) {
            return SKIN_CACHE.get(name);
        }

        // Try to get player skin
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            for (Player player : mc.level.players()) {
                if (player.getName().getString().equals(name)) {
                    PlayerInfo info = mc.getConnection().getPlayerInfo(player.getGameProfile().getId());
                    if (info != null) {
                        ResourceLocation skin = info.getSkinLocation();
                        SKIN_CACHE.put(name, skin);
                        return skin;
                    }
                }
            }
        }

        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin(uuid);
        SKIN_CACHE.put(name, skin);
        return skin;
    }

    @Override
    protected boolean shouldShowName(PlayerStandEntity entity) {
        entity.setCustomNameVisible(false);
        return entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity;
    }
}