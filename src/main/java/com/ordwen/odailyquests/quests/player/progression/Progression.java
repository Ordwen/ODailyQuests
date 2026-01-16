package com.ordwen.odailyquests.quests.player.progression;

public class Progression {

    private final int requiredAmount;
    private Double rewardAmount;
    private int advancement;
    private boolean isAchieved;
    private int selectedRequiredIndex;


    /**
     * Progression constructor.
     *
     * @param rewardAmount reward amount for this quest.
     * @param advancement  progression of quest.
     * @param isAchieved   status of quest.
     */
    public Progression(int requiredAmount, double rewardAmount, int advancement, boolean isAchieved) {
        this.requiredAmount = requiredAmount;
        this.rewardAmount = null;
        this.rewardAmount = rewardAmount;
        this.advancement = advancement;
        this.isAchieved = isAchieved;
    }

    /**
     * Progression constructor.
     *
     * @param advancement progression of quest.
     * @param isAchieved  status of quest.
     */
    public Progression(int requiredAmount, int advancement, boolean isAchieved) {
        this.requiredAmount = requiredAmount;
        this.advancement = advancement;
        this.isAchieved = isAchieved;
    }

    /**
     * Get the progression of quest.
     *
     * @return progression.
     */
    public int getAdvancement() {
        return this.advancement;
    }

    /**
     * Increment the progression of quest.
     */
    public void increaseAdvancement() {
        this.advancement++;
    }

    /**
     * Get status of quest.
     *
     * @return status.
     */
    public boolean isAchieved() {
        return this.isAchieved;
    }

    /**
     * Set status of quest.
     */
    public void setAchieved() {
        this.isAchieved = true;
    }

    /**
     * Get required amount of quest.
     *
     * @return required amount.
     */
    public int getRequiredAmount() {
        return this.requiredAmount;
    }

    /**
     * Get reward amount of quest.
     *
     * @return reward amount.
     */
    public double getRewardAmount() {
        return rewardAmount == null ? 0 : rewardAmount;
    }

    /**
     * Check if reward amount is defined.
     *
     * @return true if reward amount is defined.
     */
    public boolean hasRewardAmount() {
        return rewardAmount != null;
    }

    /**
     * Set reward amount of quest.
     *
     * @param rewardAmount reward amount.
     */
    public void setRewardAmount(double rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    /**
     * Get random required element index.
     *
     * @return random required element.
     */
    public int getSelectedRequiredIndex() {
        return this.selectedRequiredIndex;
    }

    /**
     * Set selected random required element index.
     *
     * @param selectedRequiredIndex the random required element
     */
    public void setSelectedRequiredIndex(int selectedRequiredIndex) {
        this.selectedRequiredIndex = selectedRequiredIndex;
    }
}
