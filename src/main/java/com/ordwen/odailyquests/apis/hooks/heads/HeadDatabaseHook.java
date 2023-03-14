package com.ordwen.odailyquests.apis.hooks.heads;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseHook implements Listener {

    private static HeadDatabaseAPI headDatabaseAPI;

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
        headDatabaseAPI = new HeadDatabaseAPI();
    }

    public static boolean isHeadDatabaseHooked() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase");
    }

    public static ItemStack getHeadFromAPI(String id) {
        return headDatabaseAPI.getItemHead(id);
    }
}
