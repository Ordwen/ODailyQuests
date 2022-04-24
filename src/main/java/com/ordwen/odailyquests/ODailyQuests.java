package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.*;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.quests.player.progression.ValidateVillagerTradeQuest;
import com.ordwen.odailyquests.rewards.GlobalReward;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.Metrics;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.LoadProgressionYAML;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.SaveProgressionYAML;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.MySQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.SaveProgressionSQL;
import com.ordwen.odailyquests.tools.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class ODailyQuests extends JavaPlugin {

    /**
     * Getting instance of files classes.
     */
    public ConfigurationFiles configurationFiles;
    public QuestsFiles questsFiles;
    private ProgressionFile progressionFile;
    public LoadQuests loadQuests;
    private Items items;
    public InterfacesManager interfacesManager;
    public GlobalQuestsInterface globalQuestsInterface;
    public PlayerQuestsInterface playerQuestsInterface;
    public CategorizedQuestsInterfaces categorizedQuestsInterfaces;
    private QuestsManager questsManager;
    private LoadProgressionYAML loadProgressionYAML;
    private SaveProgressionYAML saveProgressionYAML;
    private ProgressionManager progressionManager;
    private CitizensAPI citizensAPI;
    private MySQLManager mySqlManager;
    private GlobalReward globalReward;
    private RewardManager rewardManager;
    private LoadProgressionSQL loadProgressionSQL = null;
    private SaveProgressionSQL saveProgressionSQL = null;

    /* Technical items */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");

        /* Check for update */
        logger.info(ChatColor.GOLD + "Checking for update...");
        new UpdateChecker(this, 100990).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                logger.info(ChatColor.GREEN + "Plugin is up to date.");
            } else {
                logger.info(ChatColor.GOLD + "A new update is available !");
                logger.info(ChatColor.GOLD + "Current version : " + ChatColor.RED + this.getDescription().getVersion() + ChatColor.GOLD + ", Available version : " + ChatColor.GREEN + version);
                logger.info(ChatColor.GOLD + "Please download latest version :");
                logger.info(ChatColor.GOLD + "https://www.spigotmc.org/resources/odailyquests.100990/");
            }
        });

        /* Load Metrics */
        // https://bstats.org/plugin/bukkit/ODailyQuests/14277
        int pluginId = 14277;
        Metrics metrics = new Metrics(this, pluginId);

        /* Load configuration files */
        this.configurationFiles = new ConfigurationFiles(this);
        configurationFiles.loadConfigurationFiles();
        configurationFiles.loadMessagesFiles();

        /* Load SQL Support */
        if (configurationFiles.getConfigFile().getString("storage_mode").equals("MySQL")) {
            this.mySqlManager = new MySQLManager(configurationFiles, 10);
            this.loadProgressionSQL = new LoadProgressionSQL(mySqlManager);
            this.saveProgressionSQL = new SaveProgressionSQL(mySqlManager);

            mySqlManager.setupDatabase();
        }

        /* Load class instances */
        this.questsFiles = new QuestsFiles(this);
        this.progressionFile = new ProgressionFile(this);
        this.loadQuests = new LoadQuests(questsFiles, configurationFiles);
        this.items = new Items(configurationFiles);
        this.globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);
        this.playerQuestsInterface = new PlayerQuestsInterface(configurationFiles);
        this.categorizedQuestsInterfaces = new CategorizedQuestsInterfaces(configurationFiles);
        this.interfacesManager = new InterfacesManager(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);
        this.questsManager = new QuestsManager(configurationFiles, loadProgressionSQL, saveProgressionSQL);
        this.globalReward = new GlobalReward(configurationFiles);
        this.rewardManager = new RewardManager(configurationFiles);
        this.loadProgressionYAML = new LoadProgressionYAML(progressionFile);
        this.saveProgressionYAML = new SaveProgressionYAML(progressionFile);
        this.progressionManager = new ProgressionManager();
        this.citizensAPI = new CitizensAPI(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);

        /* Load dependencies */

        // VAULT - CMI
        if (!VaultAPI.setupEconomy()) {
            logger.info(ChatColor.RED + "No compatible plugin detected for reward type 'MONEY'.");
            logger.info(ChatColor.RED + "Quests with reward type 'MONEY' will not work.");
        } else {
            logger.info(ChatColor.YELLOW + "Vault" + ChatColor.GREEN + " successfully hooked.");
        }

        // TOKENMANAGER - PLAYERPOINTS
        if (!TokenManagerAPI.setupTokenManager()) {
            PlayerPoints.setupPlayerPointsAPI();
            if (PlayerPoints.isPlayerPointsSetup()) {
                logger.info(ChatColor.YELLOW + "PlayerPoints" + ChatColor.GREEN + " successfully hooked.");
            } else {
                logger.info(ChatColor.RED + "No compatible plugin detected for reward type 'POINTS'.");
                logger.info(ChatColor.RED + "Quests with reward type 'POINTS' will not work.");
            }
        } else {
            logger.info(ChatColor.YELLOW + "TokenManager" + ChatColor.GREEN + " successfully hooked.");
        }

        // CITIZENS
        if (CitizensAPI.setupCitizens()) {
            getServer().getPluginManager().registerEvents(citizensAPI, this);
            logger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GREEN + " successfully hooked.");
        } else logger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GOLD + " not detected. NPCs will not work.");

        // ELITEMOBS
        if (EliteMobsAPI.isEliteMobsSetup()) {
            logger.info(ChatColor.YELLOW + "EliteMobs" + ChatColor.GREEN + " successfully hooked.");
            getServer().getPluginManager().registerEvents(new EliteMobsAPI(), this);
        }

        // MYTHICMOBS
        if (MythicMobsHook.isMythicMobsSetup()) {
            logger.info(ChatColor.YELLOW + "MythicMobs" + ChatColor.GREEN + " successfully hooked.");
            getServer().getPluginManager().registerEvents(new MythicMobsHook(), this);
        }

        /* Load files */
        questsFiles.loadQuestsFiles();
        progressionFile.loadProgressionFile();

        /* Load quests */
        loadQuests.loadCategories();

        /* Load global reward */
        globalReward.initGlobalReward();

        /* Load interfaces */
        items.initItems();
        interfacesManager.initInventoryNames();
        playerQuestsInterface.loadPlayerQuestsInterface();

        if (configurationFiles.getConfigFile().getInt("quests_mode") == 2)
            categorizedQuestsInterfaces.loadCategorizedInterfaces();
        else globalQuestsInterface.loadGlobalQuestsInterface();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            logger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GREEN + " successfully hooked.");
        } else
            logger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GOLD + " not detected. Placeholders will not work.");

        /* Load commands */
        getCommand("quests").setExecutor(new PlayerCommands(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces));
        getCommand("questsadmin").setExecutor(new AdminCommands(this));

        /* Load Tab Completers */
        getCommand("quests").setTabCompleter(new PlayerCompleter());
        getCommand("questsadmin").setTabCompleter(new AdminCompleter());

        /* Load listeners */
        getServer().getPluginManager().registerEvents(new ValidateVillagerTradeQuest(), this);
        getServer().getPluginManager().registerEvents(interfacesManager, this);
        getServer().getPluginManager().registerEvents(questsManager, this);
        getServer().getPluginManager().registerEvents(progressionManager, this);

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
                    logger.log(Level.SEVERE, "Impossible to load player quests : the selected storage mode is incorrect !");
                    break;
            }
            logger.log(Level.WARNING, "It seems that you have reloaded the server.");
            logger.log(Level.WARNING, "Think that this can cause problems, especially in the data backup.");
            logger.log(Level.WARNING, "You should restart the server instead.");
        }
        logger.info(ChatColor.GREEN + "Plugin is started !");
    }

    @Override
    public void onDisable() {

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
                    logger.log(Level.SEVERE, "Impossible to save player quests : the selected storage mode is incorrect !");
                    break;
            }
        }

        logger.info(ChatColor.RED + "Plugin is shutting down...");
    }

}

