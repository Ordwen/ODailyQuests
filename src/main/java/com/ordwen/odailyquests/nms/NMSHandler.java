package com.ordwen.odailyquests.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public final class NMSHandler {

    /**
     * Dummy UUID used for synthetic profiles (skull textures).
     * <p>Only used to satisfy the profile constructor APIs; it does not represent a real player.</p>
     */
    private static final UUID DUMMY_UUID = UUID.randomUUID();

    /**
     * Bukkit version prefix (e.g. {@code "1.20.4"}) extracted from {@code Bukkit.getBukkitVersion()}.
     */
    private static final String VERSION = Bukkit.getBukkitVersion().split("-")[0];

    // null = unknown yet; true = supported; false = not supported
    private static volatile Boolean hasSetItemModel = null;
    private static volatile Boolean hasHasItemModel = null;
    private static volatile Boolean hasGetItemModel = null;

    private NMSHandler() {
        // Utility class
    }

    /**
     * @return the Bukkit version prefix (e.g. {@code "1.20.4"}).
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Checks whether the current server version is at least the provided prefix.
     *
     * <p>This is a simple lexicographical comparison ({@link String#compareTo(String)}).</p>
     *
     * @param versionPrefix a version prefix like {@code "1.18.1"}
     * @return {@code true} if {@link #getVersion()} is lexicographically >= {@code versionPrefix}
     */
    public static boolean isVersionAtLeast(String versionPrefix) {
        return VERSION.compareTo(versionPrefix) >= 0;
    }

    /**
     * Tries to set the {@code item_model} of an {@link ItemMeta} using reflection.
     *
     * @param meta the item meta to mutate
     * @param itemModel the model key in {@code namespace:value} form (namespace optional)
     * @return {@code true} if the model was applied, {@code false} if unsupported or invalid input
     */
    public static boolean trySetItemModel(ItemMeta meta, String itemModel) {
        if (Boolean.FALSE.equals(hasSetItemModel)) return false;
        if (meta == null || itemModel == null || itemModel.isEmpty()) return false;

        final String namespace;
        final String value;
        final int colon = itemModel.indexOf(':');
        if (colon >= 0) {
            namespace = itemModel.substring(0, colon);
            value = itemModel.substring(colon + 1);
        } else {
            namespace = NamespacedKey.MINECRAFT; // "minecraft"
            value = itemModel;
        }

        if (namespace.isEmpty() || value.isEmpty()) return false;

        final NamespacedKey key;
        try {
            key = new NamespacedKey(namespace, value);
        } catch (IllegalArgumentException e) {
            // namespace/value invalid (chars, length, etc.)
            return false;
        }

        try {
            final Method m = ItemMeta.class.getMethod("setItemModel", NamespacedKey.class);
            m.invoke(meta, key);
            hasSetItemModel = true;
            return true;
        } catch (NoSuchMethodException e) {
            hasSetItemModel = false;
            Debugger.write("ItemMeta#setItemModel not present; feature disabled.");
            return false;
        } catch (Exception e) {
            hasSetItemModel = true;
            PluginLogger.error("Failed to set item model '" + namespace + ":" + value + "': "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Indicates whether the provided {@link ItemMeta} declares a custom {@code item_model}.
     *
     * <p>Uses reflection to remain compatible with server versions that do not provide
     * {@link ItemMeta#hasItemModel()}.</p>
     *
     * @param meta item metadata to inspect
     * @return {@code true} if an item model is present; {@code false} otherwise or on unsupported versions
     */
    public static boolean hasItemModel(ItemMeta meta) {
        if (Boolean.FALSE.equals(hasHasItemModel)) return false;
        if (meta == null) return false;

        try {
            final Method m = ItemMeta.class.getMethod("hasItemModel");
            final boolean result = (boolean) m.invoke(meta);
            hasHasItemModel = true;
            return result;
        } catch (NoSuchMethodException e) {
            hasHasItemModel = false;
            Debugger.write("ItemMeta#hasItemModel not present; feature disabled.");
        } catch (Exception e) {
            hasHasItemModel = true;
            PluginLogger.error("Failed to query item model presence: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        return false;
    }

    /**
     * Retrieves the {@link NamespacedKey} associated with an item's model.
     *
     * <p>Uses reflection to call {@code ItemMeta#getItemModel()} only when available.</p>
     *
     * @param meta item metadata to inspect
     * @return the model key, or {@code null} if unavailable or unsupported
     */
    public static NamespacedKey getItemModel(ItemMeta meta) {
        if (Boolean.FALSE.equals(hasGetItemModel)) return null;
        if (meta == null) return null;

        try {
            final Method m = ItemMeta.class.getMethod("getItemModel");
            final NamespacedKey key = (NamespacedKey) m.invoke(meta);
            hasGetItemModel = true;
            return key;
        } catch (NoSuchMethodException e) {
            hasGetItemModel = false;
            Debugger.write("ItemMeta#getItemModel not present; feature disabled.");
        } catch (Exception e) {
            hasGetItemModel = true;
            PluginLogger.error("Failed to read item model: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        return null;
    }

    /**
     * Applies a Mojang skin texture to the provided {@link SkullMeta}.
     *
     * <p>The {@code texture} argument must be the texture hash/id (not base64),
     * for example {@code "f84c6a790a4e..."}. The final URL is built as:
     * {@code https://textures.minecraft.net/texture/<texture>}.</p>
     *
     * <p>Implementation is selected based on server version:
     * modern profile API for {@code >= 1.18.1}, otherwise legacy {@link GameProfile} injection.</p>
     *
     * @param skullMeta the skull meta to mutate
     * @param texture the Mojang texture id (hash)
     * @return the same meta instance, for chaining
     */
    public static SkullMeta applySkullTexture(SkullMeta skullMeta, String texture) {
        if (isVersionAtLeast("1.18.1")) {
            return applyTextureModern(skullMeta, texture);
        } else {
            return applyTextureLegacy(skullMeta, texture);
        }
    }

    /**
     * Modern skull texture application using Bukkit's profile API (Paper/Spigot recent versions).
     */
    private static SkullMeta applyTextureModern(SkullMeta skullMeta, String texture) {
        final PlayerProfile profile = Bukkit.createPlayerProfile(DUMMY_UUID);
        final PlayerTextures textures = profile.getTextures();

        final URL url;
        try {
            url = URI.create("https://textures.minecraft.net/texture/" + texture).toURL();
        } catch (MalformedURLException e) {
            PluginLogger.error("Failed to apply skull texture: " + e.getMessage());
            return skullMeta;
        }

        textures.setSkin(url);
        profile.setTextures(textures);
        skullMeta.setOwnerProfile(profile);

        return skullMeta;
    }

    /**
     * Legacy skull texture application by injecting a {@link GameProfile} and invoking
     * the internal {@code setProfile(GameProfile)} method via reflection.
     */
    private static SkullMeta applyTextureLegacy(SkullMeta skullMeta, String texture) {
        final GameProfile profile = new GameProfile(DUMMY_UUID, "odq_skull");
        final String toEncode = "{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/" + texture + "\"}}}";
        final String encoded = Base64.getEncoder().encodeToString(toEncode.getBytes());
        profile.getProperties().put("textures", new Property("textures", encoded));

        try {
            final Method setProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfileMethod.setAccessible(true);
            setProfileMethod.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            PluginLogger.error("Failed to apply skull texture: " + e.getMessage());
        }

        return skullMeta;
    }
}
