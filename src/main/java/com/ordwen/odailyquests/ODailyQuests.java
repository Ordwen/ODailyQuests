package com.ordwen.odailyquests;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ODailyQuests extends JavaPlugin {

    Logger logger = getLogger();

    /* test */
    //Quest quest = new Quest(Type.BREAK);

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");
        logger.info(ChatColor.GREEN + "Plugin is started !");

        /* test */
        //logger.info("Type of quest is : " + quest.getType().getTypeName());
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.RED + "Plugin is shutting down...");
    }
}
