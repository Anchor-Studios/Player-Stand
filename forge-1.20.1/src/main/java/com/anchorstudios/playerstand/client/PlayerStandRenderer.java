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

        // Get the pose number from the entity
        int poseNumber = entity.getEntityData().get(PlayerStandEntity.POSE_NUMBER);

        switch (poseNumber) {
            case 0 -> { // Default standing
            }

            case 1 -> { // Casual hands behind back
                this.leftArm.xRot = (float) Math.toRadians(-20);
                this.leftArm.yRot = (float) Math.toRadians(-90);
                this.rightArm.xRot = (float) Math.toRadians(-20);
                this.rightArm.yRot = (float) Math.toRadians(90);
            }

            case 2 -> { // Thinking (hand on chin)
                this.leftArm.xRot = (float) Math.toRadians(-90);
                this.leftArm.yRot = (float) Math.toRadians(-30);
                this.head.xRot = (float) Math.toRadians(10);
            }

            case 3 -> { // Pointing forward
                this.rightArm.xRot = (float) Math.toRadians(-90);
                this.leftArm.xRot = (float) Math.toRadians(-45);
            }

            case 4 -> { // Holding phone
                this.leftArm.xRot = (float) Math.toRadians(-120);
                this.leftArm.yRot = (float) Math.toRadians(-20);
                this.leftArm.zRot = (float) Math.toRadians(15);
                this.head.xRot = (float) Math.toRadians(15);
            }

            case 5 -> { // Holding shield
                this.leftArm.xRot = (float) Math.toRadians(-90);
                this.leftArm.yRot = (float) Math.toRadians(90);
                this.rightArm.xRot = (float) Math.toRadians(-45);
            }

            case 6 -> { // Surprised (hands up)
                this.leftArm.xRot = (float) Math.toRadians(-120);
                this.rightArm.xRot = (float) Math.toRadians(-120);
                this.head.xRot = (float) Math.toRadians(-10);
            }

            case 7 -> { // Holding cape
                this.leftArm.xRot = (float) Math.toRadians(-45);
                this.leftArm.yRot = (float) Math.toRadians(-120);
                this.rightArm.xRot = (float) Math.toRadians(-45);
                this.rightArm.yRot = (float) Math.toRadians(120);
            }

            case 8 -> { // Reading book
                this.leftArm.xRot = (float) Math.toRadians(-90);
                this.leftArm.yRot = (float) Math.toRadians(-45);
                this.rightArm.xRot = (float) Math.toRadians(-60);
                this.head.xRot = (float) Math.toRadians(30);
            }

            case 9 -> { // Holding torch
                this.rightArm.xRot = (float) Math.toRadians(-150);
                this.leftArm.xRot = (float) Math.toRadians(-30);
                this.head.xRot = (float) Math.toRadians(15);
            }

            case 10 -> { // Salute
                this.rightArm.xRot = (float) Math.toRadians(-120);
                this.rightArm.yRot = (float) Math.toRadians(-15);
            }

            case 11 -> { // Holding sword
                this.rightArm.xRot = (float) Math.toRadians(-135);
                this.leftArm.xRot = (float) Math.toRadians(-45);
                this.leftArm.yRot = (float) Math.toRadians(-30);
            }

            case 12 -> { // Hands on hips
                this.leftArm.xRot = (float) Math.toRadians(-30);
                this.leftArm.yRot = (float) Math.toRadians(-45);
                this.rightArm.xRot = (float) Math.toRadians(-30);
                this.rightArm.yRot = (float) Math.toRadians(45);
            }

            case 13 -> { // Holding bow
                this.leftArm.xRot = (float) Math.toRadians(-90);
                this.leftArm.yRot = (float) Math.toRadians(60);
                this.rightArm.xRot = (float) Math.toRadians(-90);
                this.rightArm.yRot = (float) Math.toRadians(-30);
            }

            case 14 -> { // Waving
                this.rightArm.xRot = (float) Math.toRadians(-90);
                this.rightArm.yRot = (float) Math.toRadians(-45);
                this.rightArm.zRot = (float) Math.toRadians(15);
            }

            case 15 -> { // Holding staff
                this.leftArm.xRot = (float) Math.toRadians(-90);
                this.rightArm.xRot = (float) Math.toRadians(-90);
                this.rightArm.yRot = (float) Math.toRadians(15);
            }

            default -> { // Fallback to default
            }
        }
    }
}