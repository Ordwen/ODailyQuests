package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class Items {

    /* instance */
    private static ConfigurationFiles configurationFiles;

    /**
     * Constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public Items(ConfigurationFiles configurationFiles) {
        Items.configurationFiles = configurationFiles;
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
        previousMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".previous_item_name"))));
        previous.setItemMeta(previousMeta);
    }

    /**
     * Init next button.
     */
    private void initNextButton() {
        next = getCustomHead("6d865aae2746a9b8e9a4fe629fb08d18d0a9251e5ccbe5fa7051f53eab9b94");
        final ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".next_item_name"))));
        next.setItemMeta(nextMeta);
    }

    public static ItemStack getCustomHead(String texture) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "ODQ");

        String toEncore = "{textures:{SKIN:{url:\"https://textures.minecraft.net/texture/" + texture + "\"}}}";
        byte[] data = Base64.getEncoder().encodeToString(toEncore.getBytes()).getBytes();

        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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
