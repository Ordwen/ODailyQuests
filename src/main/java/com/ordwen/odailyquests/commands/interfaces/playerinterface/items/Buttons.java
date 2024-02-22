package com.ordwen.odailyquests.commands.interfaces.playerinterface.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Buttons {

    /* instance */
    private static ConfigurationFiles configurationFiles;

    /**
     * Constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public Buttons(ConfigurationFiles configurationFiles) {
        Buttons.configurationFiles = configurationFiles;
    }

    /* init items */
    private static ItemStack previous;
    private static ItemStack next;

    /**
     * Load all items.
     */
    public void initItems() {
        initPreviousButton();
        initNextButton();
    }

    /**
     * Init previous button.
     */
    private void initPreviousButton() {
        previous = getCustomHead("a2f0425d64fdc8992928d608109810c1251fe243d60d175bed427c651cbe");
        final ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.setDisplayName(ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".previous_item_name")));
        previous.setItemMeta(previousMeta);
    }

    /**
     * Init next button.
     */
    private void initNextButton() {
        next = getCustomHead("6d865aae2746a9b8e9a4fe629fb08d18d0a9251e5ccbe5fa7051f53eab9b94");
        final ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".next_item_name")));
        next.setItemMeta(nextMeta);
    }

    public static ItemStack getCustomHead(String texture) {
        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "ODQ");

        final String toEncode = "{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/" + texture + "\"}}}";
        final byte[] data = Base64.getEncoder().encodeToString(toEncode.getBytes()).getBytes();

        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

        try {
            final Method setProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfileMethod.setAccessible(true);
            setProfileMethod.invoke(skullMeta, gameProfile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            PluginLogger.error(e.getMessage());
        }

        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    /**
     * Get previous button.
     *
     * @return previous button.
     */
    public static ItemStack getPreviousButton() {
        return previous;
    }

    /**
     * Get next button.
     *
     * @return next button.
     */
    public static ItemStack getNextButton() {
        return next;
    }

}
