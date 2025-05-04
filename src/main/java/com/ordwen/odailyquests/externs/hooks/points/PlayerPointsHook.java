package com.ordwen.odailyquests.externs.hooks.points;

import com.ordwen.odailyquests.tools.PluginUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

public class PlayerPointsHook {

    private PlayerPointsHook() {}

    private static PlayerPointsAPI playerPointsAPI;

    /**
     * Setup TokenManagerAPI.
     */
    public static boolean setupPlayerPointsAPI() {
        if (PluginUtils.isPluginEnabled("PlayerPoints")) {
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
