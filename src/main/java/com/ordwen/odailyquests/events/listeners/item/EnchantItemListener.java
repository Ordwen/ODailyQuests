package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("EnchantItemListener: onEnchantItemEvent summoned by " + event.getEnchanter().getName() + " for " + event.getItem().getType() + ".");

        setPlayerQuestProgression(event.getEnchanter(), event.getItem(), 1, QuestType.ENCHANT);
    }
}
