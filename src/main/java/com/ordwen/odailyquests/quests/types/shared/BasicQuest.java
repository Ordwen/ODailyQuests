package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents a basic quest in the quest system.
 * <p>
 * The `BasicQuest` class extends {@link AbstractQuest} and provides a simple implementation of a quest with the necessary properties.
 * It overrides methods from the `AbstractQuest` class to provide specific behaviors for a basic quest type.
 */
public class BasicQuest extends AbstractQuest {

    /**
     * Constructs a new BasicQuest with the specified parameters.
     *
     * @param questIndex        the index of the quest.
     * @param questName         the name of the quest.
     * @param categoryName      the category of the quest.
     * @param questDesc         the description of the quest.
     * @param questType         the type of the quest.
     * @param menuItem          the item used in the quest's menu.
     * @param achievedItem      the item awarded for completing the quest.
     * @param requiredAmountRaw the required amount of items needed for the quest.
     * @param reward            the reward for completing the quest.
     * @param requiredWorlds    the worlds required for the quest.
     * @param requiredRegions   the regions required for the quest.
     * @param protectionBypass  whether protection bypass is enabled for the quest.
     * @param requiredPermission the permission required to undertake the quest.
     */
    public BasicQuest(int questIndex, String questName, String categoryName, List<String> questDesc, String questType, ItemStack menuItem, ItemStack achievedItem, String requiredAmountRaw, Reward reward, List<String> requiredWorlds, List<String> requiredRegions, boolean protectionBypass, String requiredPermission) {
        super(questIndex, questName, categoryName, questDesc, questType, menuItem, achievedItem, requiredAmountRaw, reward, requiredWorlds, requiredRegions, protectionBypass, requiredPermission);
    }

    /**
     * Returns the type of this quest.
     *
     * @return the quest type as "BASIC".
     */
    @Override
    public String getType() {
        return "BASIC";
    }

    /**
     * Determines if the quest can progress based on the given event and progression.
     * <p>
     * In the case of a basic quest, progression is not allowed, so this method returns false.
     * </p>
     *
     * @param event      the event that triggers progression.
     * @param progression the progression information of the player.
     * @return false since basic quests do not support progression.
     */
    @Override
    public boolean canProgress(Event event, Progression progression) {
        return false;
    }

    /**
     * Loads the parameters for the quest from the given configuration section.
     * <p>
     * For basic quests, no additional parameters need to be loaded, so this method returns true.
     * </p>
     *
     * @param section the configuration section to load parameters from.
     * @param file    the file path from which the parameters are loaded.
     * @param index   the index of the quest.
     * @return true, indicating that the parameters are successfully loaded (no action needed for basic quests).
     */
    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        return true;
    }
}