package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        setPlayerQuestProgression(event.getPlayer(), event.getItem(), 1, QuestType.CONSUME, null);
    }
}
