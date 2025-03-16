package com.ordwen.odailyquests.quests.player.progression;

public class Progression {

    private final int requiredAmount;
    private int advancement;
    private boolean isAchieved;

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
}
