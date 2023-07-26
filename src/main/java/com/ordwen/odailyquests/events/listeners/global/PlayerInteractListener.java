package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (!(event.getClickedBlock().getType() == Material.PUMPKIN)) return;
        if (!(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS)) return;

        setPlayerQuestProgression(event.getPlayer(), 1, QuestType.CARVE);
    }
}
