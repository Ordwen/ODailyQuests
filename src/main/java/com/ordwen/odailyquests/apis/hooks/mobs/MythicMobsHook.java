package com.ordwen.odailyquests.apis.hooks.mobs;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsHook implements Listener {

    /**
     * Check if MythicMobs is enabled.
     * @return true if MythicMobs is enabled.
     */
    public static boolean isMythicMobsSetup() {
         return Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs");
    }

    @EventHandler
    public void onMythicMobsDeathEvent(MythicMobDeathEvent event) {
        if (event.getKiller() != null && event.getKiller() instanceof Player) {
            ProgressionManager.setPlayerQuestProgression(event.getKiller().getName(), null, null, event.getMobType().getInternalName(), 1, QuestType.CUSTOM_MOBS);
        }
    }
}
