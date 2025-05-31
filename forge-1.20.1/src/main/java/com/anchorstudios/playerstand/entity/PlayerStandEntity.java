package com.anchorstudios.playerstand.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Pose;

public class PlayerStandEntity extends Mob {

    public PlayerStandEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    // You can override AI/behavior as needed. For now, leave empty for armor stand-like behavior.
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    // Optional: make it immune to damage
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // Optional: disable AI
    @Override
    protected boolean canAddPassenger(net.minecraft.world.entity.Entity passenger) {
        return false;
    }


    // Optional: define attributes
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }
}
