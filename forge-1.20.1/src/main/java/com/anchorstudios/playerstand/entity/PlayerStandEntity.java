package com.anchorstudios.playerstand.entity;

import com.anchorstudios.playerstand.PlayerStand;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.phys.AABB;

public class PlayerStandEntity extends Mob {

    public PlayerStandEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isPushable() {
        return false;
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
    public void tick() {
        super.tick();
        this.hurtTime = 0; // reset every tick to prevent red flash
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


    // Optional: define attributes
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }
}
