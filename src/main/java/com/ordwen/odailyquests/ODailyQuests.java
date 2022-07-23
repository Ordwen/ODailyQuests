package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.IntegrationsManager;
import com.ordwen.odailyquests.apis.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.ReloadService;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.InventoryClickListener;
import com.ordwen.odailyquests.configuration.ConfigurationManager;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.files.*;
import com.ordwen.odailyquests.quests.player.progression.ValidateVillagerTradeQuest;
import com.ordwen.odailyquests.tools.Metrics;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.MySQLManager;
import com.ordwen.odailyquests.tools.TimerTask;
import com.ordwen.odailyquests.tools.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public final class ODailyQuests extends JavaPlugin {

    public static ODailyQuests INSTANCE;

    /**
     * Getting instance of files classes.
     */
    public ConfigurationFiles configurationFiles;
    public ConfigurationManager configurationManager;
    public InterfacesManager interfacesManager;
    public FilesManager filesManager;
    private MySQLManager mySqlManager;
    private TimerTask timerTask;
    private ReloadService reloadService;

    @Override
    public void onEnable() {
        INSTANCE = this;

        PluginLogger.info(ChatColor.GOLD + "Plugin is starting...");

        checkForUpdate();

        /* Load Metrics */
        // https://bstats.org/plugin/bukkit/ODailyQuests/14277
        int pluginId = 14277;
        Metrics metrics = new Metrics(this, pluginId);

        /* Load configuration files */
        this.configurationFiles = new ConfigurationFiles(this);
        this.configurationFiles.loadConfigurationFiles();
        this.configurationFiles.loadMessagesFiles();

        /* Load SQL Support */
        if (configurationFiles.getConfigFile().getString("storage_mode").equals("MySQL")) {
            mySqlManager = new MySQLManager(this);

            mySqlManager.setupDatabase();
        }

        /* Load files */
        this.filesManager = new FilesManager(this);
        this.filesManager.loadAllFiles();

        /* Load class instances */
        this.interfacesManager = new InterfacesManager(this);
        this.configurationManager = new ConfigurationManager(this);
        this.reloadService = new ReloadService(this, mySqlManager != null);

        /* Load dependencies */
        new IntegrationsManager(this).loadAllDependencies();

        /* Load holograms */
        HologramsManager.loadHolograms();

        /* Load specific settings */
        configurationManager.loadConfiguration();

        /* Load quests */
        LoadQuests.loadCategories();

        /* Load interfaces */
        interfacesManager.initAllObjects();

        /* Load commands */
        getCommand("dquests").setExecutor(new PlayerCommands(this));
        getCommand("dqadmin").setExecutor(new AdminCommands(this));

        /* Load Tab Completers */
        getCommand("dquests").setTabCompleter(new PlayerCompleter());
        getCommand("dqadmin").setTabCompleter(new AdminCompleter());

        /* Load listeners */
        getServer().getPluginManager().registerEvents(new ValidateVillagerTradeQuest(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new QuestsManager(this, mySqlManager != null), this);
        getServer().getPluginManager().registerEvents(new ProgressionManager(), this);

        /* Avoid errors on reload */
        if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
            reloadService.loadConnectedPlayerQuests();

            PluginLogger.error("It seems that you have reloaded the server.");
            PluginLogger.error("Think that this can cause problems, especially in the data backup.");
            PluginLogger.error("You should restart the server instead.");
        }

        /* Init delayed task to draw new quests */
        if (Modes.getTimestampMode() == 1 && Temporality.getTemporalityMode() == 1) {
            timerTask = new TimerTask(LocalDateTime.now());
        }

        PluginLogger.info(ChatColor.GREEN + "Plugin is started !");
    }

    @Override
    public void onDisable() {

        if (timerTask != null) timerTask.stop();

        /* Avoid errors on reload */
        if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
            reloadService.saveConnectedPlayerQuests(false);
        }

        if (mySqlManager != null) mySqlManager.close();
        PluginLogger.info(ChatColor.RED + "Plugin is shutting down...");
    }

    /**
     * Check if an update is available.
     */
    private void checkForUpdate() {
        PluginLogger.info(ChatColor.GOLD + "Checking for update...");
        new UpdateChecker(this, 100990).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                PluginLogger.info(ChatColor.GREEN + "Plugin is up to date.");
            } else {
                PluginLogger.info(ChatColor.GOLD + "A new update is available !");
                PluginLogger.info(ChatColor.GOLD + "Current version : " + ChatColor.RED + this.getDescription().getVersion() + ChatColor.GOLD + ", Available version : " + ChatColor.GREEN + version);
                PluginLogger.info(ChatColor.GOLD + "Please download latest version :");
                PluginLogger.info(ChatColor.GOLD + "https://www.spigotmc.org/resources/odailyquests.100990/");
            }
        });
    }

    /**
     * Get ConfigurationManager instance.
     * @return ConfigurationManager instance.
     */
    public ConfigurationFiles getConfigurationFiles() {
        return configurationFiles;
    }

    /**
     * Get MySQLManager instance.
     * @return MySQLManager instance.
     */
    public MySQLManager getMySqlManager() {
        return mySqlManager;
    }

    /**
     * Get ReloadService instance.
     * @return ReloadService instance.
     */
    public ReloadService getReloadService() {
        return reloadService;
    }

    /**
     * Get FilesManager instance.
     * @return FilesManager instance.
     */
    public FilesManager getFilesManager() {
        return filesManager;
    }

    /**
     * Get InterfacesManager instance.
     * @return InterfacesManager instance.
     */
    public InterfacesManager getInterfacesManager() {
        return interfacesManager;
    }

    /**
     * Get ConfigurationManager instance.
     * @return ConfigurationManager instance.
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }
}

