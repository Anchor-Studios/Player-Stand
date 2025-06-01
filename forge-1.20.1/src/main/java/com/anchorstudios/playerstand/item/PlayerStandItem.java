package com.anchorstudios.playerstand.item;

import com.anchorstudios.playerstand.Config;
import com.anchorstudios.playerstand.entity.ModEntities;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;


public class PlayerStandItem extends Item {
    public PlayerStandItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (!level.isClientSide()) {
            ChunkPos chunkPos = new ChunkPos(clickedPos);

            int max = Config.MAX_STANDS_PER_CHUNK.get();
            if (max > 0) {
                int minX = chunkPos.getMinBlockX();
                int minZ = chunkPos.getMinBlockZ();
                int maxX = chunkPos.getMaxBlockX();
                int maxZ = chunkPos.getMaxBlockZ();

                // Slightly extended bounds for safety
                AABB chunkBox = new AABB(minX - 1, -64, minZ - 1, maxX + 2, level.getMaxBuildHeight(), maxZ + 2);

                List<PlayerStandEntity> stands = level.getEntitiesOfClass(PlayerStandEntity.class, chunkBox);
                int countStands = stands.size();

                if (countStands >= max) {
                    return InteractionResult.FAIL;
                }
            }

            ItemStack stack = context.getItemInHand();
            double spawnX = clickedPos.getX() + 0.5;
            double spawnY = clickedPos.getY() + 1.0;
            double spawnZ = clickedPos.getZ() + 0.5;

            AABB spawnBox = new AABB(spawnX - 0.25, spawnY, spawnZ - 0.25, spawnX + 0.25, spawnY + 2.0, spawnZ + 0.25);
            List<PlayerStandEntity> nearbyStands = level.getEntitiesOfClass(PlayerStandEntity.class, spawnBox, e -> !e.isRemoved());

            if (!nearbyStands.isEmpty()) {
                return InteractionResult.FAIL;
            }


            PlayerStandEntity entity = new PlayerStandEntity(ModEntities.PLAYER_STAND_ENTITY.get(), level);
            entity.setPos(spawnX, spawnY, spawnZ);
            entity.setCustomName(Component.literal("Player Stand"));


            // Calculate yaw so the entity faces the player
            double dx = context.getPlayer().getX() - spawnX;
            double dz = context.getPlayer().getZ() - spawnZ;
            float yaw = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90F;
            entity.setYRot(yaw);
            entity.yHeadRot = yaw;
            entity.yBodyRot = yaw;

            entity.setCustomNameVisible(false);

            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("PlayerStandHeadId")) {
                entity.getPersistentData().putString("PlayerStandHeadId", tag.getString("PlayerStandHeadId"));
            }

            level.addFreshEntity(entity);
            level.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            if (!context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("PlayerStandHeadId")) {
            String headId = tag.getString("PlayerStandHeadId");
            String displayName = headId.replace("minecraft:", "");

            tooltip.add(
                    Component.literal("Current Model: ")
                            .withStyle(net.minecraft.ChatFormatting.YELLOW)
                            .append(Component.literal(displayName)
                                    .withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC))
            );
        }
    }



    // Preserve custom NBT when renamed in an Anvil
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("PlayerStandHeadId");
    }

    // Dynamic name generation
    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("PlayerStandHeadName")) {
            String headName = tag.getString("PlayerStandHeadName");

            // Remove suffix if present (case-sensitive)
            if (headName.endsWith(" Head")) {
                headName = headName.substring(0, headName.length() - 5);
            } else if (headName.endsWith(" Skull")) {
                headName = headName.substring(0, headName.length() - 6);
            }


            return Component.literal(headName + "'s Player Stand");
        }
        return super.getName(stack);
    }


    public static void setHeadData(ItemStack standStack, ItemStack headStack) {
        CompoundTag tag = standStack.getOrCreateTag();

        // Store display name
        tag.putString("PlayerStandHeadName", headStack.getHoverName().getString());

        // Store internal head ID
        ResourceLocation headId = BuiltInRegistries.ITEM.getKey(headStack.getItem());
        tag.putString("PlayerStandHeadId", headId.toString());
    }

}