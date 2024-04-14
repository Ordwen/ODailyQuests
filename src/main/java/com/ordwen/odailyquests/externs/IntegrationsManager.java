package com.ordwen.odailyquests.externs;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.externs.hooks.eco.CoinsEngineHook;
import com.ordwen.odailyquests.externs.hooks.eco.VaultHook;
import com.ordwen.odailyquests.externs.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.externs.hooks.mobs.EliteMobsHook;
import com.ordwen.odailyquests.externs.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.externs.hooks.npcs.CitizensHook;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIExpansion;
import com.ordwen.odailyquests.externs.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.externs.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.externs.hooks.stackers.RoseStackerHook;
import com.ordwen.odailyquests.externs.hooks.stackers.WildStackerHook;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

import static org.bukkit.Bukkit.getServer;

public class IntegrationsManager {

    private final ODailyQuests oDailyQuests;

    public IntegrationsManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Load all dependencies.
     */
    public void loadAllDependencies() {
        loadVault();
        loadCoinsEngine();
        loadEliteMobs();
        loadMythicMobs();
        loadPointsPlugin();
        loadCitizens();
        loadHolographicDisplays();
        loadPAPI();
        loadWildStacker();
        loadRoseStacker();

        new Protection().load();
    }

    /**
     * Load WildStacker.
     */
    private void loadWildStacker() {
        if (WildStackerHook.isWildStackerSetup()) {
            PluginLogger.info("WildStacker successfully hooked.");
        }
    }

    /**
     * Load RoseStacker.
     */
    private void loadRoseStacker() {
        if (RoseStackerHook.isRoseStackerSetup()) {
            PluginLogger.info("RoseStacker successfully hooked.");
        }
    }

    /**
     * Hook - MythicMobs
     */
    private void loadMythicMobs() {
        if (MythicMobsHook.isMythicMobsSetup()) {
            PluginLogger.info("MythicMobs successfully hooked.");
        }
    }

    /**
     * Hook - EliteMobs
     */
    private void loadEliteMobs() {
        if (EliteMobsHook.isEliteMobsSetup()) {
            PluginLogger.info("EliteMobs successfully hooked.");
        }
    }

    /**
     * Hook - TokenManager / PlayerPoints
     */
    private void loadPointsPlugin() {
        if (TokenManagerHook.setupTokenManager()) {
            PluginLogger.info("TokenManager successfully hooked.");
            return;
        }

        if (PlayerPointsHook.setupPlayerPointsAPI()) {
            PluginLogger.info("PlayerPoints successfully hooked.");
        }
    }

    /**
     * Hook - CoinsEngine
     */
    private void loadCoinsEngine() {
        if (CoinsEngineHook.setupCoinsEngineAPI()) {
            PluginLogger.info("CoinsEngine successfully hooked.");
        }
    }

    /**
     * Hook - Vault
     */
    private void loadVault() {
        if (VaultHook.setupEconomy()) {
            PluginLogger.info("Vault successfully hooked.");
        }
    }

    /**
     * Hook - Citizens
     */
    private void loadCitizens() {
        if (CitizensHook.setupCitizens()) {
            getServer().getPluginManager().registerEvents(new CitizensHook(), oDailyQuests);
            PluginLogger.info("Citizens successfully hooked.");
        }
    }

    /**
     * Hook - HolographicDisplays
     */
    private void loadHolographicDisplays() {
        if (HolographicDisplaysHook.isHolographicDisplaysSetup()) {
            PluginLogger.info("HolographicDisplays successfully hooked.");
        }
    }

    /**
     * Hook - PlaceholderAPI
     */
    private void loadPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIExpansion().register();
            PluginLogger.info("PlaceholderAPI successfully hooked.");
        }
    }
}
