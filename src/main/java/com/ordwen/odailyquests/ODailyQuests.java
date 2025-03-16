package com.ordwen.odailyquests;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.events.restart.RestartHandler;
import com.ordwen.odailyquests.externs.IntegrationsManager;
import com.ordwen.odailyquests.commands.admin.AdminCommands;
import com.ordwen.odailyquests.commands.player.PlayerCommands;
import com.ordwen.odailyquests.commands.admin.ReloadService;
import com.ordwen.odailyquests.commands.admin.AdminCompleter;
import com.ordwen.odailyquests.commands.player.PlayerCompleter;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.InventoryClickListener;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.events.EventsManager;
import com.ordwen.odailyquests.files.*;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllCategoryQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.QuestCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.storage.DatabaseManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.custom.crate.CrateOpenQuest;
import com.ordwen.odailyquests.quests.types.custom.mobs.EliteMobsQuest;
import com.ordwen.odailyquests.quests.types.custom.mobs.MythicMobsQuest;
import com.ordwen.odailyquests.quests.types.custom.vote.NuVotifierQuest;
import com.ordwen.odailyquests.quests.types.custom.vote.VotifierPlusQuest;
import com.ordwen.odailyquests.quests.types.entity.BreedQuest;
import com.ordwen.odailyquests.quests.types.entity.KillQuest;
import com.ordwen.odailyquests.quests.types.entity.ShearQuest;
import com.ordwen.odailyquests.quests.types.entity.TameQuest;
import com.ordwen.odailyquests.quests.types.global.*;
import com.ordwen.odailyquests.quests.types.inventory.GetQuest;
import com.ordwen.odailyquests.quests.types.inventory.LocationQuest;
import com.ordwen.odailyquests.quests.types.inventory.PlaceholderQuest;
import com.ordwen.odailyquests.quests.types.item.VillagerQuest;
import com.ordwen.odailyquests.quests.types.item.*;
import com.ordwen.odailyquests.tools.*;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.autoupdater.config.ConfigUpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;

import java.time.LocalDateTime;
import java.util.Map;

public final class ODailyQuests extends JavaPlugin {

    public static ODailyQuests INSTANCE;
    public static MorePaperLib morePaperLib;
    private ODailyQuestsAPI API;

    /**
     * Getting instance of files classes.
     */
    private InterfacesManager interfacesManager;
    private FilesManager filesManager;
    private TimerTask timerTask;
    private ReloadService reloadService;
    private CategoriesLoader categoriesLoader;
    private DatabaseManager databaseManager;
    private RestartHandler restartHandler;

    boolean isServerStopping;

    @Override
    public void onLoad() {
        INSTANCE = this;
        API = new ODailyQuestsAPI();
    }

    @Override
    public void onEnable() {
        PluginLogger.info("Plugin is starting...");
        isServerStopping = false;

        ODailyQuestsAPI.disableRegistration();
        morePaperLib = new MorePaperLib(this);

        /* Load Metrics */
        // https://bstats.org/plugin/bukkit/ODailyQuests/14277
        int pluginId = 14277;
        new Metrics(this, pluginId);


        /* Load files */
        this.filesManager = new FilesManager(this);
        this.filesManager.load();

        /* Check for updates */
        new ConfigUpdateManager(this).runUpdates();
        checkForSpigotUpdate();

        /* Init categories loader */
        this.categoriesLoader = new CategoriesLoader();

        /* Load class instances */
        this.interfacesManager = new InterfacesManager(this);
        this.databaseManager = new DatabaseManager(this);

        /* Load dependencies */
        new IntegrationsManager(this).loadAllDependencies();

        /* Hook CustomBlockData */
        CustomBlockData.registerListener(this);

        /* Register all quest types, from main plugin or addons */
        registerQuestTypes();

        /* Load all config elements */
        this.reloadService = new ReloadService(this);
        reloadService.reload();

        /* Load listeners */
        new EventsManager(this).registerListeners();

        /* Load commands */
        getCommand("dquests").setExecutor(new PlayerCommands(interfacesManager));
        getCommand("dqadmin").setExecutor(new AdminCommands(this));

        /* Load Tab Completers */
        getCommand("dquests").setTabCompleter(new PlayerCompleter());
        getCommand("dqadmin").setTabCompleter(new AdminCompleter());

        /* Register plugin events */
        getServer().getPluginManager().registerEvents(new InventoryClickListener(interfacesManager.getQuestsInterfaces()), this);
        getServer().getPluginManager().registerEvents(new QuestsManager(this), this);
        getServer().getPluginManager().registerEvents(new QuestCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllQuestsCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllCategoryQuestsCompletedListener(), this);

        /* Register server restart related events */
        restartHandler = new RestartHandler(this);
        restartHandler.registerSubClasses();

        /* Avoid errors on reload */
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            reloadService.loadConnectedPlayerQuests();

            PluginLogger.warn("It seems that you have reloaded the server.");
            PluginLogger.warn("Think that this can cause problems, especially in the data backup.");
            PluginLogger.warn("You should restart the server instead.");
        }

        /* Init delayed task to draw new quests */
        if (Modes.getTimestampMode() == 1 && Temporality.getTemporalityMode() == 1) {
            if (timerTask != null) {
                timerTask.stop();
            }

            timerTask = new TimerTask(LocalDateTime.now());
        }

        PluginLogger.info("Plugin is started!");
    }

    private void registerQuestTypes() {
        /* Register quest types */
        final QuestTypeRegistry questTypeRegistry = API.getQuestTypeRegistry();

        /* entity quests */
        questTypeRegistry.registerQuestType("KILL", KillQuest.class);
        questTypeRegistry.registerQuestType("BREED", BreedQuest.class);
        questTypeRegistry.registerQuestType("SHEAR", ShearQuest.class);
        questTypeRegistry.registerQuestType("TAME", TameQuest.class);
        questTypeRegistry.registerQuestType("FIREBALL_REFLECT", FireballReflectQuest.class);
        questTypeRegistry.registerQuestType("ELITE_MOBS", EliteMobsQuest.class);
        questTypeRegistry.registerQuestType("MYTHIC_MOBS", MythicMobsQuest.class);

        /* item quests */
        questTypeRegistry.registerQuestType("BREAK", BreakQuest.class);
        questTypeRegistry.registerQuestType("PLACE", PlaceQuest.class);
        questTypeRegistry.registerQuestType("CRAFT", CraftQuest.class);
        questTypeRegistry.registerQuestType("PICKUP", PickupQuest.class);
        questTypeRegistry.registerQuestType("LAUNCH", LaunchQuest.class);
        questTypeRegistry.registerQuestType("CONSUME", ConsumeQuest.class);
        questTypeRegistry.registerQuestType("COOK", CookQuest.class);
        questTypeRegistry.registerQuestType("ENCHANT", EnchantQuest.class);
        questTypeRegistry.registerQuestType("FISH", FishQuest.class);
        questTypeRegistry.registerQuestType("FARMING", FarmingQuest.class);

        /* inventory quests */
        questTypeRegistry.registerQuestType("GET", GetQuest.class);
        questTypeRegistry.registerQuestType("LOCATION", LocationQuest.class);
        questTypeRegistry.registerQuestType("VILLAGER_TRADE", VillagerQuest.class);
        questTypeRegistry.registerQuestType("PLACEHOLDER", PlaceholderQuest.class);
        questTypeRegistry.registerQuestType("CARVE", CarveQuest.class);

        /* global quests */
        questTypeRegistry.registerQuestType("MILKING", MilkingQuest.class);
        questTypeRegistry.registerQuestType("EXP_POINTS", ExpPointsQuest.class);
        questTypeRegistry.registerQuestType("EXP_LEVELS", ExpLevelQuest.class);
        questTypeRegistry.registerQuestType("PLAYER_DEATH", PlayerDeathQuest.class);
        questTypeRegistry.registerQuestType("FIREBALL_REFLECT", FireballReflectQuest.class);

        /* other plugins */
        questTypeRegistry.registerQuestType("NU_VOTIFIER", NuVotifierQuest.class);
        questTypeRegistry.registerQuestType("VOTIFIER_PLUS", VotifierPlusQuest.class);
        questTypeRegistry.registerQuestType("CRATE_OPEN", CrateOpenQuest.class);


        /* register addons types */
        final Map<String, Class<? extends AbstractQuest>> externalTypes = ODailyQuestsAPI.getExternalTypes();
        for (Map.Entry<String, Class<? extends AbstractQuest>> entry : externalTypes.entrySet()) {
            questTypeRegistry.registerQuestType(entry.getKey(), entry.getValue());
            PluginLogger.info("Registered external quest type: " + entry.getKey());
        }
    }

    /**
     * Register a new quest type.
     *
     * @param name       name of the quest type
     * @param questClass class of the quest type
     */
    public void registerQuestType(String name, Class<? extends AbstractQuest> questClass) {
        API.getQuestTypeRegistry().registerQuestType(name, questClass);
    }

    @Override
    public void onDisable() {
        restartHandler.setServerStopping();
        if (timerTask != null) {
            timerTask.stop();
            timerTask = null;
        }

        /* Avoid errors on reload */
        reloadService.saveConnectedPlayerQuests();

        databaseManager.close();
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
     *
     * @return true if the server is stopping.
     */
    public boolean isServerStopping() {
        return this.isServerStopping;
    }

    /**
     * Set if the server is stopping.
     *
     * @param isServerStopping true if the server is stopping.
     */
    public void setServerStopping(boolean isServerStopping) {
        this.isServerStopping = isServerStopping;
    }

    /**
     * Get ReloadService instance.
     *
     * @return ReloadService instance.
     */
    public ReloadService getReloadService() {
        return reloadService;
    }

    /**
     * Get FilesManager instance.
     *
     * @return FilesManager instance.
     */
    public FilesManager getFilesManager() {
        return filesManager;
    }

    /**
     * Get InterfacesManager instance.
     *
     * @return InterfacesManager instance.
     */
    public InterfacesManager getInterfacesManager() {
        return interfacesManager;
    }

    /**
     * Get QuestsLoader instance.
     *
     * @return QuestsLoader instance.
     */
    public CategoriesLoader getCategoriesLoader() {
        return categoriesLoader;
    }

    /**
     * Get DatabaseManager instance.
     *
     * @return DatabaseManager instance.
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Get ODailyQuestsAPI instance.
     *
     * @return ODailyQuestsAPI instance.
     */
    public ODailyQuestsAPI getAPI() {
        return API;
    }
}

