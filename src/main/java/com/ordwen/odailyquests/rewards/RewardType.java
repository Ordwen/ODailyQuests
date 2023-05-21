package com.ordwen.odailyquests.rewards;

/**
 * List of all possible rewards types.
 */
public enum RewardType {
    COMMAND("COMMAND"),
    EXP_POINTS("EXP_POINTS"),
    EXP_LEVELS("EXP_LEVELS"),
    MONEY("MONEY"),
    POINTS("POINTS"),
    NONE("NONE"),

    ;

    private final String rewardTypeName;

    /**
     * Reward constructor.
     * @param rewardName the name of the reward.
     */
    RewardType(String rewardName) {
        this.rewardTypeName = rewardName;
    }

    /**
     * Get the user-configured name for a reward.
     * @return the name of the reward.
     */
    public String getRewardTypeName() {
        return this.rewardTypeName;
    }
}
