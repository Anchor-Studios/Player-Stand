package com.anchorstudios.playerstand;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class PlayerStandItem extends Item {
    public PlayerStandItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos clickedPos = context.getClickedPos();
            double spawnX = clickedPos.getX() + 0.5;
            double spawnY = clickedPos.getY() + 1.0;
            double spawnZ = clickedPos.getZ() + 0.5;


            ArmorStand armorStand = new ArmorStand(level, spawnX, spawnY, spawnZ);
            armorStand.setNoGravity(false);
            armorStand.setInvisible(false);
            armorStand.setCustomName(Component.literal("Player Stand"));

            float playerYaw = context.getPlayer().getYRot();
            armorStand.setYRot(playerYaw + 180); // Face toward player


            level.addFreshEntity(armorStand);

            level.playSound(null, spawnX, spawnY, spawnZ,
                    SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            // Consume the item if not in creative mode
            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}