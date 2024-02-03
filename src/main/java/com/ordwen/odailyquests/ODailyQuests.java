package com.ordwen.odailyquests;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.commands.RestartHandler;
import com.ordwen.odailyquests.externs.IntegrationsManager;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.ReloadService;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.InventoryClickListener;
import com.ordwen.odailyquests.configuration.ConfigurationManager;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.events.EventsManager;
import com.ordwen.odailyquests.files.*;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllCategoryQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.QuestCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.QuestProgressListener;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.h2.H2Manager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.tools.*;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public final class ODailyQuests extends JavaPlugin {

    public static ODailyQuests INSTANCE;
    private ODailyQuestsAPI API;

    /**
     * Getting instance of files classes.
     */
    private ConfigurationFiles configurationFiles;
    private ConfigurationManager configurationManager;
    private InterfacesManager interfacesManager;
    private FilesManager filesManager;
    private SQLManager sqlManager;
    private YamlManager yamlManager;
    private TimerTask timerTask;
    private ReloadService reloadService;
    private CategoriesLoader categoriesLoader;

    boolean isServerStopping = false;

    @Override
    public void onEnable() {
        INSTANCE = this;
        API = new ODailyQuestsAPI(this);

        PluginLogger.info("Plugin is starting...");

        /* Load Metrics */
        // https://bstats.org/plugin/bukkit/ODailyQuests/14277
        int pluginId = 14277;
        Metrics metrics = new Metrics(this, pluginId);

        /* Load files */
        this.configurationFiles = new ConfigurationFiles(this);
        this.filesManager = new FilesManager(this);
        this.filesManager.loadAllFiles();

        /* Check for updates */
        new AutoUpdater(this).checkForUpdate(); // LAST USE : 2.1.0 -> 2.1.1
        checkForSpigotUpdate();

        /* Load SQL Support */
        switch (configurationFiles.getConfigFile().getString("storage_mode")) {
            case "MySQL" -> this.sqlManager = new MySQLManager(this);
            case "H2" -> this.sqlManager = new H2Manager(this);
            default -> this.yamlManager = new YamlManager();
        }

        /* Init categories loader */
        this.categoriesLoader = new CategoriesLoader();

        /* Load class instances */
        this.interfacesManager = new InterfacesManager(this);
        this.configurationManager = new ConfigurationManager(this);
        this.reloadService = new ReloadService(this, sqlManager != null);

        /* Load dependencies */
        new IntegrationsManager(this).loadAllDependencies();

        /* Load debugger */
        new Debugger(this).loadDebugMode();

        /* Load all elements */
        reloadService.reload();

        /* Load listeners */
        new EventsManager(this).registerListeners();

        /* Load commands */
        getCommand("dquests").setExecutor(new PlayerCommands(this));
        getCommand("dqadmin").setExecutor(new AdminCommands(this));

        /* Load Tab Completers */
        getCommand("dquests").setTabCompleter(new PlayerCompleter());
        getCommand("dqadmin").setTabCompleter(new AdminCompleter());

        /* Register plugin events */
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new QuestsManager(this, sqlManager != null), this);
        getServer().getPluginManager().registerEvents(new QuestProgressListener(), this);
        getServer().getPluginManager().registerEvents(new QuestCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllQuestsCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllCategoryQuestsCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new RestartHandler(this), this);

        /* Avoid errors on reload */
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            reloadService.loadConnectedPlayerQuests();

            PluginLogger.error("It seems that you have reloaded the server.");
            PluginLogger.error("Think that this can cause problems, especially in the data backup.");
            PluginLogger.error("You should restart the server instead.");
        }

        /* Init delayed task to draw new quests */
        if (Modes.getTimestampMode() == 1 && Temporality.getTemporalityMode() == 1) {
            timerTask = new TimerTask(LocalDateTime.now());
        }

        PluginLogger.info("Plugin is started!");
    }

    @Override
    public void onDisable() {

        if (timerTask != null) timerTask.stop();

        /* Avoid errors on reload */
        reloadService.saveConnectedPlayerQuests(false);

        if (sqlManager != null) sqlManager.close();
        PluginLogger.info(ChatColor.RED + "Plugin is shutting down...");
    }

    /**
     * Check if an update is available.
     */
    private void checkForSpigotUpdate() {
        PluginLogger.info("Checking for update...");
        new UpdateChecker(this, 100990).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                PluginLogger.info("Plugin is up to date.");
            } else {
                PluginLogger.warn("A new update is available !");
                PluginLogger.warn("Current version : " + this.getDescription().getVersion() + ", Available version : " + version);
                PluginLogger.warn("Please download latest version :");
                PluginLogger.warn("https://www.spigotmc.org/resources/odailyquests.100990/");
            }
        });
    }

    /**
     * Check if the server is stopping.
     * @return true if the server is stopping.
     */
    public boolean isServerStopping() {
        return this.isServerStopping;
    }

    /**
     * Set if the server is stopping.
     * @param isServerStopping true if the server is stopping.
     */
    public void setServerStopping(boolean isServerStopping) {
        this.isServerStopping = isServerStopping;
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
    public SQLManager getSQLManager() {
        return sqlManager;
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

    /**
     * Get YamlManager instance.
     * @return YamlManager instance.
     */
    public YamlManager getYamlManager() {
        return yamlManager;
    }

    /**
     * Get QuestsLoader instance.
     * @return QuestsLoader instance.
     */
    public CategoriesLoader getCategoriesLoader() {
        return categoriesLoader;
    }

    /**
     * Get ODailyQuestsAPI instance.
     * @return ODailyQuestsAPI instance.
     */
    public ODailyQuestsAPI getAPI() {
        return API;
    }
}

