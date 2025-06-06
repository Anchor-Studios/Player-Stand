package com.anchorstudios.playerstand.recipe;

import com.anchorstudios.playerstand.PlayerStand;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, PlayerStand.MODID);

    public static final RegistryObject<RecipeSerializer<PlayerStandCraftingRecipe>> PLAYER_STAND_CRAFTING =
            SERIALIZERS.register("player_stand_crafting",
                    () -> PlayerStandRecipeSerializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}