package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

public class BucketFillListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        final ItemStack item = event.getItemStack();
        if (item == null) return;

        if (item.getType() == Material.MILK_BUCKET) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("BucketFillListener: onPlayerBucketFill summoned by " + event.getPlayer().getName());

            setPlayerQuestProgression(event, event.getPlayer(), 1, "MILKING");
        }
    }
}
