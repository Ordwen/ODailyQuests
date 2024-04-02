package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

public class RewardLoader {

    /**
     * Load a reward from a configuration section.
     *
     * @param section configuration section.
     * @return reward.
     */
    public Reward getRewardFromSection(ConfigurationSection section, String fileName, int questIndex) {

        RewardType rewardType;
        try {
            rewardType = RewardType.valueOf(section.getString(".reward_type"));
        } catch (Exception e) {
            configurationError(fileName, "reward_type", section.getString(".reward_type") + " is not a valid reward type.", questIndex);
            rewardType = RewardType.NONE;
        }

        return switch (rewardType) {
            case NONE -> new Reward(RewardType.NONE, 0);
            case COMMAND -> new Reward(RewardType.COMMAND, section.getStringList(".commands"));

            case COINS_ENGINE -> {
                final String currencyLabel = section.getString(".currency_label");
                final String currencyDisplayName = section.getString(".currency_display_name");

                if (currencyLabel == null || currencyDisplayName == null) {
                    PluginLogger.error("Currency label or currency display name is missing in the configuration file.");
                    yield new Reward(RewardType.NONE, 0);
                }

                yield new Reward(RewardType.COINS_ENGINE, currencyLabel, currencyDisplayName, section.getInt(".amount"));
            }

            default -> new Reward(rewardType, section.getDouble(".amount"));
        };
    }

    /**
     * Display an error message in the console when a quest cannot be loaded because of a configuration error.
     *
     * @param fileName   the name of the file where the error occurred
     * @param parameter  the parameter that caused the error
     * @param reason     the reason of the error
     * @param questIndex the index of the quest in the file
     */
    public void configurationError(String fileName, String parameter, String reason, int questIndex) {
        PluginLogger.error("-----------------------------------");
        PluginLogger.error("Invalid quest configuration detected.");
        PluginLogger.error("File : " + fileName);
        PluginLogger.error("Reason : " + reason);

        if (parameter != null) {
            PluginLogger.error("Parameter : " + parameter);
        }

        if (questIndex != -1) {
            PluginLogger.error("Quest index : " + questIndex);
        }

        PluginLogger.error("-----------------------------------");
    }
}
