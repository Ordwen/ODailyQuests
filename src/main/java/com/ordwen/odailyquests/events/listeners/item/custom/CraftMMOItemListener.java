package com.ordwen.odailyquests.events.listeners.item.custom;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import net.Indyuce.mmoitems.api.event.CraftMMOItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CraftMMOItemListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onCraftMMOItemEvent(CraftMMOItemEvent event) {
        if (event.isCancelled()) {
            Debugger.write("CraftMMOItemEvent cancelled");
        }

        final Player player = event.getPlayer();
        final ItemStack result = event.getResult();
        if (result == null) {
            Debugger.write("CraftItemEvent: result is null");
            return;
        }

        Debugger.write("CraftMMOItemEvent: onCraftMMOItemEvent summoned by " + player.getName() + " for " + result.getType() + " x" + result.getAmount());
        setPlayerQuestProgression(event, player, result.getAmount(), "CRAFT");
    }
}
