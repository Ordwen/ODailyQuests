package com.ordwen.odailyquests.events.listeners.entity.custom.stackers;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityUnstackListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onWildStackerEntityUnstackEvent(EntityUnstackEvent event) {
        if (event.isCancelled()) return;

        if (event.getUnstackSource() instanceof Player player) {
            setPlayerQuestProgression(player, event.getEntity().getType(), null, event.getAmount(), QuestType.KILL, null);
        }
    }
}
