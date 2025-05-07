package com.ordwen.odailyquests.externs.hooks.points;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;

public class TokenManagerHook {

    private TokenManagerHook() {}

    private static TokenManager tokenManagerAPI;

    /**
     * Setup TokenManagerAPI.
     */
    public static void setupTokenManager() {
        tokenManagerAPI = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");
    }

    /**
     * Get TokenManager api.
     * @return api.
     */
    public static TokenManager getTokenManagerAPI() {
        return tokenManagerAPI;
    }
}
