package com.ordwen.odailyquests.quests.player.progression;

public class Progression {

    private int progression;
    private boolean isAchieved;

    /**
     * Progression constructor.
     *
     * @param progression progression of quest.
     * @param isAchieved  status of quest.
     */
    public Progression(int progression, boolean isAchieved) {
        this.progression = progression;
        this.isAchieved = isAchieved;
    }

    /**
     * Get the progression of quest.
     *
     * @return progression.
     */
    public int getProgression() {
        return this.progression;
    }

    /**
     * Increment the progression of quest.
     */
    public void increaseProgression() {
        this.progression++;
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
}
