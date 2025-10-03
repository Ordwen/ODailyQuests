package com.ordwen.odailyquests.quests.types;


import com.ordwen.odailyquests.api.quests.IQuest;
import com.ordwen.odailyquests.quests.conditions.placeholder.PlaceholderCondition;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract quest that a player can undertake.
 * <p>
 * This class defines the core properties and methods of a quest, including the quest's name, description,
 * type, reward, required items, and other related attributes. It serves as a base class for specific types of quests.
 */
public abstract class AbstractQuest extends PlayerProgressor implements IQuest {

    final int questIndex;
    final String fileIndex;
    final String questName;
    final String categoryName;
    final List<String> questDesc;
    final String questType;
    final ItemStack menuItem;
    final int menuItemAmount;
    final ItemStack achievedItem;
    final String requiredAmountRaw;
    final Reward reward;
    final List<String> requiredWorlds;
    final List<String> requiredRegions;
    final boolean protectionBypass;
    final List<String> requiredPermissions;

    protected boolean isRandomRequired;
    protected final List<String> displayNames;

    private List<PlaceholderCondition> placeholderConditions;

    /**
     * Constructs a new AbstractQuest with the specified parameters.
     *
     * @param questIndex         the index of the quest.
     * @param fileIndex          the file index of the quest.
     * @param questName          the name of the quest.
     * @param categoryName       the category of the quest.
     * @param questDesc          the description of the quest.
     * @param questType          the type of the quest.
     * @param menuItem           the item used in the quest's menu.
     * @param menuItemAmount     the amount of the menu item (default is 1).
     * @param achievedItem       the item awarded for completing the quest.
     * @param requiredAmountRaw  the required amount of items needed for the quest.
     * @param reward             the reward for completing the quest.
     * @param requiredWorlds     the worlds required for the quest.
     * @param requiredRegions    the regions required for the quest.
     * @param protectionBypass   whether protection bypass is enabled for the quest.
     * @param requiredPermissions the permissions required to undertake the quest.
     */
    protected AbstractQuest(int questIndex, String fileIndex, String questName, String categoryName, List<String> questDesc, String questType, ItemStack menuItem, int menuItemAmount, ItemStack achievedItem, String requiredAmountRaw, Reward reward, List<String> requiredWorlds, final List<String> requiredRegions, boolean protectionBypass, List<String> requiredPermissions, List<PlaceholderCondition> placeholderConditions) {
        this.questIndex = questIndex;
        this.fileIndex = fileIndex;
        this.questName = questName;
        this.categoryName = categoryName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.menuItem = menuItem;
        this.menuItemAmount = menuItemAmount;
        this.achievedItem = achievedItem;
        this.requiredAmountRaw = requiredAmountRaw;
        this.reward = reward;
        this.requiredWorlds = requiredWorlds;
        this.requiredRegions = requiredRegions;
        this.protectionBypass = protectionBypass;
        this.requiredPermissions = requiredPermissions;
        this.placeholderConditions = placeholderConditions;

        this.displayNames = new ArrayList<>();
    }

    /**
     * Constructs a new AbstractQuest from a BasicQuest.
     *
     * @param basicQuest the base quest to initialize this quest.
     */
    protected AbstractQuest(BasicQuest basicQuest) {
        this.questIndex = basicQuest.getQuestIndex();
        this.fileIndex = basicQuest.getFileIndex();
        this.questName = basicQuest.getQuestName();
        this.categoryName = basicQuest.getCategoryName();
        this.questDesc = basicQuest.getQuestDesc();
        this.questType = basicQuest.getQuestType();
        this.menuItem = basicQuest.getMenuItem();
        this.menuItemAmount = basicQuest.getMenuItemAmount();
        this.achievedItem = basicQuest.getAchievedItem();
        this.requiredAmountRaw = basicQuest.getRequiredAmountRaw();
        this.reward = basicQuest.getReward();
        this.requiredWorlds = basicQuest.getRequiredWorlds();
        this.requiredRegions = basicQuest.getRequiredRegions();
        this.protectionBypass = basicQuest.isProtectionBypass();
        this.requiredPermissions = basicQuest.getRequiredPermissions();
        this.placeholderConditions = basicQuest.getPlaceholderConditions();

        this.displayNames = new ArrayList<>();
    }

    /**
     * Gets the selected display name for a random required item.
     * <p>
     * This method returns a valid display name from the list of display names based on the provided index.
     * If the index is out of bounds or random required is disabled, it returns an error message.
     * </p>
     *
     * @param index the index of the display name.
     * @return the selected display name or an error message.
     */
    public String getSelectedDisplayName(int index) {
        if (!isRandomRequired || displayNames.isEmpty()) return ChatColor.RED + "Invalid usage.";
        if (index < 0 || index >= displayNames.size()) return ChatColor.RED + "Invalid index.";

        return displayNames.get(index);
    }

    /**
     * Checks if the display name is missing for a random required item.
     * <p>
     * This method validates if the display name for a random required item is missing in the configuration.
     * If missing, it logs an error.
     * </p>
     *
     * @param section the configuration section to check.
     * @param file    the file path where the error occurred.
     * @param index   the index of the item.
     * @param path    the path to check for missing values.
     * @param type    the type of display name.
     * @return true if the display name is missing, false otherwise.
     */
    protected boolean isDisplayNameMissing(ConfigurationSection section, String file, String index, String path, String type) {
        if (path.equals(".random_required")) {
            final String displayName = section.getString(path + "." + type);
            if (displayName == null || displayName.isEmpty()) {
                PluginLogger.configurationError(file, index, path + "." + type, "Missing display name for random required item.");
                return true;
            }
            displayNames.add(displayName);
        }
        return false;
    }

    /**
     * Get index of quest.
     *
     * @return quest index.
     */
    public int getQuestIndex() {
        return this.questIndex;
    }

    /**
     * Get file index of quest.
     *
     * @return file index.
     */
    public String getFileIndex() {
        return this.fileIndex;
    }

    /**
     * Get the type of quest.
     *
     * @return the type of the quest.
     */
    public String getQuestType() {
        return this.questType;
    }

    /**
     * Get the name of the quest.
     *
     * @return quest name.
     */
    public String getQuestName() {
        return this.questName;
    }

    /**
     * Get the name of the category.
     *
     * @return category name.
     */
    public String getCategoryName() {
        return this.categoryName;
    }

    /**
     * Get menu item.
     *
     * @return menu item.
     */
    public ItemStack getMenuItem() {
        return this.menuItem;
    }

    /**
     * Get menu item amount.
     *
     * @return menu item amount.
     */
    public int getMenuItemAmount() {
        return this.menuItemAmount;
    }

    /**
     * Get achieved item.
     *
     * @return achieved item.
     */
    public ItemStack getAchievedItem() {
        return this.achievedItem;
    }

    /**
     * Get the description of the quest.
     *
     * @return quest description.
     */
    public List<String> getQuestDesc() {
        return this.questDesc;
    }

    /**
     * Get the amount required by the quest.
     *
     * @return quest amount-required.
     */
    public String getRequiredAmountRaw() {
        return this.requiredAmountRaw;
    }

    /**
     * Get the reward of the quest.
     *
     * @return quest reward.
     */
    public Reward getReward() {
        return this.reward;
    }

    /**
     * Get the required worlds of the quest.
     *
     * @return quest required worlds.
     */
    public List<String> getRequiredWorlds() {
        return this.requiredWorlds;
    }

    /**
     * Get the required regions of the quest.
     *
     * @return quest required regions.
     */
    public List<String> getRequiredRegions() {
        return this.requiredRegions;
    }

    /**
     * Get whether the quest has protection bypass.
     *
     * @return quest protection bypass.
     */
    public boolean isProtectionBypass() {
        return this.protectionBypass;
    }

    /**
     * Get the required permission of the quest.
     *
     * @return quest required permission.
     */
    public List<String> getRequiredPermissions() {
        return this.requiredPermissions;
    }

    /**
     * Check if the quest is random required.
     *
     * @return true if random required, false otherwise.
     */
    public boolean isRandomRequired() {
        return isRandomRequired;
    }

    /**
     * Check if the quest requires a random amount of items.
     *
     * @return true if the required amount is a range (e.g., "1-5"), false if it is a fixed number (e.g., "3").
     */
    public boolean isRandomRequiredAmount() {
        return requiredAmountRaw.contains("-");
    }

    /**
     * Get the list of placeholder conditions associated with this quest.
     *
     * @return the list of placeholder conditions.
     */
    public List<PlaceholderCondition> getPlaceholderConditions() {
        return placeholderConditions;
    }

    /**
     * Check if the quest has any placeholder conditions.
     *
     * @return true if there are placeholder conditions, false otherwise.
     */
    public boolean hasPlaceholderConditions() {
        return !placeholderConditions.isEmpty();
    }
}
