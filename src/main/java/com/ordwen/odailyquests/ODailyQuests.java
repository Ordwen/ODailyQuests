package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.CitizensAPI;
import com.ordwen.odailyquests.apis.PlaceholderAPIHook;
import com.ordwen.odailyquests.apis.TokenManagerAPI;
import com.ordwen.odailyquests.apis.VaultAPI;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.ReloadCommand;
import com.ordwen.odailyquests.commands.completers.AdminCompleter;
import com.ordwen.odailyquests.commands.completers.PlayerCompleter;
import com.ordwen.odailyquests.commands.completers.ReloadCompleter;
import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.files.QuestsFiles;
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
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ODailyQuests extends JavaPlugin {

    /**
     * Getting instance of files classes.
     */
    private ConfigurationFiles configurationFiles;
    private QuestsFiles questsFiles;
    private ProgressionFile progressionFile;
    private LoadQuests loadQuests;
    private Items items;
    private InterfacesManager interfacesManager;
    private GlobalQuestsInterface globalQuestsInterface;
    private PlayerQuestsInterface playerQuestsInterface;
    private CategorizedQuestsInterfaces categorizedQuestsInterfaces;
    private QuestsManager questsManager;
    private LoadProgressionYAML loadProgressionYAML;
    private SaveProgressionYAML saveProgressionYAML;
    private ProgressionManager progressionManager;
    private CitizensAPI citizensAPI;
    private MySQLManager mySqlManager;
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
        // https://bstats.org/what-is-my-plugin-id
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
        this.loadProgressionYAML = new LoadProgressionYAML(progressionFile);
        this.saveProgressionYAML = new SaveProgressionYAML(progressionFile);
        this.progressionManager = new ProgressionManager();
        this.citizensAPI = new CitizensAPI(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);

        /* Load dependencies */
        if (!VaultAPI.setupEconomy()) {
            logger.severe("Plugin disabled due to no Vault dependency found !");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            logger.info(ChatColor.YELLOW + "Vault" + ChatColor.GREEN + " successfully hooked.");
        }
        if (!TokenManagerAPI.setupTokenManager()) {
            logger.info(ChatColor.YELLOW + "TokenManager" + ChatColor.RED + " not detected. Quests with type 'GET' will not work.");
        } else {
            logger.info(ChatColor.YELLOW + "TokenManager" + ChatColor.GREEN + " successfully hooked.");
        }
        if (CitizensAPI.setupCitizens()) {
            getServer().getPluginManager().registerEvents(citizensAPI, this);
            logger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GREEN + " successfully hooked.");
        } else logger.info(ChatColor.YELLOW + "Citizens" + ChatColor.GOLD + " not detected. NPCs will not work.");

        /* Load files */
        questsFiles.loadQuestsFiles();
        progressionFile.loadProgressionFile();

        /* Load quests */
        loadQuests.loadQuests();

        /* Load interfaces */
        items.initItems();
        interfacesManager.initInventoryNames();
        playerQuestsInterface.loadPlayerQuestsInterface();

        if (configurationFiles.getConfigFile().getInt("quests_mode") == 2) categorizedQuestsInterfaces.loadCategorizedInterfaces();
        else globalQuestsInterface.loadGlobalQuestsInterface();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            logger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GREEN + " successfully hooked.");
        } else logger.info(ChatColor.YELLOW + "PlaceholderAPI" + ChatColor.GOLD + " not detected. Placeholders will not work.");

        /* Load commands */
        getCommand("quests").setExecutor(new PlayerCommands(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces));
        getCommand("questsadmin").setExecutor(new AdminCommands());
        getCommand("questsreload").setExecutor(new ReloadCommand(configurationFiles, questsFiles, loadQuests, interfacesManager, playerQuestsInterface, globalQuestsInterface, categorizedQuestsInterfaces));

        /* Load Tab Completers */
        getCommand("quests").setTabCompleter(new PlayerCompleter());
        getCommand("questsadmin").setTabCompleter(new AdminCompleter());
        getCommand("questsreload").setTabCompleter(new ReloadCompleter());

        /* Load listeners */
        getServer().getPluginManager().registerEvents(interfacesManager, this);
        getServer().getPluginManager().registerEvents(questsManager, this);
        getServer().getPluginManager().registerEvents(progressionManager, this);

        logger.info(ChatColor.GREEN + "Plugin is started !");
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.RED + "Plugin is shutting down...");
    }

}

