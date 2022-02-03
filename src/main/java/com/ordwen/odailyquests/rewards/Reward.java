package com.ordwen.odailyquests.rewards;

public class Reward {

    RewardType rewardType;
    String command;
    int amount;

    /**
     * Constructor for a reward with a command.
     * @param command the reward-command.
     */
    public Reward(RewardType rewardType, String command) {
        this.rewardType = rewardType;
        this.command = command;
    }

    /**
     * Constructor for other reward.
     * @param amount the reward amount.
     */
    public Reward(RewardType rewardType, int amount) {
        this.rewardType = rewardType;
        this.amount = amount;
    }

    /**
     * Get the command of a reward.
     * @return the command to perform.
     */
    public String getRewardCommand() {
        return this.command;
    }

    /**
     * Get the amount of a reward.
     * @return the quantity to give.
     */
    public int getRewardAmount() {
        return this.amount;
    }

    /**
     * Get the reward type of a reward.
     * @return reward-type.
     */
    public RewardType getRewardType() {
        return this.rewardType;
    }
}
