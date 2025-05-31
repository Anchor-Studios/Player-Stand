package com.anchorstudios.playerstand.recipe;

import com.anchorstudios.playerstand.Config;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.core.RegistryAccess;

import com.anchorstudios.playerstand.item.PlayerStandItem;

import net.minecraft.world.item.PlayerHeadItem;

public class PlayerStandCraftingRecipe extends CustomRecipe {
    public PlayerStandCraftingRecipe(ResourceLocation id) {
        super(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean foundStand = false;
        boolean foundHead = false;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof PlayerStandItem) {
                    if (foundStand) return false;
                    foundStand = true;
                } else if (Config.ALLOW_MOB_HEAD_BINDING.get() && (stack.is(Items.ZOMBIE_HEAD) || stack.is(Items.SKELETON_SKULL))) {
                    if (foundHead) return false;
                    foundHead = true;
                } else {
                    return false;
                }
            }
        }

        return foundStand && foundHead;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack newStand = ItemStack.EMPTY;
        ItemStack headStack = ItemStack.EMPTY;

        // Find the stand and head in the grid
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof PlayerStandItem) {
                newStand = stack.copy();
                newStand.setCount(1);
            }
            else if (stack.getItem() instanceof PlayerHeadItem ||
                    stack.is(Items.ZOMBIE_HEAD) ||
                    stack.is(Items.SKELETON_SKULL)) {
                headStack = stack;
            }
        }

        if (!newStand.isEmpty() && !headStack.isEmpty()) {
            // Update the stand's NBT data
            PlayerStandItem.setHeadData(newStand, headStack);
        }

        return newStand;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof PlayerHeadItem || stack.is(Items.ZOMBIE_HEAD) || stack.is(Items.SKELETON_SKULL)) {
                // Keep the head, do not consume
                remaining.set(i, stack.copy());
            } else {
                // Use default remaining item if any
                remaining.set(i, stack.getCraftingRemainingItem());
            }
        }

        return remaining;
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PLAYER_STAND_CRAFTING.get(); // Register serializer
    }
}
