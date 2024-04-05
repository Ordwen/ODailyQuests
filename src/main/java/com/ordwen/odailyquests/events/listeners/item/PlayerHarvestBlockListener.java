package com.ordwen.odailyquests.events.listeners.item;


import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerHarvestBlockListener extends PlayerProgressor implements Listener {

    public static ItemStack current;

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final List<ItemStack> drops = event.getItemsHarvested();

        for (ItemStack item : drops) {
            current = new ItemStack(item.getType());
            setPlayerQuestProgression(event, player, item.getAmount(), "FARMING");
        }
    }
}
