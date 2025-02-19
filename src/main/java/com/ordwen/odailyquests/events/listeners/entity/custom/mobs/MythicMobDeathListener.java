package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;


import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.SharedMobs;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class MythicMobDeathListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onMythicMobsDeathEvent(MythicMobDeathEvent event) {
        if (SharedMobs.isEnabled()) {
            final ActiveMob.ThreatTable threatTable = event.getMob().getThreatTable();

            if (threatTable == null) {
                Debugger.write("Threat table is null for mob: " + event.getMob().getDisplayName());
                return;
            }

            final Set<AbstractEntity> targets = threatTable.getAllThreatTargets();

            for (AbstractEntity target : targets) {
                if (target.getBukkitEntity() instanceof Player player) {
                    setPlayerQuestProgression(event, player, 1, "MYTHIC_MOBS");
                }
            }

            return;
        }

        if (event.getKiller() != null && event.getKiller() instanceof Player player) {
            setPlayerQuestProgression(event, player, 1, "MYTHIC_MOBS");
        }
    }
}
