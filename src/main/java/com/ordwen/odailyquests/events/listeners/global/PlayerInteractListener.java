package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        final Block block = event.getClickedBlock();
        if (block == null) return;

        if (!(block.getType() == Material.PUMPKIN)) return;
        if (!(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS)) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("PlayerInteractListener: onPlayerInteract summoned by " + event.getPlayer().getName());

        setPlayerQuestProgression(event, event.getPlayer(), 1, "CARVE");
    }
}
