package com.ordwen.odailyquests.commands.interfaces.pagination;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    private static ItemStack playerHead;
    private static SkullMeta skullMeta;

    /**
     * Load all items.
     */
    public void initItems() {
        initPreviousButton();
        initNextButton();
        initPlayerHead();
    }

    /**
     * Init previous button.
     */
    private void initPreviousButton() {
        previous = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta previousMeta = (SkullMeta) previous.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

        String url = "http://textures.minecraft.net/texture/a2f0425d64fdc8992928d608109810c1251fe243d60d175bed427c651cbe";
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

        Field profileField;
        try {
            profileField = previousMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(previousMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        previousMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".previous_item_name")));
        previous.setItemMeta(previousMeta);
    }

    /**
     * Init next button.
     */
    private void initNextButton() {
        next = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta nextMeta = (SkullMeta) next.getItemMeta();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

        String url = "http://textures.minecraft.net/texture/6d865aae2746a9b8e9a4fe629fb08d18d0a9251e5ccbe5fa7051f53eab9b94";
        byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

        Field profileField;
        try {
            profileField = nextMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(nextMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        nextMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("interfaces").getString(".next_item_name")));
        next.setItemMeta(nextMeta);
    }

    /**
     * Init player head.
     */
    private void initPlayerHead() {
        playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests.player_head").getString(".item_name")));
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

    /**
     * Get player head.
     *
     * @return player head.
     */
    public static ItemStack getPlayerHead(Player player) {

        skullMeta.setOwnerProfile(player.getPlayerProfile());

        List<String> itemDesc = configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests.player_head").getStringList(".item_description");
        for (String string : itemDesc) {
            itemDesc.set(itemDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', string
                    .replace("%achieved%", String.valueOf(QuestsManager.getActiveQuests().get(player.getName()).getAchievedQuests()))
                    .replace("%drawIn%", PlayerQuestsInterface.timeRemain(player.getName()))));
        }

        skullMeta.setLore(itemDesc);
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }
}
