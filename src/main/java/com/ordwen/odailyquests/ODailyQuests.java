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
import com.ordwen.odailyquests.metrics.Metrics;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.LoadProgression;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.SaveProgression;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
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
    private LoadProgression loadProgression;
    private SaveProgression saveProgression;
    private ProgressionManager progressionManager;
    private CitizensAPI citizensAPI;

    /* Technical items */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");
    String dataPath = this.getDataFolder().getPath();

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");

        /* Load Metrics */
        // https://bstats.org/what-is-my-plugin-id
        int pluginId = 14277;
        Metrics metrics = new Metrics(this, pluginId);

        /* Load class instances */
        this.configurationFiles = new ConfigurationFiles(this);
        this.questsFiles = new QuestsFiles(this);
        this.progressionFile = new ProgressionFile(this);
        this.loadQuests = new LoadQuests(questsFiles, configurationFiles);
        this.items = new Items(configurationFiles);
        this.globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);
        this.playerQuestsInterface = new PlayerQuestsInterface(configurationFiles);
        this.categorizedQuestsInterfaces = new CategorizedQuestsInterfaces(configurationFiles);
        this.interfacesManager = new InterfacesManager(configurationFiles, globalQuestsInterface, categorizedQuestsInterfaces);
        this.questsManager = new QuestsManager(configurationFiles);
        this.loadProgression = new LoadProgression(progressionFile);
        this.saveProgression = new SaveProgression(progressionFile);
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
        configurationFiles.loadConfigurationFiles();
        configurationFiles.loadMessagesFiles();
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

        if (PlaceholderAPIHook.setupPlaceholderAPI()) {
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

