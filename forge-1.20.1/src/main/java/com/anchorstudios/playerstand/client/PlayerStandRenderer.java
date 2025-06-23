package com.anchorstudios.playerstand.client;

import com.anchorstudios.playerstand.PlayerStand;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStandRenderer extends LivingEntityRenderer<PlayerStandEntity, PlayerStandModel> {
    private static final Map<String, ResourceLocation> SKIN_CACHE = new HashMap<>();
    private static final ResourceLocation FALLBACK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PlayerStand.MODID, "textures/entity/player_stand_entity.png");
    private static final ResourceLocation ZOMBIE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PlayerStand.MODID, "textures/entity/zombie.png");

    public PlayerStandRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerStandModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);

        // Add armor and held item layers
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerStandEntity entity) {
        String playerName = entity.getPlayerName();
        return getSkinForName(playerName);
    }

    private ResourceLocation getSkinForName(String name) {
        if (name.equals("NOT PLAYER")){
            return FALLBACK_TEXTURE;
        } else if (name.equals("minecraft:zombie_head")) {
            return ZOMBIE_TEXTURE;
        }

        if (SKIN_CACHE.containsKey(name)) {
            return SKIN_CACHE.get(name);
        }

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

class PlayerStandModel extends HumanoidModel<PlayerStandEntity> {
    public PlayerStandModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(PlayerStandEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Force left arm into a raised position
        this.leftArm.xRot = (float) Math.toRadians(-90);  // Points forward
        this.leftArm.yRot = 0;  // No rotation to the side
        this.leftArm.zRot = 0;  // No rotation around

        // You can adjust these values to get the exact pose you want
        // For example, to make it look like the entity is waving:
        // this.leftArm.xRot = (float) Math.toRadians(-90);
        // this.leftArm.yRot = (float) Math.toRadians(-45);
        // this.leftArm.zRot = 0;
    }
}