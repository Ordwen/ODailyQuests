package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.api.events.CategoryTotalRewardReachedEvent;
import com.ordwen.odailyquests.configuration.functionalities.rewards.TotalRewards;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;

public class CategoryTotalRewardReachedListener implements Listener {

    @EventHandler
    public void onCategoryTotalRewardReached(CategoryTotalRewardReachedEvent event) {
        if (event.isCancelled()) return;

        final Reward reward = TotalRewards.getCategoryTotalReward(event.getCategory(), event.getCompletedInCategory());
        if (reward == null) return;
        RewardManager.sendReward(event.getPlayer(), reward, Collections.emptyMap());
    }
}
