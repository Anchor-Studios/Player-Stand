package com.anchorstudios.playerstand;

import net.minecraft.server.level.ServerPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SkinFetcher {
    private static final File SKIN_FOLDER = new File("playerstand/skins");

    public static void checkAndSaveSkin(ServerPlayer player) {
        try {
            String playerName = player.getGameProfile().getName();
            UUID uuid = getOfflineOrOnlineUUID(player);

            if (!SKIN_FOLDER.exists()) {
                SKIN_FOLDER.mkdirs();
            }

            File outFile = new File(SKIN_FOLDER, playerName + ".png");

            // Try fetching from Mojang if online UUID, else skip
            if (isOnlineUUID(uuid)) {
                String skinUrl = fetchSkinUrl(uuid);
                if (skinUrl != null) {
                    BufferedImage newImage = ImageIO.read(new URL(skinUrl));
                    if (newImage != null) {
                        // Save skin locally if changed or new
                        if (!outFile.exists() || !imagesAreEqual(outFile, newImage)) {
                            ImageIO.write(newImage, "png", outFile);
                            System.out.println("Saved skin for " + playerName + " from Mojang.");
                        }
                        return;
                    }
                }
                // If fail to fetch online skin, try local fallback below
            }

            // Offline or failed to fetch: try loading local skin file
            if (outFile.exists()) {
                System.out.println("Using existing local skin for " + playerName);
            } else {
                System.out.println("No skin found for " + playerName + ", using default.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static UUID getOfflineOrOnlineUUID(ServerPlayer player) {
        UUID uuid = player.getGameProfile().getId();
        if (uuid == null || !isOnlineUUID(uuid)) {
            // Generate offline UUID from player name
            String playerName = player.getGameProfile().getName();
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
            System.out.println("Generated offline UUID for " + playerName + ": " + uuid);
        }
        return uuid;
    }

    private static boolean isOnlineUUID(UUID uuid) {
        // Online UUIDs are version 4
        return uuid.version() == 4;
    }

    private static String fetchSkinUrl(UUID uuid) {
        // Return null if failed or offline UUID
        try {
            String id = uuid.toString().replace("-", "");
            java.net.URL url = new java.net.URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id);
            try (java.io.InputStream is = url.openStream();
                 java.io.InputStreamReader isr = new java.io.InputStreamReader(is)) {
                com.google.gson.JsonObject profile = com.google.gson.JsonParser.parseReader(isr).getAsJsonObject();
                String value = profile.getAsJsonArray("properties").get(0)
                        .getAsJsonObject().get("value").getAsString();
                String decoded = new String(java.util.Base64.getDecoder().decode(value));
                com.google.gson.JsonObject textures = com.google.gson.JsonParser.parseString(decoded).getAsJsonObject();
                return textures.getAsJsonObject("textures").getAsJsonObject("SKIN")
                        .get("url").getAsString();
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch skin URL for UUID " + uuid);
            return null;
        }
    }

    private static boolean imagesAreEqual(File file, BufferedImage image) {
        try {
            BufferedImage oldImage = ImageIO.read(file);
            if (oldImage == null) return false;
            if (oldImage.getWidth() != image.getWidth() || oldImage.getHeight() != image.getHeight()) return false;
            for (int x = 0; x < oldImage.getWidth(); x++) {
                for (int y = 0; y < oldImage.getHeight(); y++) {
                    if (oldImage.getRGB(x, y) != image.getRGB(x, y)) return false;
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
