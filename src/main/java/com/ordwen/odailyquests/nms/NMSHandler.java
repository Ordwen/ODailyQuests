package com.ordwen.odailyquests.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class NMSHandler {

    private static final UUID DUMMY_UUID = UUID.randomUUID();

    private static NMSHandler instance;
    private final String version;

    private NMSHandler() {
        this.version = Bukkit.getBukkitVersion().split("-")[0];
    }

    public static NMSHandler getInstance() {
        if (instance == null) {
            instance = new NMSHandler();
        }

        return instance;
    }

    public boolean isVersionAtLeast(String versionPrefix) {
        return version.compareTo(versionPrefix) >= 0;
    }

    public static SkullMeta applySkullTexture(SkullMeta skullMeta, String texture) {
        if (getInstance().isVersionAtLeast("1.18.1")) {
            return applyTextureModern(skullMeta, texture);
        } else {
            return applyTextureLegacy(skullMeta, texture);
        }
    }

    private static SkullMeta applyTextureModern(SkullMeta skullMeta, String texture) {
        final PlayerProfile profile = Bukkit.createPlayerProfile(DUMMY_UUID);
        final PlayerTextures textures = profile.getTextures();

        final URL url;
        try {
            url = new URL("http://textures.minecraft.net/texture/" + texture);
        } catch (MalformedURLException e) {
            PluginLogger.error("Failed to apply skull texture: " + e.getMessage());
            return skullMeta;
        }

        textures.setSkin(url);
        profile.setTextures(textures);
        skullMeta.setOwnerProfile(profile);

        return skullMeta;
    }

    private static SkullMeta applyTextureLegacy(SkullMeta skullMeta, String texture) {
        final GameProfile profile = new GameProfile(DUMMY_UUID, "odq_skull");
        final String toEncode = "{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/" + texture + "\"}}}";
        final byte[] data = Base64.getEncoder().encodeToString(toEncode.getBytes()).getBytes();
        profile.getProperties().put("textures", new Property("textures", new String(data)));

        try {
            final Method setProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfileMethod.setAccessible(true);
            setProfileMethod.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            PluginLogger.error("Failed to apply skull texture: " + e.getMessage());
        }

        return skullMeta;
    }

    public String getVersion() {
        return version;
    }
}
