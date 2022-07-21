package com.ordwen.odailyquests.apis.hooks.stackers;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.ProgressionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WildStackerHook implements Listener {

    /**
     * Check if WildStacker is enabled.
     * @return true if WildStacker is enabled.
     */
    public static boolean isWildStackerSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("WildStacker");
    }

    @EventHandler
    public void onEntityUnstackEvent(EntityUnstackEvent event) {
        if (event.getUnstackSource() instanceof Player player) {
            ProgressionManager.setPlayerQuestProgression(player.getName(), null, event.getEntity().getType(), null, event.getAmount(), QuestType.KILL);
        }
    }
}
