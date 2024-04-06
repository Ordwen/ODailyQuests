package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.quests.getters.QuestItemGetter;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.ColorConvert;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class QuestsLoader extends QuestItemGetter {

    private final RewardLoader rewardLoader = new RewardLoader();
    private final QuestTypeRegistry questTypeRegistry = ODailyQuests.INSTANCE.getAPI().getQuestTypeRegistry();

    /**
     * Load the reward of a quest.
     *
     * @param questSection the current quest section.
     * @param fileName     the file name where the quest is.
     * @param questIndex   the quest index in the file.
     * @return the reward of the quest.
     */
    private Reward createReward(ConfigurationSection questSection, String fileName, int questIndex) {
        if (!questSection.isConfigurationSection(".reward")) return new Reward(RewardType.NONE, 0);
        final ConfigurationSection rewardSection = questSection.getConfigurationSection(".reward");

        return rewardLoader.getRewardFromSection(rewardSection, fileName, questIndex);
    }

    /**
     * Create a quest with all basic information.
     *
     * @param questSection the current quest section.
     * @param fileName     the file name where the quest is.
     * @param questIndex   the quest index in the file.
     * @return the global quest.
     */
    private BasicQuest createBasicQuest(ConfigurationSection questSection, String fileName, int questIndex) {

        /* quest name */
        String questName = ColorConvert.convertColorCode(questSection.getString(".name"));

        /* quest description */
        List<String> questDesc = questSection.getStringList(".description");
        for (String string : questDesc) questDesc.set(questDesc.indexOf(string), ColorConvert.convertColorCode(string));

        /* check if quest uses placeholders */
        boolean usePlaceholders = questSection.getBoolean(".use_placeholders");

        /* quest type */
        final String questType = questSection.getString(".quest_type");
        if (!questTypeRegistry.containsKey(questType)) {
            PluginLogger.configurationError(fileName, questIndex, "quest_type", questType + " is not a valid quest type.");
            return null;
        }

        /* required amount */
        int requiredAmount = questSection.contains(".required_amount") ? 1 : questSection.getInt(".required_amount");
        if (requiredAmount < 1) requiredAmount = 1;

        /* required worlds */
        final List<String> requiredWorlds = questSection.getStringList(".required_worlds");

        String presumedItem = questSection.getString(".menu_item");
        if (presumedItem == null) {
            PluginLogger.configurationError(fileName, questIndex, "menu_item", "The menu item is not defined.");
            return null;
        }

        final ItemStack menuItem = getItemStackFromMaterial(presumedItem, fileName, questIndex, "menu_item");
        if (menuItem == null) return null;

        final ItemStack achievedItem;
        if (questSection.isString("achieved_menu_item")) {
            final String presumedAchievedItem = questSection.getString("achieved_menu_item");
            achievedItem = getItemStackFromMaterial(presumedAchievedItem, fileName, questIndex, "achieved_menu_item");
            if (achievedItem == null) return null;
        } else {
            achievedItem = menuItem;
        }

        /* reward */
        final Reward reward = createReward(questSection, fileName, questIndex);

        return new BasicQuest(questIndex, questName, fileName, questDesc, questType, menuItem,
                achievedItem, requiredAmount, reward, requiredWorlds, usePlaceholders);
    }

    /**
     * Load quests from a file.
     *
     * @param file     file to check.
     * @param quests   list for quests.
     * @param fileName file name for PluginLogger.
     */
    public void loadQuests(FileConfiguration file, List<AbstractQuest> quests, String fileName) {

        /* load quests */
        if (file.getConfigurationSection("quests") != null) {

            int questIndex = 0;
            for (String fileQuest : file.getConfigurationSection("quests").getKeys(false)) {

                final ConfigurationSection questSection = file.getConfigurationSection("quests." + fileQuest);
                if (questSection == null) continue;

                final BasicQuest base = createBasicQuest(questSection, fileName, questIndex);
                if (base == null) continue;

                final String questType = base.getQuestType();

                registerQuest(quests, fileName, questType, base, questSection, questIndex);

                questIndex++;
            }

            PluginLogger.info(fileName + " array successfully loaded (" + quests.size() + ").");
        } else {
            PluginLogger.error("Impossible to load " + fileName + " : there is no quests in " + fileName + " file !");
        }
    }

    /**
     * Register a quest.
     *
     * @param quests       list for quests.
     * @param fileName     file name for PluginLogger.
     * @param questType    type of the quest.
     * @param base         parent quest.
     * @param questSection current quest section.
     * @param questIndex   quest index in the file.
     */
    private void registerQuest(List<AbstractQuest> quests, String fileName, String questType, BasicQuest base, ConfigurationSection questSection, int questIndex) {
        final Class<? extends AbstractQuest> questClass = questTypeRegistry.get(questType);

        AbstractQuest questInstance = null;
        try {
            questInstance = questClass.getDeclaredConstructor(BasicQuest.class).newInstance(base);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            PluginLogger.error("Error while creating a new instance of " + questType + " quest.");
            PluginLogger.error(e.getMessage());
        }
        if (questInstance != null && questInstance.loadParameters(questSection, fileName, questIndex)) {
            quests.add(questInstance);
        }
    }
}
