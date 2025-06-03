package com.anchorstudios.playerstand.entity;

import com.anchorstudios.playerstand.Config;
import com.anchorstudios.playerstand.PlayerStand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.phys.AABB;

import static net.minecraft.client.renderer.entity.ShulkerRenderer.getTextureLocation;

public class PlayerStandEntity extends Mob {

    private static final EntityDataAccessor<String> PLAYER_NAME_DATA =
            SynchedEntityData.defineId(PlayerStandEntity.class, EntityDataSerializers.STRING);

    public PlayerStandEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        boolean damaged = super.hurt(source, amount);
        if (damaged) {
            // Cancel knockback and hurt animation
            this.hurtTime = 0;
            this.invulnerableTime = 0;
            this.setDeltaMovement(0, 0, 0);

            if (!this.level().isClientSide) {
                boolean instantBreak = false;

                // Check if damage source is a player in creative mode
                if (source.getEntity() instanceof Player player) {
                    if (player.isCreative()) {
                        instantBreak = true;
                    }
                }

                if (instantBreak || this.getHealth() <= 0.0F) {
                    // Drop item only if not creative player
                    if (!instantBreak) {
                        this.spawnAtLocation(PlayerStand.PLAYER_STAND_ITEM.get());
                    }

                    this.discard();
                }
            }
        }
        return damaged;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND && Config.ALLOW_PLAYER_BINDING.get()) {
            ItemStack heldItem = player.getMainHandItem();

            if (heldItem.isEmpty()) {
                boolean alreadyHasTexture = !("NOT PLAYER" == this.getPlayerName());
                boolean canRetexture = !alreadyHasTexture || Config.ALLOW_RETEXTURE_EXISTING.get();

                if (canRetexture) {
                    this.setPlayerName(player.getName().getString());

                    // Set custom name to "<PlayerName>'s Player Stand"
                    String displayName = player.getDisplayName().getString() + "'s Player Stand";
                    this.setCustomName(Component.literal(displayName));

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    public void setPlayerName(String name) {
        this.getEntityData().set(PLAYER_NAME_DATA, name);
    }

    public String getPlayerName() {
        return this.getEntityData().get(PLAYER_NAME_DATA);
    }

    @Override
    public void tick() {
        super.tick();
        this.hurtTime = 0; // reset every tick to prevent red flash
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("PlayerStandPlayerName")) {
            setPlayerName(tag.getString("PlayerStandPlayerName"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("PlayerStandPlayerName", getPlayerName());
    }

    private static final EntityDataAccessor<String> DATA_CUSTOM_NAME =
            SynchedEntityData.defineId(PlayerStandEntity.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PLAYER_NAME_DATA, "NOT PLAYER"); // Default name
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    protected boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }
}
