package com.ordwen.odailyquests.externs.hooks.eco;

import com.ordwen.odailyquests.tools.PluginUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private VaultHook() {
    }

    private static Economy econ = null;

    /**
     * Setup economy.
     */
    public static void setupEconomy() {
        if (!PluginUtils.isPluginEnabled("Vault")) {
            return;
        }

        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        econ = rsp.getProvider();
    }

    /**
     * Get Vault economy.
     *
     * @return economy.
     */
    public static Economy getEconomy() {
        return econ;
    }

}
