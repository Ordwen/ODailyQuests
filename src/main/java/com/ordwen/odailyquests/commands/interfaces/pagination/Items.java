package com.ordwen.odailyquests.commands.interfaces.pagination;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.UUID;

public class Items {

    private final ConfigurationFiles configurationFiles;

    public Items(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static ItemStack previous;
    private static ItemStack next;

    public void initItems() {
        try {
            initPreviousButton();
            initNextButton();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void initPreviousButton() throws MalformedURLException {
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

    public static ItemStack getPreviousButton() {
        return previous;
    }

    public static ItemStack getNextButton() {
        return next;
    }

}
