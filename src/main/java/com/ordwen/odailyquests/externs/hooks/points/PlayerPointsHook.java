package com.ordwen.odailyquests.externs.hooks.points;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;

public class PlayerPointsHook {

    private static PlayerPointsAPI playerPointsAPI;

    /**
     * Setup TokenManagerAPI.
     */
    public static boolean setupPlayerPointsAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = PlayerPoints.getInstance().getAPI();
            return true;
        }
        return false;
    }

    public static boolean isPlayerPointsSetup() {
        return playerPointsAPI != null;
    }

    public static PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }
}
