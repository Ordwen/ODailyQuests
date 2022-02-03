package com.ordwen.odailyquests.rewards;

public class Reward {

    String command;
    int amount;

    /**
     * Constructor for a reward with a command.
     * @param command the reward-command.
     */
    public Reward(String command) {
        this.command = command;
    }

    /**
     * Constructor for other reward.
     * @param amount the reward amount.
     */
    public Reward(int amount) {
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
}
