package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.quests.QuestTypeRegistry;
import com.ordwen.odailyquests.quests.conditions.ConditionOperator;
import com.ordwen.odailyquests.quests.conditions.placeholder.PlaceholderCondition;
import com.ordwen.odailyquests.quests.getters.QuestItemGetter;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for loading quests from configuration files.
 * <p>
 * This loader handles:
 * <ul>
 *     <li>Reading quest definitions from YAML files</li>
 *     <li>Validating configuration values</li>
 *     <li>Creating {@link BasicQuest} objects</li>
 *     <li>Instantiating final quest implementations based on their type</li>
 *     <li>Loading quest rewards</li>
 *     <li>Reading placeholder-based conditions</li>
 * </ul>
 * <p>
 * Invalid configurations are reported through {@link PluginLogger}, and
 * the corresponding quests are skipped gracefully.
 */
public class QuestsLoader extends QuestItemGetter {

    private static final String ACHIEVED_MENU_ITEM = "achieved_menu_item";
    private static final String REQUIRED_PERMISSIONS = "required_permissions";
    private static final String REQUIRED_PERMISSION = "required_permission";
    private static final String CONDITIONS = "conditions";

    private final RewardLoader rewardLoader = new RewardLoader();
    private final QuestTypeRegistry questTypeRegistry = ODailyQuests.INSTANCE.getAPI().getQuestTypeRegistry();

    /**
     * Loads the reward configuration for a quest.
     * <p>
     * If the <code>.reward</code> section is missing or invalid, a default
     * {@link RewardType#NONE} reward is returned and the error is logged.
     *
     * @param questSection the section of the quest currently being loaded
     * @param fileName     the file name for error reporting
     * @param questIndex   the quest index inside the file
     * @return a fully constructed {@link Reward}, never {@code null}
     */
    private Reward createReward(ConfigurationSection questSection, String fileName, String questIndex) {
        if (!questSection.isConfigurationSection(".reward")) return new Reward(RewardType.NONE, 0, null);
        final ConfigurationSection rewardSection = questSection.getConfigurationSection(".reward");

        if (rewardSection == null) {
            PluginLogger.configurationError(fileName, questIndex, "reward", "There is no reward section defined for the quest.");
            return new Reward(RewardType.NONE, 0, null);
        }

        return rewardLoader.getRewardFromSection(rewardSection, fileName, questIndex);
    }

    /**
     * Creates a {@link BasicQuest} object containing all fundamental quest
     * attributes shared by all quest types.
     * <p>
     * This includes:
     * <ul>
     *     <li>Name and description</li>
     *     <li>Type validation</li>
     *     <li>World/region constraints</li>
     *     <li>Permissions</li>
     *     <li>Placeholder-based conditions</li>
     *     <li>Menu items and achieved-state items</li>
     *     <li>Quest reward</li>
     * </ul>
     * <p>
     * If any validation error occurs, the issue is logged and {@code null}
     * is returned, signaling that the quest should be skipped.
     *
     * @param questSection the configuration section defining the quest
     * @param fileName     the file name for error logging
     * @param questIndex   the index of the quest within the file
     * @param fileIndex    the quest identifier inside the YAML file
     * @return a {@link BasicQuest}, or {@code null} if configuration is invalid
     */
    private BasicQuest createBasicQuest(ConfigurationSection questSection, String fileName, int questIndex, String fileIndex) {
        /* quest name */
        final String questName = TextFormatter.format(questSection.getString(".name"));

        /* quest description */
        final List<String> questDesc = formatQuestDescription(questSection);

        /* quest type */
        final String questType = questSection.getString(".quest_type");
        if (!isValidQuestType(questType, fileName, fileIndex)) {
            return null;
        }

        /* required amount */
        final String requiredAmount = questSection.getString(".required_amount", "1");

        /* required worlds */
        final List<String> requiredWorlds = questSection.getStringList(".required_worlds");

        /* required regions */
        final List<String> requiredRegions = questSection.getStringList(".required_regions");

        /* protection bypass */
        final boolean protectionBypass = questSection.getBoolean(".protection_bypass");

        /* required permission */
        final List<String> requiredPermissions = resolveRequiredPermissions(questSection);

        /* conditions */
        final Optional<List<PlaceholderCondition>> conditionsOpt = parsePlaceholderConditions(questSection, fileName, fileIndex);
        if (conditionsOpt.isEmpty()) {
            return null;
        }
        final List<PlaceholderCondition> placeholderConditions = conditionsOpt.get();

        /* menu item */
        final ItemStack menuItem = createMenuItem(questSection, fileName, fileIndex);
        if (menuItem == null) {
            return null;
        }

        /* menu item amount */
        final int menuItemAmount = questSection.getInt(".menu_item_amount", 1);
        if (menuItemAmount < 0 || menuItemAmount > 64) {
            PluginLogger.configurationError(fileName, fileIndex, "menu_item_amount", "The menu item amount must be between 0 and 64.");
            return null;
        }

        /* achieved menu item */
        final ItemStack achievedItem = createAchievedMenuItem(questSection, fileName, fileIndex, menuItem);
        if (achievedItem == null) {
            return null;
        }

        /* reward */
        final Reward reward = createReward(questSection, fileName, fileIndex);

        return new BasicQuest(questIndex, fileIndex, questName, fileName, questDesc, questType, menuItem, menuItemAmount, achievedItem, requiredAmount, reward, requiredWorlds, requiredRegions, protectionBypass, requiredPermissions, placeholderConditions);
    }

    /**
     * Retrieves and formats a quest's description lines using
     * {@link TextFormatter#format(String)}.
     *
     * @param questSection the quest configuration section
     * @return a formatted list of description lines, never {@code null}
     */
    private List<String> formatQuestDescription(ConfigurationSection questSection) {
        final List<String> questDesc = questSection.getStringList(".description");
        questDesc.replaceAll(TextFormatter::format);
        return questDesc;
    }

    /**
     * Checks whether the provided quest type exists in the
     * {@link QuestTypeRegistry}.
     * <p>
     * If invalid, an error is logged.
     *
     * @param questType the quest type identifier
     * @param fileName  the file name for error logging
     * @param fileIndex the quest index in the file
     * @return {@code true} if the type is valid, {@code false} otherwise
     */
    private boolean isValidQuestType(String questType, String fileName, String fileIndex) {
        if (questTypeRegistry.containsKey(questType)) {
            return true;
        }
        PluginLogger.configurationError(fileName, fileIndex, "quest_type", questType + " is not a valid quest type.");
        return false;
    }

    /**
     * Resolves the permissions required to start the quest.
     * <p>
     * The configuration accepts these formats:
     * <ul>
     *     <li><code>required_permissions: [ ... ]</code></li>
     *     <li><code>required_permissions: "perm"</code></li>
     *     <li><code>required_permission: "perm"</code></li>
     * </ul>
     * <p>
     * If no permissions are defined, an empty list is returned.
     *
     * @param questSection the configuration section containing quest data
     * @return a list of required permissions, never {@code null}
     */
    private List<String> resolveRequiredPermissions(ConfigurationSection questSection) {
        if (questSection.isString(REQUIRED_PERMISSIONS)) {
            final String value = questSection.getString(REQUIRED_PERMISSIONS);
            if (value == null) {
                return Collections.emptyList();
            }
            return List.of(value);
        }

        if (questSection.isList(REQUIRED_PERMISSIONS)) {
            return questSection.getStringList(REQUIRED_PERMISSIONS);
        }

        if (questSection.isString(REQUIRED_PERMISSION)) {
            final String value = questSection.getString(REQUIRED_PERMISSION);
            if (value == null) {
                return Collections.emptyList();
            }
            return List.of(value);
        }

        return Collections.emptyList();
    }

    /**
     * Loads the quest's main menu item.
     * <p>
     * If no material is defined, the error is logged and {@code null} is returned.
     *
     * @param questSection the section defining the quest
     * @param fileName     the file name for logging purposes
     * @param fileIndex    the quest index in the file
     * @return an {@link ItemStack} for the menu icon, or {@code null} if invalid
     */
    private ItemStack createMenuItem(ConfigurationSection questSection, String fileName, String fileIndex) {
        final String presumedItem = questSection.getString(".menu_item");
        if (presumedItem == null) {
            PluginLogger.configurationError(fileName, fileIndex, "menu_item", "The menu item is not defined.");
            return null;
        }

        return getItemStackFromMaterial(presumedItem, fileName, fileIndex, "menu_item");
    }

    /**
     * Loads the menu item displayed when the quest is completed.
     * <p>
     * If not defined, the base menu item is reused.
     * If the configuration is invalid, an error is logged and the base item is returned.
     *
     * @param questSection the quest configuration
     * @param fileName     file name for error logging
     * @param fileIndex    quest index in the file
     * @param menuItem     the default menu item
     * @return the achieved-state {@link ItemStack}, never {@code null}
     */
    private ItemStack  createAchievedMenuItem(ConfigurationSection questSection, String fileName, String fileIndex, ItemStack menuItem) {
        if (!questSection.isString(ACHIEVED_MENU_ITEM)) {
            return menuItem.clone();
        }

        final String presumedAchievedItem = questSection.getString(ACHIEVED_MENU_ITEM);
        if (presumedAchievedItem == null) {
            PluginLogger.configurationError(fileName, fileIndex, ACHIEVED_MENU_ITEM, "The achieved menu item is defined but empty.");
            return menuItem.clone();
        }

        return getItemStackFromMaterial(presumedAchievedItem, fileName, fileIndex, ACHIEVED_MENU_ITEM);
    }

    /**
     * Loads all quests defined in a configuration file.
     * <p>
     * For each quest entry:
     * <ul>
     *     <li>Reads base quest attributes</li>
     *     <li>Validates configuration</li>
     *     <li>Instantiates the concrete quest type</li>
     *     <li>Adds the quest to the provided list</li>
     * </ul>
     * <p>
     * Invalid quests are skipped, and their errors logged.
     *
     * @param file     the YAML file containing quests
     * @param quests   the list to populate with loaded quests
     * @param fileName the file name used for logging
     */
    public void loadQuests(FileConfiguration file, List<AbstractQuest> quests, String fileName) {
        final ConfigurationSection allQuestsSection = file.getConfigurationSection("quests");
        if (allQuestsSection == null) {
            PluginLogger.error("Impossible to load " + fileName + ": there is no quests in " + fileName + " file!");
            return;
        }

        int questIndex = 0;

        for (String fileQuest : allQuestsSection.getKeys(false)) {
            final ConfigurationSection questSection = allQuestsSection.getConfigurationSection(fileQuest);
            if (questSection == null) {
                continue;
            }

            final BasicQuest base = createBasicQuest(questSection, fileName, questIndex, fileQuest);
            if (base == null) {
                continue;
            }

            final String questType = base.getQuestType();
            if (registerQuest(quests, fileName, questType, base, questSection, fileQuest)) {
                questIndex++;
            }
        }

        PluginLogger.info(quests.size() + " quests loaded from " + fileName + " file.");
    }

    /**
     * Instantiates and registers a quest of the given type.
     * <p>
     * The quest implementation is fetched from {@link QuestTypeRegistry}
     * and constructed using its {@link BasicQuest}-based constructor.
     * <p>
     * If instantiation fails or parameters cannot be loaded, the quest
     * is not added to the list.
     *
     * @param quests       the list to append the quest to
     * @param fileName     the file name for error reporting
     * @param questType    the quest type identifier
     * @param base         the preloaded basic quest information
     * @param questSection the configuration section for this quest
     * @param questIndex   the quest index inside the file
     * @return {@code true} if the quest was successfully registered
     */
    private boolean registerQuest(List<AbstractQuest> quests, String fileName, String questType, BasicQuest base, ConfigurationSection questSection, String questIndex) {
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
            return true;
        }

        return false;
    }

    /**
     * Parses all placeholder-based conditions for a quest.
     * <p>
     * Behaviour:
     * <ul>
     *     <li>If the conditions section is missing → returns {@code Optional.of(emptyList())}</li>
     *     <li>If the conditions are valid → returns {@code Optional.of(list)}</li>
     *     <li>If any condition is invalid → logs the error and returns {@code Optional.empty()}</li>
     * </ul>
     *
     * @param questSection the quest configuration section
     * @param fileName     file name for error logging
     * @param questIndex   index of the quest in the file
     * @return an {@link Optional} containing the conditions or empty if invalid
     */
    private Optional<List<PlaceholderCondition>> parsePlaceholderConditions(ConfigurationSection questSection, String fileName, String questIndex) {
        if (!questSection.isConfigurationSection(CONDITIONS)) {
            return Optional.of(Collections.emptyList());
        }

        final ConfigurationSection conditionsSection = questSection.getConfigurationSection(CONDITIONS);
        if (conditionsSection == null) {
            return Optional.of(Collections.emptyList());
        }

        final List<PlaceholderCondition> conditions = new ArrayList<>();

        for (String key : conditionsSection.getKeys(false)) {
            final String basePath = CONDITIONS + "." + key;

            final ConfigurationSection conditionSection = conditionsSection.getConfigurationSection(key);
            if (conditionSection == null) {
                PluginLogger.configurationError(fileName, questIndex, basePath, "The condition section is invalid.");
                return Optional.empty();
            }

            final String placeholder = conditionSection.getString("placeholder");
            if (placeholder == null || placeholder.isEmpty()) {
                PluginLogger.configurationError(fileName, questIndex, basePath + ".placeholder", "The placeholder value is missing.");
                return Optional.empty();
            }

            final String operatorRaw = conditionSection.getString("operator");
            if (operatorRaw == null || operatorRaw.isEmpty()) {
                PluginLogger.configurationError(fileName, questIndex, basePath + ".operator", "The condition operator is missing.");
                return Optional.empty();
            }

            final ConditionOperator conditionOperator;
            try {
                conditionOperator = ConditionOperator.valueOf(operatorRaw.toUpperCase());
            } catch (IllegalArgumentException ex) {
                PluginLogger.configurationError(fileName, questIndex, basePath + ".operator", operatorRaw + " is not a valid operator.");
                return Optional.empty();
            }

            final Object expectedObject = conditionSection.get("expected");
            if (expectedObject == null) {
                PluginLogger.configurationError(fileName, questIndex, basePath + ".expected", "The expected value is missing.");
                return Optional.empty();
            }

            final String expectedValue = String.valueOf(expectedObject);
            final String errorMessage = conditionSection.getString("error_message");

            conditions.add(new PlaceholderCondition(placeholder, conditionOperator, expectedValue, errorMessage));
        }

        return Optional.of(conditions);
    }
}
