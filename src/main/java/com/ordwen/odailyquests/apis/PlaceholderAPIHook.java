package com.ordwen.odailyquests.apis;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    public static boolean setupPlaceholderAPI() {
        me.clip.placeholderapi.PlaceholderAPI placeholderAPI = (me.clip.placeholderapi.PlaceholderAPI) Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        return placeholderAPI != null;
    }

    @Override
    public String getIdentifier() {
        return "%";
    }

    @Override
    public String getAuthor() {
        return "Ordwen";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
