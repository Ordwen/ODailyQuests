package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            setPlayerQuestProgression(player, event.getItem().getItemStack(), event.getItem().getItemStack().getAmount(), QuestType.PICKUP, event.getItem().getUniqueId().toString());
        }
    }
}

