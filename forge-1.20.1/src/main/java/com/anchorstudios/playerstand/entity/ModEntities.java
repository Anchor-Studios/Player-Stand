package com.anchorstudios.playerstand.entity;

import com.anchorstudios.playerstand.PlayerStand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PlayerStand.MODID);

    public static final RegistryObject<EntityType<PlayerStandEntity>> PLAYER_STAND_ENTITY =
            ENTITY_TYPES.register("player_stand_entity",
                    () -> EntityType.Builder.<PlayerStandEntity>of(PlayerStandEntity::new, MobCategory.MISC)
                            .sized(0.5f, 1.975f)
                            .clientTrackingRange(10)
                            .build("player_stand_entity"));

}
