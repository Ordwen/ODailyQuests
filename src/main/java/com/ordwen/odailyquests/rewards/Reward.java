package com.ordwen.odailyquests.rewards;

import java.util.List;

public class Reward {

    final RewardType rewardType;
    final List<String> commands;
    final String currencyLabel;
    final String currencyDisplayName;
    final String message;
    double amount;
    final Double minAmount;
    final Double maxAmount;

    /**
     * Constructor for a reward with a command.
     *
     * @param commands the reward-command.
     */
    public Reward(RewardType rewardType, List<String> commands, String message) {
        this.rewardType = rewardType;
        this.commands = commands;
        this.message = message;

        this.currencyLabel = null;
        this.currencyDisplayName = null;
        this.minAmount = null;
        this.maxAmount = null;
    }

    /**
     * Constructor for other reward.
     *
     * @param amount the reward amount.
     */
    public Reward(RewardType rewardType, double amount, String message) {
        this.rewardType = rewardType;
        this.amount = amount;
        this.message = message;

        this.commands = null;
        this.currencyLabel = null;
        this.currencyDisplayName = null;
        this.minAmount = null;
        this.maxAmount = null;
    }


    /**
     * Constructor for a reward with a random amount range.
     *
     * @param minAmount the minimum reward amount (inclusive).
     * @param maxAmount the maximum reward amount (inclusive).
     */
    public Reward(RewardType rewardType, double minAmount, double maxAmount, String message) {
        this.rewardType = rewardType;
        this.amount = minAmount;
        this.message = message;

        this.commands = null;
        this.currencyLabel = null;
        this.currencyDisplayName = null;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    /**
     * Constructor for a reward that is using CoinsEngine.
     *
     * @param currencyLabel       the reward-currency, by its name in the configuration.
     * @param currencyDisplayName the name of the currency that will be displayed to the player.
     * @param amount              the reward amount.
     */
    public Reward(RewardType rewardType, String currencyLabel, String currencyDisplayName, int amount, String message) {
        this(rewardType, currencyLabel, currencyDisplayName, (double) amount, message);
    }

    /**
     * Constructor for a reward that is using CoinsEngine.
     *
     * @param currencyLabel       the reward-currency, by its name in the configuration.
     * @param currencyDisplayName the name of the currency that will be displayed to the player.
     * @param amount              the reward amount.
     */
    public Reward(RewardType rewardType, String currencyLabel, String currencyDisplayName, double amount, String message) {
        this.rewardType = rewardType;
        this.currencyLabel = currencyLabel;
        this.currencyDisplayName = currencyDisplayName;
        this.amount = amount;
        this.message = message;

        this.commands = null;
        this.minAmount = null;
        this.maxAmount = null;
    }

    /**
     * Constructor for a CoinsEngine reward with a random amount range.
     *
     * @param minAmount the minimum reward amount (inclusive).
     * @param maxAmount the maximum reward amount (inclusive).
     */
    public Reward(RewardType rewardType, String currencyLabel, String currencyDisplayName, double minAmount, double maxAmount, String message) {
        this.rewardType = rewardType;
        this.currencyLabel = currencyLabel;
        this.currencyDisplayName = currencyDisplayName;
        this.amount = minAmount;
        this.message = message;

        this.commands = null;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
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
     * Returns true if the reward amount is defined as a random range.
     *
     * @return true when the reward amount has a range, false otherwise.
     */
    public boolean hasRandomAmount() {
        return minAmount != null && maxAmount != null;
    }

    /**
     * Returns the minimum amount of the reward range.
     *
     * @return the minimum amount, or the fixed amount if not random.
     */
    public double getMinRewardAmount() {
        return hasRandomAmount() ? minAmount : amount;
    }

    /**
     * Returns the maximum amount of the reward range.
     *
     * @return the maximum amount, or the fixed amount if not random.
     */
    public double getMaxRewardAmount() {
        return hasRandomAmount() ? maxAmount : amount;
    }

    /**
     * Resolves the reward amount, returning a random value if a range is configured.
     *
     * @return resolved reward amount.
     */
    public double resolveRewardAmount() {
        if (!hasRandomAmount()) {
            return amount;
        }

        if (minAmount.equals(maxAmount)) {
            return minAmount;
        }

        boolean minIsWhole = minAmount % 1 == 0;
        boolean maxIsWhole = maxAmount % 1 == 0;
        if (minIsWhole && maxIsWhole) {
            int min = minAmount.intValue();
            int max = maxAmount.intValue();
            return java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        return java.util.concurrent.ThreadLocalRandom.current().nextDouble(minAmount, Math.nextUp(maxAmount));
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

    /**
     * Get the message of a reward.
     *
     * @return the message to send to the player.
     */
    public String getMessage() {
        return this.message;
    }
}
