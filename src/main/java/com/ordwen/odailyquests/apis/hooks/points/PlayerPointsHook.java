package com.ordwen.odailyquests.apis.hooks.points;

import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;

public class PlayerPointsHook {

    private static PlayerPointsAPI playerPointsAPI;

    /**
     * Setup TokenManagerAPI.
     */
    public static void setupPlayerPointsAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            playerPointsAPI = org.black_ixx.playerpoints.PlayerPoints.getInstance().getAPI();
        }
    }

    public static boolean isPlayerPointsSetup() {
        return playerPointsAPI != null;
    }

    public static PlayerPointsAPI getPlayerPointsAPI() {
        return playerPointsAPI;
    }
}
