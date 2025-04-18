package com.ordwen.odailyquests.quests.types;


import com.ordwen.odailyquests.api.quests.IQuest;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQuest extends PlayerProgressor implements IQuest {

    final int questIndex;
    final String questName;
    final String categoryName;
    final List<String> questDesc;
    final String questType;
    final ItemStack menuItem;
    final ItemStack achievedItem;
    final String requiredAmountRaw;
    final Reward reward;
    final List<String> requiredWorlds;
    final List<String> requiredRegions;
    final boolean protectionBypass;
    final String requiredPermission;

    protected boolean isRandomRequired;
    protected final List<String> displayNames;

    /**
     * Quest constructor.
     *
     * @param questName         name of the quest.
     * @param questDesc         description of the quest.
     * @param questType         type of the quest.
     * @param requiredAmountRaw required amount of the item.
     * @param reward            reward of the quest.
     */
    protected AbstractQuest(int questIndex, String questName, String categoryName, List<String> questDesc, String questType, ItemStack menuItem, ItemStack achievedItem, String requiredAmountRaw, Reward reward, List<String> requiredWorlds, final List<String> requiredRegions, boolean protectionBypass, String requiredPermission) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.categoryName = categoryName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.menuItem = menuItem;
        this.achievedItem = achievedItem;
        this.requiredAmountRaw = requiredAmountRaw;
        this.reward = reward;
        this.requiredWorlds = requiredWorlds;
        this.requiredRegions = requiredRegions;
        this.protectionBypass = protectionBypass;
        this.requiredPermission = requiredPermission;

        this.displayNames = new ArrayList<>();
    }

    /**
     * Quest constructor.
     *
     * @param basicQuest quest base.
     */
    protected AbstractQuest(BasicQuest basicQuest) {
        this.questIndex = basicQuest.getQuestIndex();
        this.questName = basicQuest.getQuestName();
        this.categoryName = basicQuest.getCategoryName();
        this.questDesc = basicQuest.getQuestDesc();
        this.questType = basicQuest.getQuestType();
        this.menuItem = basicQuest.getMenuItem();
        this.achievedItem = basicQuest.getAchievedItem();
        this.requiredAmountRaw = basicQuest.getRequiredAmountRaw();
        this.reward = basicQuest.getReward();
        this.requiredWorlds = basicQuest.getRequiredWorlds();
        this.requiredRegions = basicQuest.getRequiredRegions();
        this.protectionBypass = basicQuest.isProtectionBypass();
        this.requiredPermission = basicQuest.getRequiredPermission();

        this.displayNames = new ArrayList<>();
    }

    public String getSelectedDisplayName(int index) {
        if (!isRandomRequired || displayNames.isEmpty()) return ChatColor.RED + "Invalid usage.";
        if (index < 0 || index >= displayNames.size()) return ChatColor.RED + "Invalid index.";

        return displayNames.get(index);
    }

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
    public String getRequiredPermission() {
        return this.requiredPermission;
    }

    /**
     * Check if the quest is random required.
     *
     * @return true if random required, false otherwise.
     */
    public boolean isRandomRequired() {
        return isRandomRequired;
    }
}
