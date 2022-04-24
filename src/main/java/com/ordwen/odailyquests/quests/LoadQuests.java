package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.apis.EliteMobsAPI;
import com.ordwen.odailyquests.apis.MythicMobsHook;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.ColorConvert;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
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
     *
     * @param questsFiles        quests files class.
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
     * Clear all quests lists.
     */
    public void clearQuestsLists() {
        globalQuests.clear();
        easyQuests.clear();
        mediumQuests.clear();
        hardQuests.clear();
    }

    /**
     * Load all quests from files.
     */
    public void loadCategories() {

        /* init files */
        FileConfiguration globalQuestsFile = questsFiles.getGlobalQuestsFile();
        FileConfiguration easyQuestsFile = questsFiles.getEasyQuestsFile();
        FileConfiguration mediumQuestsFile = questsFiles.getMediumQuestsFile();
        FileConfiguration hardQuestsFile = questsFiles.getHardQuestsFile();

        if (configurationFiles.getConfigFile().getInt("quests_mode") == 1) {
            /* load global quests */
            loadQuests(globalQuestsFile, globalQuests, "Global Quests");
        } else if (configurationFiles.getConfigFile().getInt("quests_mode") == 2) {
            /* load easy quests */
            loadQuests(easyQuestsFile, easyQuests, "Easy Quests");
            /* load medium quests */
            loadQuests(mediumQuestsFile, mediumQuests, "Medium Quests");
            /* load hard quests */
            loadQuests(hardQuestsFile, hardQuests, "Hard Quests");
        } else {
            logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load the quests. The selected mode is incorrect.");
        }
    }

    /**
     * Load quests from a file.
     *
     * @param file     file to check.
     * @param quests   list for quests.
     * @param fileName file name for logger.
     */
    public void loadQuests(FileConfiguration file, ArrayList<Quest> quests, String fileName) {

        /* load quests */
        if (file.getConfigurationSection("quests") != null) {
            for (String fileQuest : file.getConfigurationSection("quests").getKeys(false)) {

                /* init variables (quest constructor) */
                Quest quest = null;
                int questIndex;
                String questName;
                List<String> questDesc;
                QuestType questType = null;
                ItemStack requiredItem = null;
                EntityType entityType = null;
                String entityName = null;
                Villager.Profession profession = null;
                int villagerLevel = 0;
                ItemStack menuItem;
                int requiredAmount;

                /* init variables (reward constructor) */
                Reward reward;
                RewardType rewardType;

                /* init quest items */
                questIndex = Integer.parseInt(fileQuest) - 1;
                questName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(file.getConfigurationSection("quests." + fileQuest).getString(".name")));

                menuItem = null;
                try {
                    menuItem = new ItemStack(Material.valueOf(file.getConfigurationSection("quests." + fileQuest).getString(".menu_item")));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "-----------------------------------");
                    logger.log(Level.SEVERE, "Invalid material type detected.");
                    logger.log(Level.SEVERE, "File : " + fileName);
                    logger.log(Level.SEVERE, "Quest number : " + (questIndex + 1));
                    logger.log(Level.SEVERE, "Parameter : menu_item");
                    logger.log(Level.SEVERE, "Value : " + file.getConfigurationSection("quests." + fileQuest).getString(".menu_item"));
                    logger.log(Level.SEVERE, "-----------------------------------");
                }

                questDesc = file.getConfigurationSection("quests." + fileQuest).getStringList(".description");
                for (String string : questDesc) {
                    questDesc.set(questDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(string)));
                }

                try {
                    questType = QuestType.valueOf(file.getConfigurationSection("quests." + fileQuest).getString(".quest_type"));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "-----------------------------------");
                    logger.log(Level.SEVERE, "Invalid quest type detected.");
                    logger.log(Level.SEVERE, "File : " + fileName);
                    logger.log(Level.SEVERE, "Quest number : " + (questIndex + 1));
                    logger.log(Level.SEVERE, "Parameter : quest_type");
                    logger.log(Level.SEVERE, "Value : " + file.getConfigurationSection("quests." + fileQuest).getString(".quest_type"));
                    logger.log(Level.SEVERE, "-----------------------------------");
                }

                if (questType != null) {
                    boolean isEntityType = questType == QuestType.KILL
                            || questType == QuestType.BREED
                            || questType == QuestType.TAME
                            || questType == QuestType.SHEAR
                            || questType == QuestType.CUSTOM_MOBS;

                    if (isEntityType) {
                        if (questType == QuestType.CUSTOM_MOBS) {
                            entityName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(file.getConfigurationSection("quests." + fileQuest).getString(".entity_name")));
                        } else {
                            try {
                                entityType = EntityType.valueOf(file.getConfigurationSection("quests." + fileQuest).getString(".entity_type"));
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "-----------------------------------");
                                logger.log(Level.SEVERE, "Invalid entity type detected.");
                                logger.log(Level.SEVERE, "File : " + fileName);
                                logger.log(Level.SEVERE, "Quest number : " + (questIndex + 1));
                                logger.log(Level.SEVERE, "Parameter : entity_type");
                                logger.log(Level.SEVERE, "Value : " + file.getConfigurationSection("quests." + fileQuest).getString(".entity_type"));
                                logger.log(Level.SEVERE, "-----------------------------------");
                            }
                        }
                    } else {
                        try {
                            requiredItem = new ItemStack(Material.valueOf(file.getConfigurationSection("quests." + fileQuest).getString(".required_item")));
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "-----------------------------------");
                            logger.log(Level.SEVERE, "Invalid material type detected.");
                            logger.log(Level.SEVERE, "File : " + fileName);
                            logger.log(Level.SEVERE, "Quest number : " + (questIndex + 1));
                            logger.log(Level.SEVERE, "Parameter : required_item");
                            logger.log(Level.SEVERE, "Value : " + file.getConfigurationSection("quests." + fileQuest).getString(".required_item"));
                            logger.log(Level.SEVERE, "-----------------------------------");
                        }

                        if (questType == QuestType.VILLAGER_TRADE) {
                            if (file.getConfigurationSection("quests." + fileQuest).contains(".villager_profession")) {
                                profession = Villager.Profession.valueOf(file.getConfigurationSection("quests." + fileQuest).getString(".villager_profession"));
                            }
                            if (file.getConfigurationSection("quests." + fileQuest).contains(".villager_level")) {
                                villagerLevel = file.getConfigurationSection("quests." + fileQuest).getInt(".villager_level");
                            }
                        }
                    }

                    requiredAmount = file.getConfigurationSection("quests." + fileQuest).getInt(".required_amount");

                    /* init reward */
                    rewardType = RewardType.valueOf(file.getConfigurationSection("quests." + fileQuest + ".reward").getString(".reward_type"));

                    if (rewardType == RewardType.COMMAND) {
                        reward = new Reward(rewardType, file.getConfigurationSection("quests." + fileQuest + ".reward").getStringList(".commands"));
                    } else {
                        reward = new Reward(rewardType, file.getConfigurationSection("quests." + fileQuest + ".reward").getInt(".amount"));
                    }

                    /* init quest */
                    if (isEntityType) {
                        if (questType == QuestType.CUSTOM_MOBS) {
                            if (EliteMobsAPI.isEliteMobsSetup() || MythicMobsHook.isMythicMobsSetup()) {
                                quest = new Quest(questIndex, questName, questDesc, questType, entityName, menuItem, requiredAmount, reward);
                            } else {
                                logger.log(Level.SEVERE, "File : " + fileName);
                                logger.log(Level.SEVERE, "Quest at index " + (questIndex + 1) + " cannot be loaded !");
                                logger.log(Level.SEVERE, "There is no compatible plugin found for quest type CUSTOM_MOBS.");
                            }
                        } else quest = new Quest(questIndex, questName, questDesc, questType, entityType, menuItem, requiredAmount, reward);
                    } else {
                        if (questType == QuestType.VILLAGER_TRADE) {
                            quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, menuItem, requiredAmount, profession, villagerLevel, reward);
                        }
                        else quest = new Quest(questIndex, questName, questDesc, questType, requiredItem, menuItem, requiredAmount, reward);
                    }

                    /* add quest to the list */
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
            }
            logger.info(ChatColor.GREEN + fileName + " array successfully loaded (" + ChatColor.YELLOW + quests.size() + ChatColor.GREEN + ").");
        } else
            logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load " + fileName + " : there is no quests in hardQuests.yml file !");
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
