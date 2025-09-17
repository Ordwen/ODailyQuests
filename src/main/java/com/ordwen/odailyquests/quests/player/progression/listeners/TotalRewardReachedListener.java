package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.api.events.TotalRewardReachedEvent;
import com.ordwen.odailyquests.configuration.functionalities.rewards.TotalRewards;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;

public class TotalRewardReachedListener implements Listener {

    @EventHandler
    public void onTotalRewardReached(TotalRewardReachedEvent event) {
        if (event.isCancelled()) return;

        final Reward reward = TotalRewards.getGlobalTotalReward(event.getTotalCompleted());
        if (reward == null) return;
        RewardManager.sendReward(event.getPlayer(), reward, Collections.emptyMap());
    }
}
