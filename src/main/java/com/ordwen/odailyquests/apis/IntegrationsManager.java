package com.ordwen.odailyquests.apis;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.apis.hooks.eco.VaultHook;
import com.ordwen.odailyquests.apis.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.apis.hooks.mobs.EliteMobsHook;
import com.ordwen.odailyquests.apis.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.apis.hooks.npcs.CitizensHook;
import com.ordwen.odailyquests.apis.hooks.placeholders.PlaceholderAPIHook;
import com.ordwen.odailyquests.apis.hooks.points.PlayerPointsHook;
import com.ordwen.odailyquests.apis.hooks.points.TokenManagerHook;
import com.ordwen.odailyquests.apis.hooks.stackers.WildStackerHook;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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
        loadEliteMobs();
        loadMythicMobs();
        loadPointsPlugin();
        loadCitizens();
        loadHolographicDisplays();
        loadPAPI();
        loadWildStacker();
        loadRoseStacker();
    }

    /**
     * Load WildStacker.
     */
    private void loadWildStacker() {
        if (WildStackerHook.isWildStackerSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "WildStacker" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Load RoseStacker.
     */
    private void loadRoseStacker() {
        if (WildStackerHook.isWildStackerSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "RoseStacker" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Hook - MythicMobs
     */
    private void loadMythicMobs() {
        if (MythicMobsHook.isMythicMobsSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "MythicMobs" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Hook - EliteMobs
     */
    private void loadEliteMobs() {
        if (EliteMobsHook.isEliteMobsSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "EliteMobs" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Hook - TokenManager / PlayerPoints
     */
    private void loadPointsPlugin() {
        if (!TokenManagerHook.setupTokenManager()) {
            PlayerPointsHook.setupPlayerPointsAPI();
            if (PlayerPointsHook.isPlayerPointsSetup()) {
                PluginLogger.info(ChatColor.YELLOW + "PlayerPoints" + ChatColor.GREEN + " successfully hooked.");
            } else {
                PluginLogger.info(ChatColor.RED + "No compatible plugin detected for reward type 'POINTS'.");
                PluginLogger.info(ChatColor.RED + "Quests with reward type 'POINTS' will not work.");
            }
        } else {
            PluginLogger.info(ChatColor.YELLOW + "TokenManager" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Hook - Vault
     */
    private void loadVault() {
        if (!VaultHook.setupEconomy()) {
            PluginLogger.info(ChatColor.RED + "No compatible plugin detected for reward type 'MONEY'.");
            PluginLogger.info(ChatColor.RED + "Quests with reward type 'MONEY' will not work.");
        } else {
            PluginLogger.info(ChatColor.YELLOW + "Vault" + ChatColor.GREEN + " successfully hooked.");
        }
    }

    /**
     * Hook - Citizens
     */
    private void loadCitizens() {
        if (CitizensHook.setupCitizens()) {
            getServer().getPluginManager().registerEvents(new CitizensHook(), oDailyQuests);
            PluginLogger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GREEN + " successfully hooked.");
        } else
            PluginLogger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GOLD + " not detected. NPCs will not work.");
    }

    /**
     * Hook - HolographicDisplays
     */
    private void loadHolographicDisplays() {
        if (HolographicDisplaysHook.isHolographicDisplaysSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "HolographicDisplays" + ChatColor.GREEN + " successfully hooked.");
        } else
            PluginLogger.info(ChatColor.YELLOW + "HolographicDisplays" + ChatColor.GOLD + " not detected. Holograms will not work.");
    }

    /**
     * Hook - PlaceholderAPI
     */
    private void loadPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            PluginLogger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GREEN + " successfully hooked.");
        } else
            PluginLogger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GOLD + " not detected. Placeholders will not work.");
    }
}
