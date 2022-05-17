package com.ordwen.odailyquests.apis.hooks.points;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;

public class TokenManagerHook {

    private static TokenManager tokenManagerAPI;

    /**
     * Setup TokenManagerAPI.
     */
    public static boolean setupTokenManager() {
        tokenManagerAPI = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
        return tokenManagerAPI != null;
    }

    /**
     * Get TokenManager api.
     * @return api.
     */
    public static TokenManager getTokenManagerAPI() {
        return tokenManagerAPI;
    }
}
