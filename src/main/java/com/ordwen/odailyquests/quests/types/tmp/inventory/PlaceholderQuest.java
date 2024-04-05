package com.ordwen.odailyquests.quests.types.tmp.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.ConditionType;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlaceholderQuest extends AbstractQuest {

    private String placeholder;
    private ConditionType conditionType;
    private String expectedValue;
    private String errorMessage;

    public PlaceholderQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "PLACEHOLDER";
    }

    @Override
    public boolean canProgress(Event provided) {
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        final ConfigurationSection placeholderSection = section.getConfigurationSection(".placeholder");
        if (placeholderSection == null) {
            PluginLogger.configurationError(file, index, "placeholder", "The placeholder section is missing.");
            return false;
        }

        placeholder = placeholderSection.getString(".value");
        if (placeholder == null) {
            PluginLogger.configurationError(file, index, "value", "The value of the placeholder is missing.");
            return false;
        }

        final String operator = placeholderSection.getString(".operator");
        if (operator == null) {
            PluginLogger.configurationError(file, index, "operator", "The operator of the placeholder is missing.");
            return false;
        }
        conditionType = ConditionType.valueOf(operator);

        expectedValue = placeholderSection.getString(".expected");
        if (expectedValue == null) {
            PluginLogger.configurationError(file, index, "expected", "The expected value of the placeholder is missing.");
            return false;
        }

        errorMessage = placeholderSection.getString(".error_message");
        if (errorMessage == null) {
            PluginLogger.configurationError(file, index, "error_message", "The error message of the placeholder is missing.");
            return false;
        }

        /* apply Persistent Data Container to the menu item to differentiate GET quests */
        final ItemStack menuItem = super.getMenuItem();
        final ItemMeta meta = menuItem.getItemMeta();
        if (meta == null) return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_type"), PersistentDataType.STRING, "placeholder");
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_index"), PersistentDataType.INTEGER, index);
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "file_name"), PersistentDataType.STRING, file);

        return true;
    }

    /**
     * Get the placeholder required by the quest.
     * @return quest required placeholder.
     */
    public String getPlaceholder() {
        return this.placeholder;
    }

    /**
     * Get the condition type required by the quest.
     * @return ConditionType object.
     */
    public ConditionType getConditionType() {
        return this.conditionType;
    }

    /**
     * Get the expected value required by the quest.
     * @return quest expected value.
     */
    public String getExpectedValue() {
        return this.expectedValue;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
