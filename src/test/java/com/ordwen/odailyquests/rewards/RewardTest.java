package com.ordwen.odailyquests.rewards;

import org.junit.Test;

import java.util.Collections;

public class RewardTest {

    Reward reward1 = new Reward(RewardType.COMMAND, Collections.singletonList("say Hello World !"));
    Reward reward2 = new Reward(RewardType.MONEY, 500);

    @Test
    public void getRewardCommand() {
        assert reward1.getRewardCommands().equals(Collections.singletonList("say Hello World !"));
    }

    @Test
    public void getRewardAmount() {
        assert reward2.getRewardAmount() == 500;
    }

    @Test
    public void getRewardType() {
        assert reward1.getRewardType() == RewardType.COMMAND;
        assert reward2.getRewardType() == RewardType.MONEY;
    }
}