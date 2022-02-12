package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadQuests {

    /**
     * Getting instance of classes.
     */
    private final QuestsFiles questsFiles;
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param questsFiles quests files class.
     * @param configurationFiles configuration files class.
     */
    public LoadQuests(QuestsFiles questsFiles, ConfigurationFiles configurationFiles) {
        this.questsFiles = questsFiles;
        this.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* Init quests lists */
    private static final ArrayList<Quest> globalQuests = new ArrayList<>();
    private static final ArrayList<Quest> easyQuests = new ArrayList<>();
    private static final ArrayList<Quest> mediumQuests = new ArrayList<>();
    private static final ArrayList<Quest> hardQuests = new ArrayList<>();

    /**
     * Load all quests from files.
     */
    public void loadQuests() {

        /* init variables (quest constructor) */
        Quest quest;
        int questIndex;
        String questName;
        List<String> questDesc;
        QuestType questType;
        ItemStack requiredItem;
        int requiredAmount;

        /* init variables (reward constructor) */
        Reward reward;
        RewardType rewardType;

        /* init files */
        FileConfiguration globalQuestsFile = questsFiles.getGlobalQuestsFile();
        FileConfiguration easyQuestsFile = questsFiles.getEasyQuestsFile();
        FileConfiguration mediumQuestsFile = questsFiles.getMediumQuestsFile();
        FileConfiguration hardQuestsFile = questsFiles.getHardQuestsFile();

        if (configurationFiles.getConfigFile().getInt("mode") == 1) {

            /* load global quests */
            for (String fileQuest : Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests")).getKeys(false)) {

                /* init quest items */
                questIndex = Integer.parseInt(fileQuest) - 1;
                questName = ChatColor.translateAlternateColorCodes('&', globalQuestsFile.getConfigurationSection("quests." + fileQuest).getString(".name"));
                questDesc = Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest)).getStringList(".description");
                for (String string : questDesc) {
                    questDesc.set(questDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', string));
                }

                questType = QuestType.valueOf(Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".quest_type"));
                requiredItem = new ItemStack(Material.valueOf(Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".required_item")));
                requiredAmount = Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest)).getInt(".required_amount");

                /* init reward */
                rewardType = RewardType.valueOf(Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getString(".reward_type"));

                if (rewardType == RewardType.COMMAND) {
                    reward = new Reward(rewardType, Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getStringList(".commands"));
                } else {
                    reward = new Reward(rewardType, Objects.requireNonNull(globalQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getInt(".amount"));
                }

                /* init quest */
                quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, requiredAmount, reward);

                /* add quest to the list */
                globalQuests.add(quest);
                logger.info(ChatColor.YELLOW + "Quest " + ChatColor.GRAY + fileQuest + ChatColor.YELLOW + " successfully loaded.");
            }
            logger.info(ChatColor.GREEN + "Global quests array successfully loaded.");
        }

        else if (configurationFiles.getConfigFile().getInt("mode") == 2) {

            /* load easy quests */
            if (easyQuestsFile.getConfigurationSection("quests") != null) {
                for (String fileQuest : easyQuestsFile.getConfigurationSection("quests").getKeys(false)) {

                    /* init quest items */
                    questIndex = Integer.parseInt(fileQuest) - 1;
                    questName = Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".name");
                    questDesc = Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest)).getStringList(".description");
                    questType = QuestType.valueOf(Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".quest_type"));
                    requiredItem = new ItemStack(Material.valueOf(Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".required_item")));
                    requiredAmount = Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest)).getInt(".required_amount");

                    /* init reward */
                    rewardType = RewardType.valueOf(Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getString(".reward_type"));

                    if (rewardType == RewardType.COMMAND) {
                        reward = new Reward(rewardType, Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getStringList(".commands"));
                    } else {
                        reward = new Reward(rewardType, Objects.requireNonNull(easyQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getInt(".amount"));
                    }

                    /* init quest */
                    quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, requiredAmount, reward);

                    /* add quest to the list */
                    easyQuests.add(quest);

                    logger.info(ChatColor.YELLOW + "Quest " + ChatColor.GRAY + fileQuest + ChatColor.YELLOW + " successfully loaded.");
                }
                logger.info(ChatColor.GREEN + "Easy quests array successfully loaded.");
            } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load easy quests : there is no quests in easyQuests.yml file !");

            /* load medium quests */
            if (easyQuestsFile.getConfigurationSection("quests") != null) {
                for (String fileQuest : mediumQuestsFile.getConfigurationSection("quests").getKeys(false)) {

                    /* init quest items */
                    questIndex = Integer.parseInt(fileQuest) - 1;
                    questName = Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".name");
                    questDesc = Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest)).getStringList(".description");
                    questType = QuestType.valueOf(Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".quest_type"));
                    requiredItem = new ItemStack(Material.valueOf(Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".required_item")));
                    requiredAmount = Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest)).getInt(".required_amount");

                    /* init reward */
                    rewardType = RewardType.valueOf(Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getString(".reward_type"));

                    if (rewardType == RewardType.COMMAND) {
                        reward = new Reward(rewardType, Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getStringList(".commands"));
                    } else {
                        reward = new Reward(rewardType, Objects.requireNonNull(mediumQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getInt(".amount"));
                    }

                    /* init quest */
                    quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, requiredAmount, reward);

                    /* add quest to the list */
                    mediumQuests.add(quest);
                    logger.info(ChatColor.YELLOW + "Quest " + ChatColor.GRAY + fileQuest + ChatColor.YELLOW + " successfully loaded.");
                }
                logger.info(ChatColor.GREEN + "Medium quests array successfully loaded.");
            } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load medium quests : there is no quests in mediumQuests.yml file !");

            /* load hard quests */
            if (easyQuestsFile.getConfigurationSection("quests") != null) {
                for (String fileQuest : hardQuestsFile.getConfigurationSection("quests").getKeys(false)) {

                    /* init quest items */
                    questIndex = Integer.parseInt(fileQuest) - 1;
                    questName = Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".name");
                    questDesc = Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest)).getStringList(".description");
                    questType = QuestType.valueOf(Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".quest_type"));
                    requiredItem = new ItemStack(Material.valueOf(Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest)).getString(".required_item")));
                    requiredAmount = Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest)).getInt(".required_amount");

                    /* init reward */
                    rewardType = RewardType.valueOf(Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getString(".reward_type"));

                    if (rewardType == RewardType.COMMAND) {
                        reward = new Reward(rewardType, Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getStringList(".commands"));
                    } else {
                        reward = new Reward(rewardType, Objects.requireNonNull(hardQuestsFile.getConfigurationSection("quests." + fileQuest + ".reward")).getInt(".amount"));
                    }

                    /* init quest */
                    quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, requiredAmount, reward);

                    /* add quest to the list */
                    hardQuests.add(quest);
                    logger.info(ChatColor.YELLOW + "Quest " + ChatColor.GRAY + fileQuest + ChatColor.YELLOW + " successfully loaded.");
                }
                logger.info(ChatColor.GREEN + "Hard quests array successfully loaded.");
            } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load hard quests : there is no quests in hardQuests.yml file !");
        }
        else  {
            logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load the quests. The selected mode is incorrect.");
        }
    }

    /**
     * Get global quests.
     */
    public static ArrayList<Quest> getGlobalQuests() {
        return globalQuests;
    }

    /**
     * Get easy quests.
     */
    public static ArrayList<Quest> getEasyQuests() {
        return easyQuests;
    }

    /**
     * Get medium quests.
     */
    public static ArrayList<Quest> getMediumQuests() {
        return mediumQuests;
    }

    /**
     * Get hard quests.
     */
    public static ArrayList<Quest> getHardQuests() {
        return hardQuests;
    }
}
