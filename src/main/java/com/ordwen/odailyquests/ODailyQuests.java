package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.*;
import com.ordwen.odailyquests.apis.PlaceholderAPIHook;
import com.ordwen.odailyquests.apis.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.apis.holograms.LoadHolograms;
import com.ordwen.odailyquests.apis.holograms.HologramsManager;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.*;
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
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public final class ODailyQuests extends JavaPlugin {

    /**
     * Getting instance of files classes.
     */
    public ConfigurationFiles configurationFiles;
    public QuestsFiles questsFiles;
    private ProgressionFile progressionFile;
    private PlayerInterfaceFile playerInterfaceFile;
    public LoadQuests loadQuests;
    private Items items;
    public InterfacesManager interfacesManager;
    public GlobalQuestsInterface globalQuestsInterface;
    public PlayerQuestsInterface playerQuestsInterface;
    public CategorizedQuestsInterfaces categorizedQuestsInterfaces;
    private QuestsManager questsManager;
    private LoadProgressionYAML loadProgressionYAML;
    private SaveProgressionYAML saveProgressionYAML;
    private CitizensHook citizensHook;
    private MySQLManager mySqlManager;
    private GlobalReward globalReward;
    private RewardManager rewardManager;
    private LoadProgressionSQL loadProgressionSQL = null;
    private SaveProgressionSQL saveProgressionSQL = null;
    public HolographicDisplaysHook holographicDisplaysHook;
    private HologramsFile hologramsFile;
    public HologramsManager hologramsManager;
    private LoadHolograms loadHolograms;

    @Override
    public void onEnable() {

        PluginLogger.info(ChatColor.GOLD + "Plugin is starting...");

        /* Check for update */
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
        this.hologramsFile = new HologramsFile(this);
        this.playerInterfaceFile = new PlayerInterfaceFile(this);
        this.hologramsManager = new HologramsManager(hologramsFile);
        this.loadHolograms = new LoadHolograms(hologramsFile);
        this.loadQuests = new LoadQuests(questsFiles, configurationFiles);
        this.items = new Items(configurationFiles);
        this.globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);
        this.playerQuestsInterface = new PlayerQuestsInterface(playerInterfaceFile);
        this.categorizedQuestsInterfaces = new CategorizedQuestsInterfaces(configurationFiles);
        this.interfacesManager = new InterfacesManager(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);
        this.questsManager = new QuestsManager(configurationFiles, loadProgressionSQL, saveProgressionSQL);
        this.globalReward = new GlobalReward(configurationFiles);
        this.rewardManager = new RewardManager(configurationFiles);
        this.loadProgressionYAML = new LoadProgressionYAML(progressionFile);
        this.saveProgressionYAML = new SaveProgressionYAML(progressionFile);
        this.citizensHook = new CitizensHook(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);

        /* Load files */
        questsFiles.loadQuestsFiles();
        progressionFile.loadProgressionFile();
        hologramsFile.loadHologramsFile();
        playerInterfaceFile.loadPlayerInterfaceFile();

        /* Load dependencies */

        // VAULT - CMI
        if (!VaultHook.setupEconomy()) {
            PluginLogger.info(ChatColor.RED + "No compatible plugin detected for reward type 'MONEY'.");
            PluginLogger.info(ChatColor.RED + "Quests with reward type 'MONEY' will not work.");
        } else {
            PluginLogger.info(ChatColor.YELLOW + "Vault" + ChatColor.GREEN + " successfully hooked.");
        }

        // TOKENMANAGER - PLAYERPOINTS
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

        // CITIZENS
        if (CitizensHook.setupCitizens()) {
            getServer().getPluginManager().registerEvents(citizensHook, this);
            PluginLogger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GREEN + " successfully hooked.");
        } else PluginLogger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GOLD + " not detected. NPCs will not work.");

        // ELITEMOBS
        if (EliteMobsHook.isEliteMobsSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "EliteMobs" + ChatColor.GREEN + " successfully hooked.");
            getServer().getPluginManager().registerEvents(new EliteMobsHook(), this);
        }

        // MYTHICMOBS
        if (MythicMobsHook.isMythicMobsSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "MythicMobs" + ChatColor.GREEN + " successfully hooked.");
            getServer().getPluginManager().registerEvents(new MythicMobsHook(), this);
        }

        // HOLOGRAPHICDISPLAYS
        if (HolographicDisplaysHook.isHolographicDisplaysSetup()) {
            PluginLogger.info(ChatColor.YELLOW + "HolographicDisplays" + ChatColor.GREEN + " successfully hooked.");
            holographicDisplaysHook = new HolographicDisplaysHook();
            loadHolograms.loadHolograms();
        } else PluginLogger.info(ChatColor.YELLOW + "HolographicDisplays" + ChatColor.GOLD + " not detected. Holograms will not work.");

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

        interfacesManager.initEmptyCaseItems();

        /* Hook PAPI */
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            PluginLogger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GREEN + " successfully hooked.");
        } else
            PluginLogger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GOLD + " not detected. Placeholders will not work.");

        /* Load utils */
        ProgressionManager.isSynchronised = configurationFiles.getConfigFile().getBoolean("synchronised_progression");

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
        PluginLogger.info(ChatColor.GREEN + "Plugin is started !");
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
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
                    break;
            }
        }

        PluginLogger.info(ChatColor.RED + "Plugin is shutting down...");
    }

}

