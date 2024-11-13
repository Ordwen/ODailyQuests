package com.ordwen.odailyquests.rewards;

import java.util.List;

public class Reward {

    final RewardType rewardType;
    final List<String> commands;
    final String currencyLabel;
    final String currencyDisplayName;
    double amount;

    /**
     * Constructor for a reward with a command.
     *
     * @param commands the reward-command.
     */
    public Reward(RewardType rewardType, List<String> commands) {
        this.rewardType = rewardType;
        this.commands = commands;

        this.currencyLabel = null;
        this.currencyDisplayName = null;
    }

    /**
     * Constructor for other reward.
     *
     * @param amount the reward amount.
     */
    public Reward(RewardType rewardType, double amount) {
        this.rewardType = rewardType;
        this.amount = amount;

        this.commands = null;
        this.currencyLabel = null;
        this.currencyDisplayName = null;
    }

    /**
     * Constructor for a reward that is using CoinsEngine.
     *
     * @param currencyLabel       the reward-currency, by its name in the configuration.
     * @param currencyDisplayName the name of the currency that will be displayed to the player.
     * @param amount              the reward amount.
     */
    public Reward(RewardType rewardType, String currencyLabel, String currencyDisplayName, int amount) {
        this.rewardType = rewardType;
        this.currencyLabel = currencyLabel;
        this.currencyDisplayName = currencyDisplayName;
        this.amount = amount;

        this.commands = null;
    }

    /**
     * Get the command of a reward.
     *
     * @return the command to perform.
     */
    public List<String> getRewardCommands() {
        return this.commands;
    }

    /**
     * Get the amount of a reward.
     *
     * @return the quantity to give.
     */
    public double getRewardAmount() {
        return this.amount;
    }

    /**
     * Get the currency of a reward.
     *
     * @return the currency to give.
     */
    public String getRewardCurrency() {
        return this.currencyLabel;
    }

    /**
     * Get the currency display name of a reward.
     *
     * @return the currency display name to give.
     */
    public String getRewardCurrencyDisplayName() {
        return this.currencyDisplayName;
    }

    /**
     * Get the reward type of reward.
     *
     * @return reward-type.
     */
    public RewardType getRewardType() {
        return this.rewardType;
    }
}
