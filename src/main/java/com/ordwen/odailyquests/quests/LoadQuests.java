package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LoadQuests {

    /**
     * Getting instance of classes.
     */
    private final ODailyQuests oDailyQuests;
    private final QuestsFiles questsFiles;
    private final ConfigurationFiles configurationFiles;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     * @param questsFiles files class.
     * @param configurationFiles
     */
    public LoadQuests(ODailyQuests oDailyQuests, QuestsFiles questsFiles, ConfigurationFiles configurationFiles) {
        this.oDailyQuests = oDailyQuests;
        this.questsFiles = questsFiles;
        this.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("ODailyQuests");

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
        String questName;
        List<String> questDesc;
        QuestType questType;
        ItemStack requiredItem;
        int requiredAmount;

        /* init variables (reward constructor) */
        Reward reward;
        RewardType rewardType;

        if (configurationFiles.getConfigFile().getInt("mode") == 1) {

            for (String fileQuest : questsFiles.getGlobalQuestsFile().getConfigurationSection("quests").getKeys(false)) {

                /* init quest items */
                questName = questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest).getString(".name");
                questDesc = questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest).getStringList(".description");
                questType = QuestType.valueOf(questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest).getString(".quest_type"));
                requiredItem = new ItemStack(Material.valueOf(questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest).getString(".required_item")));
                requiredAmount = questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest).getInt(".required_amount");

                /* init reward */
                rewardType = RewardType.valueOf(questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest + ".reward").getString(".reward_type"));

                switch (rewardType) {
                    case COMMAND:
                        reward = new Reward(rewardType, questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest + ".reward").getStringList(".commands"));
                    default:
                        reward = new Reward(rewardType, questsFiles.getGlobalQuestsFile().getConfigurationSection("quests." + fileQuest + ".reward").getInt(".amount"));
                }

                /* init quest */
                quest = new Quest(questName, questDesc, questType, requiredItem, requiredAmount, reward);

                /* add quest to the list */
                globalQuests.add(quest);
                logger.info(ChatColor.YELLOW + "Quest " + ChatColor.GRAY + fileQuest + ChatColor.YELLOW + " successfully loaded.");
            }
            logger.info(ChatColor.GREEN + "Global quests successfully loaded.");
        }

        else if (configurationFiles.getConfigFile().getInt("mode") == 2) {
            logger.info(ChatColor.GREEN + "Easy quests successfully loaded.");
            logger.info(ChatColor.GREEN + "Medium quests successfully loaded.");
            logger.info(ChatColor.GREEN + "Hard quests successfully loaded.");
        }

        else  {
            logger.info(ChatColor.RED + "Impossible to load the quests. The selected mode is incorrect.");
        }
    }

    /**
     * Get global quests.
     */
    public static ArrayList<Quest> getGlobalQuests() {
        return globalQuests;
    }
}
