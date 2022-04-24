package com.ordwen.odailyquests.apis;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EliteMobsAPI implements Listener {

    /**
     * Check if EliteMobs is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isEliteMobsSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("EliteMobs");
    }

    @EventHandler
    public void onEliteMobsDeathEvent(EliteMobDeathEvent event) {
        if (event.getEntityDeathEvent().getEntity().getKiller() != null) {
            ProgressionManager.setPlayerQuestProgression(event.getEntityDeathEvent().getEntity().getKiller().getName(), null, null, event.getEliteEntity().getName().substring(event.getEliteEntity().getName().indexOf(' ')+1), 1, QuestType.CUSTOM_MOBS);
        }
    }

}

