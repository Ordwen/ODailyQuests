package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.IntegrationsManager;
import com.ordwen.odailyquests.apis.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.InventoryClickListener;
import com.ordwen.odailyquests.configuration.ConfigurationManager;
import com.ordwen.odailyquests.files.*;
import com.ordwen.odailyquests.quests.player.progression.ValidateVillagerTradeQuest;
import com.ordwen.odailyquests.tools.Metrics;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.LoadProgressionYAML;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.SaveProgressionYAML;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.MySQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.SaveProgressionSQL;
import com.ordwen.odailyquests.tools.TimerTask;
import com.ordwen.odailyquests.tools.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
    private LoadProgressionSQL loadProgressionSQL = null;
    private SaveProgressionSQL saveProgressionSQL = null;
    private TimerTask timerTask;

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
            MySQLManager mySqlManager = new MySQLManager(configurationFiles, 10);
            this.loadProgressionSQL = new LoadProgressionSQL(mySqlManager);
            this.saveProgressionSQL = new SaveProgressionSQL(mySqlManager);

            mySqlManager.setupDatabase();
        }

        /* Load files */
        this.filesManager = new FilesManager(this);
        this.filesManager.loadAllFiles();

        /* Load class instances */
        this.interfacesManager = new InterfacesManager(configurationFiles);
        this.configurationManager = new ConfigurationManager(configurationFiles);

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
        getCommand("quests").setExecutor(new PlayerCommands(configurationFiles));
        getCommand("questsadmin").setExecutor(new AdminCommands(this));

        /* Load Tab Completers */
        getCommand("quests").setTabCompleter(new PlayerCompleter());
        getCommand("questsadmin").setTabCompleter(new AdminCompleter());

        /* Load listeners */
        getServer().getPluginManager().registerEvents(new ValidateVillagerTradeQuest(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new QuestsManager(configurationFiles, loadProgressionSQL, saveProgressionSQL), this);
        getServer().getPluginManager().registerEvents(new ProgressionManager(), this);

        /* Avoid server/plugin reload errors */
        if (getServer().getOnlinePlayers().size() > 0) {
            switch (configurationFiles.getConfigFile().getString("storage_mode")) {
                case "YAML":
                    for (Player player : getServer().getOnlinePlayers()) {
                        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                            LoadProgressionYAML.loadPlayerQuests(player.getName(), QuestsManager.getActiveQuests(),
                                    configurationFiles.getConfigFile().getInt("quests_mode"),
                                    configurationFiles.getConfigFile().getInt("timestamp_mode"),
                                    configurationFiles.getConfigFile().getInt("temporality_mode"));
                        }
                    }
                    break;
                case "MySQL":
                    for (Player player : getServer().getOnlinePlayers()) {
                        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                            loadProgressionSQL.loadProgression(player.getName(), QuestsManager.getActiveQuests(),
                                    configurationFiles.getConfigFile().getInt("quests_mode"),
                                    configurationFiles.getConfigFile().getInt("timestamp_mode"),
                                    configurationFiles.getConfigFile().getInt("temporality_mode"));
                        }
                    }
                    break;
                default:
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
                    break;
            }
            PluginLogger.error("It seems that you have reloaded the server.");
            PluginLogger.error("Think that this can cause problems, especially in the data backup.");
            PluginLogger.error("You should restart the server instead.");
        }

        /* Init delayed task to draw new quests */
        timerTask = new TimerTask(LocalDateTime.now());

        PluginLogger.info(ChatColor.GREEN + "Plugin is started !");
    }

    @Override
    public void onDisable() {

        timerTask.stop();

        /* Avoid server/plugin reload errors */
        if (getServer().getOnlinePlayers().size() > 0) {
            switch (configurationFiles.getConfigFile().getString("storage_mode")) {
                case "YAML":
                    for (Player player : getServer().getOnlinePlayers()) {
                        SaveProgressionYAML.saveProgression(player.getName(), QuestsManager.getActiveQuests());
                        QuestsManager.getActiveQuests().remove(player.getName());
                    }
                    break;
                case "MySQL":
                    for (Player player : getServer().getOnlinePlayers()) {
                        saveProgressionSQL.saveProgression(player.getName(), QuestsManager.getActiveQuests());
                        QuestsManager.getActiveQuests().remove(player.getName());
                    }
                    break;
                default:
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
                    break;
            }
        }

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

}

