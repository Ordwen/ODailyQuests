package com.ordwen.odailyquests;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.events.restart.RestartHandler;
import com.ordwen.odailyquests.externs.IntegrationsManager;
import com.ordwen.odailyquests.commands.admin.AdminCommands;
import com.ordwen.odailyquests.commands.player.PlayerCommands;
import com.ordwen.odailyquests.commands.admin.ReloadService;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.InventoryClickListener;
import com.ordwen.odailyquests.configuration.ConfigurationManager;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.events.EventsManager;
import com.ordwen.odailyquests.files.*;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllCategoryQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.AllQuestsCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.listeners.QuestCompletedListener;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.h2.H2Manager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.custom.items.PyroFishQuest;
import com.ordwen.odailyquests.quests.types.custom.mobs.EliteMobsQuest;
import com.ordwen.odailyquests.quests.types.custom.mobs.MythicMobsQuest;
import com.ordwen.odailyquests.quests.types.custom.vote.NuVotifierQuest;
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
import com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql.MySQLManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    private ReloadService reloadService;
    private CategoriesLoader categoriesLoader;
    public static HashMap<String, QuestSystem> questSystemMap = new HashMap<>();

    boolean isServerStopping = false;
    @Getter @Setter
    public boolean isSecondSystemEnabled;

    @Override
    public void onLoad() {
        INSTANCE = this;
        API = new ODailyQuestsAPI();
    }

    @Override
    public void onEnable() {
        PluginLogger.info("Plugin is starting...");
        ODailyQuestsAPI.disableRegistration();

        /* Load Metrics */
        // https://bstats.org/plugin/bukkit/ODailyQuests/14277
        int pluginId = 14277;
        final Metrics metrics = new Metrics(this, pluginId);

        /* Load files */
        this.configurationFiles = new ConfigurationFiles(this);
        this.filesManager = new FilesManager(this);
        this.filesManager.loadAllFiles();

        setSecondSystemEnabled(configurationFiles.getConfigFile().getBoolean("second_system.enabled"));
        loadQuestSystems();

        /* Check for updates */
        new AutoUpdater(this).checkForUpdate();
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
        questTypeRegistry.registerQuestType("PYRO_FISH", PyroFishQuest.class);
        questTypeRegistry.registerQuestType("NU_VOTIFIER", NuVotifierQuest.class);

        /* register addons types */
        final Map<String, Class<? extends AbstractQuest>> externalTypes = ODailyQuestsAPI.getExternalTypes();
        for (Map.Entry<String, Class<? extends AbstractQuest>> entry : externalTypes.entrySet()) {
            questTypeRegistry.registerQuestType(entry.getKey(), entry.getValue());
            PluginLogger.info("Registered external quest type: " + entry.getKey());
        }

        /* Load all elements */
        reloadService.reload();

        /* Load listeners */
        new EventsManager(this).registerListeners();

        /* Little reflection to register commands without plugin.yml, so they can be configured in the config.yml */
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                bukkitCommandMap.setAccessible(true);
                CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                commandMap.register(questSystem.getCommandName(), new PlayerCommands(questSystem.getCommandName(), questSystem));
                commandMap.register(questSystem.getAdminCommandName(), new AdminCommands(this, questSystem, questSystem.getAdminCommandName()));
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        /* Register plugin events */
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new QuestsManager(this, sqlManager != null), this);
        getServer().getPluginManager().registerEvents(new QuestCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllQuestsCompletedListener(), this);
        getServer().getPluginManager().registerEvents(new AllCategoryQuestsCompletedListener(), this);

        /* Register server restart related events */
        new RestartHandler(this).registerSubClasses();

        /* Avoid errors on reload */
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
                reloadService.loadConnectedPlayerQuests(questSystem);
            });

            PluginLogger.error("It seems that you have reloaded the server.");
            PluginLogger.error("Think that this can cause problems, especially in the data backup.");
            PluginLogger.error("You should restart the server instead.");
        }

        /* Init delayed task to draw new quests */
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            if (questSystem.getTimeStampMode() == 1 && questSystem.getTemporalityMode() == 1) {
                questSystem.setTimerTask(new TimerTask(LocalDateTime.now(), questSystem));
            }
        });

        PluginLogger.info("Plugin is started!");
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

    public void loadQuestSystems() {
        QuestSystem normalSystem = new QuestSystem();
        normalSystem.setSystemName("Normal Quest System");
        normalSystem.setConfigPath("");
        normalSystem.setPlayerTableSQL("create table PLAYER\n" +
                "  (\n" +
                "     PLAYERNAME char(32)  not null  ,\n" +
                "     PLAYERTIMESTAMP bigint not null,  \n" +
                "     ACHIEVEDQUESTS tinyint not null, \n" +
                "     TOTALACHIEVEDQUESTS int not null, \n" +
                "     constraint PK_PLAYER primary key (PLAYERNAME)\n" +
                "  );");
        normalSystem.setProgressionTableSQL("create table PROGRESSION\n" +
                "  (\n" +
                "     PRIMARYKEY int auto_increment  ,\n" +
                "     PLAYERNAME char(32)  not null  ,\n" +
                "     PLAYERQUESTID smallint  not null  ,\n" +
                "     QUESTID int  not null  ,\n" +
                "     ADVANCEMENT int  not null  ,\n" +
                "     ISACHIEVED bit  not null  ,\n" +
                "     primary key (PRIMARYKEY) ,\n" +
                "     constraint UNIQUE_PLAYERNAME_PLAYERQUESTID unique (PLAYERNAME, PLAYERQUESTID)" +
                "  ); ");
        normalSystem.setPlayerTableName("PLAYER");
        normalSystem.setProgressionTableName("PROGRESSION");
        normalSystem.setMYSQL_PLAYER_QUERY("INSERT INTO PLAYER (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "PLAYERTIMESTAMP = " + "VALUES(PLAYERTIMESTAMP), " +
                "ACHIEVEDQUESTS = VALUES(ACHIEVEDQUESTS), " +
                "TOTALACHIEVEDQUESTS = VALUES(TOTALACHIEVEDQUESTS)");
        normalSystem.setMYSQL_PROGRESS_UPDATE("INSERT INTO PROGRESSION (PLAYERNAME, PLAYERQUESTID, QUESTID, ADVANCEMENT, ISACHIEVED) " +
                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "QUESTID = VALUES(QUESTINDEX), " +
                "ADVANCEMENT = VALUES(ADVANCEMENT), " +
                "ISACHIEVED = VALUES(ISACHIEVED)");
        normalSystem.setH2_PLAYER_QUERY("MERGE INTO PLAYER (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                "KEY (PLAYERNAME) VALUES (?, ?, ?, ?)");
        normalSystem.setH2_PROGRESS_UPDATE("MERGE INTO PROGRESSION (PLAYERNAME, PLAYERQUESTID, QUESTID, ADVANCEMENT, ISACHIEVED) " +
                "KEY (PLAYERNAME, PLAYERQUESTID) VALUES (?, ?, ?, ?, ?)");
        normalSystem.setQUESTS_RENEWED_MESSAGE(QuestsMessages.QUESTS_RENEWED);
        normalSystem.setQuestsFilePath("quests/");
        normalSystem.setALL_QUESTS_ACHIEVED(QuestsMessages.ALL_QUESTS_ACHIEVED);
        normalSystem.setEASY_QUESTS_ACHIEVED(QuestsMessages.EASY_QUESTS_ACHIEVED);
        normalSystem.setMEDIUM_QUESTS_ACHIEVED(QuestsMessages.MEDIUM_QUESTS_ACHIEVED);
        normalSystem.setHARD_QUESTS_ACHIEVED(QuestsMessages.HARD_QUESTS_ACHIEVED);
        normalSystem.setQUESTS_IN_PROGRESS(QuestsMessages.QUESTS_IN_PROGRESS);
        normalSystem.setPapiPrefix("");

        questSystemMap.put("normal", normalSystem);
        if (isSecondSystemEnabled) {
            QuestSystem secondSystem = new QuestSystem();
            secondSystem.setSystemName("Second Quest System");
            secondSystem.setConfigPath("second_system.");
            secondSystem.setPlayerTableSQL("create table PLAYER_SECOND\n" +
                    "  (\n" +
                    "     PLAYERNAME char(32)  not null  ,\n" +
                    "     PLAYERTIMESTAMP bigint not null,  \n" +
                    "     ACHIEVEDQUESTS tinyint not null, \n" +
                    "     TOTALACHIEVEDQUESTS int not null, \n" +
                    "     constraint PK_PLAYER_SECOND primary key (PLAYERNAME)\n" +
                    "  );");
            secondSystem.setProgressionTableSQL("create table PROGRESSION_SECOND\n" +
                    "  (\n" +
                    "     PRIMARYKEY int auto_increment  ,\n" +
                    "     PLAYERNAME char(32)  not null  ,\n" +
                    "     PLAYERQUESTID smallint  not null  ,\n" +
                    "     QUESTID int  not null  ,\n" +
                    "     ADVANCEMENT int  not null  ,\n" +
                    "     ISACHIEVED bit  not null  ,\n" +
                    "     primary key (PRIMARYKEY) ,\n" +
                    "     constraint UNIQUE_PLAYERNAME_PLAYERQUESTID_SECOND unique (PLAYERNAME, PLAYERQUESTID)" +
                    "  ); ");
            secondSystem.setPlayerTableName("PLAYER_SECOND");
            secondSystem.setProgressionTableName("PROGRESSION_SECOND");
            secondSystem.setMYSQL_PLAYER_QUERY("INSERT INTO PLAYER_SECOND (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                    "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "PLAYERTIMESTAMP = " + "VALUES(PLAYERTIMESTAMP), " +
                    "ACHIEVEDQUESTS = VALUES(ACHIEVEDQUESTS), " +
                    "TOTALACHIEVEDQUESTS = VALUES(TOTALACHIEVEDQUESTS)");
            secondSystem.setMYSQL_PROGRESS_UPDATE("INSERT INTO PROGRESSION_SECOND (PLAYERNAME, PLAYERQUESTID, QUESTID, ADVANCEMENT, ISACHIEVED) " +
                    "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "QUESTID = VALUES(QUESTINDEX), " +
                    "ADVANCEMENT = VALUES(ADVANCEMENT), " +
                    "ISACHIEVED = VALUES(ISACHIEVED)");
            secondSystem.setH2_PLAYER_QUERY("MERGE INTO PLAYER_SECOND (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                    "KEY (PLAYERNAME) VALUES (?, ?, ?, ?)");
            secondSystem.setH2_PROGRESS_UPDATE("MERGE INTO PROGRESSION_SECOND (PLAYERNAME, PLAYERQUESTID, QUESTID, ADVANCEMENT, ISACHIEVED) " +
                    "KEY (PLAYERNAME, PLAYERQUESTID) VALUES (?, ?, ?, ?, ?)");
            secondSystem.setQUESTS_RENEWED_MESSAGE(QuestsMessages.QUESTS_RENEWED_SECOND);
            secondSystem.setQuestsFilePath("quests/second_system/");
            secondSystem.setALL_QUESTS_ACHIEVED(QuestsMessages.ALL_QUESTS_ACHIEVED_SECOND);
            secondSystem.setEASY_QUESTS_ACHIEVED(QuestsMessages.EASY_QUESTS_ACHIEVED_SECOND);
            secondSystem.setMEDIUM_QUESTS_ACHIEVED(QuestsMessages.MEDIUM_QUESTS_ACHIEVED_SECOND);
            secondSystem.setHARD_QUESTS_ACHIEVED(QuestsMessages.HARD_QUESTS_ACHIEVED_SECOND);
            secondSystem.setQUESTS_IN_PROGRESS(QuestsMessages.QUESTS_IN_PROGRESS_SECOND);
            secondSystem.setPapiPrefix(configurationFiles.getConfigFile().getString(secondSystem.getConfigPath() + "placeholder_prefix") + "_");
            questSystemMap.put("second", secondSystem);
        }
    }

    @Override
    public void onDisable() {

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            if (questSystem.getTimerTask() != null) questSystem.getTimerTask().stop();
            /* Avoid errors on reload */
            reloadService.saveConnectedPlayerQuests(false, questSystem);
        });

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
     * Get ConfigurationManager instance.
     *
     * @return ConfigurationManager instance.
     */
    public ConfigurationFiles getConfigurationFiles() {
        return configurationFiles;
    }

    /**
     * Get MySQLManager instance.
     *
     * @return MySQLManager instance.
     */
    public SQLManager getSQLManager() {
        return sqlManager;
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
     * Get ConfigurationManager instance.
     *
     * @return ConfigurationManager instance.
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Get YamlManager instance.
     *
     * @return YamlManager instance.
     */
    public YamlManager getYamlManager() {
        return yamlManager;
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
     * Get ODailyQuestsAPI instance.
     *
     * @return ODailyQuestsAPI instance.
     */
    public ODailyQuestsAPI getAPI() {
        return API;
    }
}

