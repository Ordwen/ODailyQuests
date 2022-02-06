package com.ordwen.odailyquests;

import com.ordwen.odailyquests.commands.PlayerCommands;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class ODailyQuests extends JavaPlugin {

    /**
     * Getting instance of files classes.
     */
    private ConfigurationFiles configurationFiles;
    private QuestsFiles questsFiles;
    private LoadQuests loadQuests;
    private GlobalQuestsInterface globalQuestsInterface;

    /* Technical items */
    Logger logger = PluginLogger.getLogger("ODailyQuests");
    String dataPath = this.getDataFolder().getPath();

    ArrayList<Quest> globalQuestsArray = new ArrayList<>();

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");

        this.configurationFiles = new ConfigurationFiles(this);
        this.questsFiles = new QuestsFiles(this);
        this.loadQuests = new LoadQuests(questsFiles, configurationFiles);
        this.globalQuestsInterface = new GlobalQuestsInterface(configurationFiles);

        /* Load files */
        configurationFiles.loadConfigurationFiles();
        questsFiles.loadQuestsFiles();

        /* Load quests */
        loadQuests.loadQuests();
        globalQuestsArray = LoadQuests.getGlobalQuests();

        /* Load interfaces */
        globalQuestsInterface.loadGlobalQuestsInventory();

        /* Load commands */
        getCommand("quests").setExecutor(new PlayerCommands());

        /* Load listeners */
        getServer().getPluginManager().registerEvents(new GlobalQuestsInterface(configurationFiles), this);

        logger.info(ChatColor.GREEN + "Plugin is started !");

        /*
        logger.info("FIRST QUEST");
        logger.info("Name of first quest is : " + globalQuestsArray.get(0).getQuestName());
        logger.info("Description of first quest is : " + globalQuestsArray.get(0).getQuestDesc());
        logger.info("Type of first quest is : " + globalQuestsArray.get(0).getType().getTypeName());
        logger.info("Item required of first quest is : " + globalQuestsArray.get(0).getItemRequired());
        logger.info("Amount required of first quest is : " + globalQuestsArray.get(0).getAmountRequired());
        logger.info("Reward type of first quest is : " + globalQuestsArray.get(0).getReward().getRewardType());
        logger.info("Reward of first quest is : " + globalQuestsArray.get(0).getReward().getRewardAmount());

        logger.info("SECOND QUEST");
        logger.info("Name of second quest is : " + globalQuestsArray.get(1).getQuestName());
        logger.info("Description of second quest is : " + globalQuestsArray.get(1).getQuestDesc());
        logger.info("Type of second quest is : " + globalQuestsArray.get(1).getType().getTypeName());
        logger.info("Item required of second quest is : " + globalQuestsArray.get(1).getItemRequired());
        logger.info("Amount required of second quest is : " + globalQuestsArray.get(1).getAmountRequired());
        logger.info("Reward type of second quest is : " + globalQuestsArray.get(1).getReward().getRewardType());
        logger.info("Reward of second quest is : " + globalQuestsArray.get(1).getReward().getRewardCommands());
        for (String cmd : globalQuestsArray.get(1).getReward().getRewardCommands()) {
            this.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", "Console"));
        }
         */
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.RED + "Plugin is shutting down...");
    }

    public String getPluginFolder() {
        return dataPath;
    }
    public Logger getPluginLogger() { return logger; }
}

