package com.ordwen.odailyquests;

import com.ordwen.odailyquests.apis.TokenManagerAPI;
import com.ordwen.odailyquests.apis.VaultAPI;
import com.ordwen.odailyquests.commands.AdminCommands;
import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.metrics.Metrics;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.LoadProgression;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.quests.player.progression.SaveProgression;
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
    private InterfacesManager interfacesManager;
    private GlobalQuestsInterface globalQuestsInterface;
    private PlayerQuestsInterface playerQuestsInterface;
    private CategorizedQuestsInterfaces categorizedQuestsInterfaces;
    private QuestsManager questsManager;
    private LoadProgression loadProgression;
    private SaveProgression saveProgression;
    private ProgressionManager progressionManager;

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

        /* Load class instances */
        this.configurationFiles = new ConfigurationFiles(this);
        this.questsFiles = new QuestsFiles(this);
        this.progressionFile = new ProgressionFile(this);
        this.loadQuests = new LoadQuests(questsFiles, configurationFiles);
        this.interfacesManager = new InterfacesManager(configurationFiles);
        this.globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);
        this.playerQuestsInterface = new PlayerQuestsInterface(configurationFiles);
        this.categorizedQuestsInterfaces = new CategorizedQuestsInterfaces(configurationFiles);
        this.questsManager = new QuestsManager(configurationFiles);
        this.loadProgression = new LoadProgression(progressionFile);
        this.saveProgression = new SaveProgression(progressionFile);
        this.progressionManager = new ProgressionManager();

        /* Load files */
        configurationFiles.loadConfigurationFiles();
        configurationFiles.loadMessagesFiles();
        questsFiles.loadQuestsFiles();
        progressionFile.loadProgressionFile();

        /* Load quests */
        loadQuests.loadQuests();

        /* Load interfaces */
        InterfacesManager.initInventoryNames();
        playerQuestsInterface.loadPlayerQuestsInterface();

        if (configurationFiles.getConfigFile().getInt("mode") == 2) categorizedQuestsInterfaces.loadCategorizedInterfaces();
        else globalQuestsInterface.loadGlobalQuestsInterface();

        /* Load commands */
        getCommand("quests").setExecutor(new PlayerCommands(configurationFiles));
        getCommand("questsadmin").setExecutor(new AdminCommands());

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

