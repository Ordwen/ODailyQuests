package com.ordwen.odailyquests.rewards;

import java.util.List;

public class Reward {

    RewardType rewardType;
    List<String> commands;
    int amount;

    /**
     * Constructor for a reward with a command.
     * @param commands the reward-command.
     */
    public Reward(RewardType rewardType, List<String> commands) {
        this.rewardType = rewardType;
        this.commands = commands;
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
    public List<String> getRewardCommands() {
        return this.commands;
    }

    /**
     * Get the amount of a reward.
     * @return the quantity to give.
     */
    public int getRewardAmount() {
        return this.amount;
    }

    /**
     * Get the reward type of reward.
     * @return reward-type.
     */
    public RewardType getRewardType() {
        return this.rewardType;
    }

}
