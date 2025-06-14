package com.ordwen.odailyquests.rewards;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.configuration.ConfigurationSection;

public class RewardLoader {

    /**
     * Load a reward from a configuration section.
     *
     * @param section configuration section.
     * @return reward.
     */
    public Reward getRewardFromSection(ConfigurationSection section, String fileName, String questIndex) {

        RewardType rewardType;
        try {
            rewardType = RewardType.valueOf(section.getString(".reward_type"));
        } catch (Exception e) {
            configurationError(fileName, "reward_type", section.getString(".reward_type") + " is not a valid reward type.", questIndex);
            rewardType = RewardType.NONE;
        }

        final String message = TextFormatter.format(section.getString(".message"));

        return switch (rewardType) {
            case NONE -> new Reward(RewardType.NONE, 0, message);
            case COMMAND -> new Reward(RewardType.COMMAND, section.getStringList(".commands"), message);

            case COINS_ENGINE -> {
                final String currencyLabel = section.getString(".currency_label");
                final String currencyDisplayName = section.getString(".currency_display_name");

                if (currencyLabel == null || currencyDisplayName == null) {
                    PluginLogger.error("Currency label or currency display name is missing in the configuration file.");
                    yield new Reward(RewardType.NONE, 0, message);
                }

                final String amount = section.getString(".amount");
                if (amount == null || amount.isEmpty()) {
                    PluginLogger.error("Amount is missing in the configuration file for COINS_ENGINE reward.");
                    yield new Reward(RewardType.NONE, 0, message);
                }

                int parsedAmount;
                try {
                    parsedAmount = Integer.parseInt(TextFormatter.format(amount));
                } catch (NumberFormatException e) {
                    PluginLogger.error("Amount is not a valid integer in the configuration file for COINS_ENGINE reward.");
                    yield new Reward(RewardType.NONE, 0, message);
                }

                yield new Reward(RewardType.COINS_ENGINE, currencyLabel, currencyDisplayName, parsedAmount, message);
            }

            default -> {
                final String amount = section.getString(".amount");
                if (amount == null || amount.isEmpty()) {
                    PluginLogger.error("Amount is missing in the configuration file for COINS_ENGINE reward.");
                    yield new Reward(RewardType.NONE, 0, message);
                }

                double parsedAmount;
                try {
                    parsedAmount = Double.parseDouble(TextFormatter.format(amount));
                } catch (NumberFormatException e) {
                    PluginLogger.error("Amount is not a valid number in the configuration file for " + rewardType + " reward.");
                    yield new Reward(RewardType.NONE, 0, message);
                }

                yield new Reward(rewardType, parsedAmount, message);
            }
        };
    }

    /**
     * Display an error message in the console when a reward cannot be loaded because of a configuration error.
     *
     * @param fileName   the name of the file where the error occurred
     * @param parameter  the parameter that caused the error
     * @param reason     the reason of the error
     * @param questIndex the index of the quest in the file
     */
    public void configurationError(String fileName, String parameter, String reason, String questIndex) {
        PluginLogger.error("-----------------------------------");
        PluginLogger.error("Invalid reward configuration detected.");
        PluginLogger.error("File : " + fileName);
        PluginLogger.error("Reason : " + reason);

        if (parameter != null) {
            PluginLogger.error("Parameter : " + parameter);
        }

        if (questIndex != null) {
            PluginLogger.error("Quest index : " + questIndex);
        }

        PluginLogger.error("-----------------------------------");
    }
}
