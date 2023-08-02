package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.api.events.AllCategoryQuestsCompletedEvent;
import com.ordwen.odailyquests.configuration.functionalities.rewards.CategoriesRewards;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AllCategoryQuestsCompletedListener implements Listener {

    @EventHandler
    public void onAllQuestsFromCategoryCompletedEvent(AllCategoryQuestsCompletedEvent event) {
        final String category = CategoriesLoader.getCategoryByName(event.getCategory()).getName();
        CategoriesRewards.sendCategoryReward(event.getPlayer(), category);
    }
}
