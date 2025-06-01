package com.anchorstudios.playerstand;

import com.anchorstudios.playerstand.entity.ModEntities;
import com.anchorstudios.playerstand.item.PlayerStandItem;
import com.anchorstudios.playerstand.recipe.ModRecipes;
import com.anchorstudios.playerstand.server.PlayerJoinHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import com.anchorstudios.playerstand.entity.PlayerStandEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(PlayerStand.MODID)
public class PlayerStand {
    public static final String MODID = "playerstand";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Item> PLAYER_STAND_ITEM = ITEMS.register("player_stand",
            () -> new PlayerStandItem(new Item.Properties()
                    .stacksTo(16)
            )
    );

    public static final RegistryObject<CreativeModeTab> PLAYER_STAND_TAB = CREATIVE_MODE_TABS.register("playerstand_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> PLAYER_STAND_ITEM.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.playerstand_tab"))
                    .displayItems((params, output) -> {
                        output.accept(PLAYER_STAND_ITEM.get());
                    })
                    .build()
    );

    public PlayerStand(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        ModRecipes.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);
        // Register mod config
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register for server/game events
        MinecraftForge.EVENT_BUS.register(this);

        // Register common setup (optional)
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(PlayerJoinHandler.class);
    }


    @Mod.EventBusSubscriber(modid = PlayerStand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ModEventHandlers {

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(ModEntities.PLAYER_STAND_ENTITY.get(), PlayerStandEntity.createAttributes().build());
        }
    }


    private void commonSetup(final FMLCommonSetupEvent event) {

        LOGGER.info("Player Stand mod is initializing...");
    }
}