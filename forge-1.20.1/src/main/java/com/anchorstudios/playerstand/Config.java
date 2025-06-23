package com.anchorstudios.playerstand;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Set;

@Mod.EventBusSubscriber(modid = PlayerStand.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // === General Settings ===
    public static final ForgeConfigSpec.BooleanValue ALLOW_PLAYER_BINDING = BUILDER
            .comment("Allow right-clicking the player stand to bind it to the player's skin.")
            .define("allowPlayerBinding", true);

    public static final ForgeConfigSpec.BooleanValue ALLOW_POSE_CHANGING = BUILDER
            .comment("Allow changing poses by shift right-clicking the player stand.")
            .define("allowPoseChanging", true);

    public static final ForgeConfigSpec.BooleanValue ALLOW_RETEXTURE_EXISTING = BUILDER
            .comment("Allow changing the texture of an already skinned Player Stand by right-clicking again.")
            .define("allowRetextureExisting", false);

    public static final ForgeConfigSpec.BooleanValue ALLOW_HEAD_BINDING = BUILDER
            .comment("Allow crafting with head items to turn player stands into those.")
            .define("allowHeadBinding", true);

    public static final ForgeConfigSpec.IntValue MAX_STANDS_PER_CHUNK = BUILDER
            .comment("Maximum number of player stands allowed per chunk (0 = unlimited).")
            .defineInRange("maxStandsPerChunk", 0, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // Loaded values
    public static boolean allowPlayerBinding;
    public static boolean allowPoseChanging;
    public static boolean allowHeadBinding;
    public static boolean allowRetextureExisting;
    public static int maxStandsPerChunk;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        allowPlayerBinding = ALLOW_PLAYER_BINDING.get();
        allowPoseChanging = ALLOW_POSE_CHANGING.get();
        allowHeadBinding = ALLOW_HEAD_BINDING.get();
        maxStandsPerChunk = MAX_STANDS_PER_CHUNK.get();
        allowRetextureExisting = ALLOW_RETEXTURE_EXISTING.get();
    }
}