package com.anchorstudios.playerstand.item;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;


public class PlayerStandItem extends Item {
    public PlayerStandItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos clickedPos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();
            double spawnX = clickedPos.getX() + 0.5;
            double spawnY = clickedPos.getY() + 1.0;
            double spawnZ = clickedPos.getZ() + 0.5;

            PlayerStandEntity playerStand = ModEntities.PLAYER_STAND_ENTITY.get().create(level);
            if (playerStand == null) return InteractionResult.FAIL;

            playerStand.moveTo(spawnX, spawnY, spawnZ, context.getPlayer().getYRot() + 180, 0);
            playerStand.setNoGravity(false);
            playerStand.setInvisible(false);
            playerStand.setCustomName(Component.literal("Player Stand"));

            level.addFreshEntity(playerStand);

            level.playSound(null, spawnX, spawnY, spawnZ,
                    SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            // Consume the item if not in creative mode
            if (!context.getPlayer().isCreative()) {
                stack.shrink(1);
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