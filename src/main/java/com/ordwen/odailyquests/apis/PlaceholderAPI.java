package com.ordwen.odailyquests.apis;

import org.bukkit.Bukkit;

public class PlaceholderAPI {

    public static boolean setupPlaceholderAPI() {
        me.clip.placeholderapi.PlaceholderAPI placeholderAPI = (me.clip.placeholderapi.PlaceholderAPI) Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        return placeholderAPI != null;
    }
}
