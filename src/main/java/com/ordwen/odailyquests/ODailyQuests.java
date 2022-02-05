package com.ordwen.odailyquests;

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

    /* Technical items */
    Logger logger = PluginLogger.getLogger("ODailyQuests");
    String dataPath = this.getDataFolder().getPath();

    ArrayList<Quest> globalQuests = new ArrayList<>();

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");

        this.configurationFiles = new ConfigurationFiles(this);
        this.questsFiles = new QuestsFiles(this);
        this.loadQuests = new LoadQuests(this, questsFiles, configurationFiles);

        /* Load files */
        configurationFiles.loadConfigurationFiles();
        questsFiles.loadQuestsFiles();

        /* Load quests */
        loadQuests.loadQuests();
        globalQuests = LoadQuests.getGlobalQuests();

        logger.info(ChatColor.GREEN + "Plugin is started !");

        logger.info("Name of first quest is : " + globalQuests.get(0).getQuestName());
        logger.info("Description of first quest is : " + globalQuests.get(0).getQuestDesc());
        logger.info("Type of first quest is : " + globalQuests.get(0).getType().getTypeName());
        logger.info("Item required of first quest is : " + globalQuests.get(0).getItemRequired());
        logger.info("Amount required of first quest is : " + globalQuests.get(0).getAmountRequired());
        logger.info("Reward type of first quest is : " + globalQuests.get(0).getReward().getRewardType());
        //logger.info("Reward of first quest is : " + globalQuests.get(0).getReward().getRewardCommand());
        //this.getServer().dispatchCommand(Bukkit.getConsoleSender(), quest.getReward().getRewardCommand());

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

