package com.anchorstudios.playerstand;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PlayerStandRecipeSerializer implements RecipeSerializer<PlayerStandCraftingRecipe> {
    public static final PlayerStandRecipeSerializer INSTANCE = new PlayerStandRecipeSerializer();

    @Override
    public PlayerStandCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new PlayerStandCraftingRecipe(id);
    }

    @Override
    public PlayerStandCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return new PlayerStandCraftingRecipe(id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, PlayerStandCraftingRecipe recipe) {
        // No data needs to be sent
    }
}