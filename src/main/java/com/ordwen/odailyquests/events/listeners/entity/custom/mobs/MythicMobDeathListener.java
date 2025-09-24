package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;


import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.SharedMobs;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class MythicMobDeathListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMobsDeathEvent(MythicMobDeathEvent event) {
        final ActiveMob activeMob = event.getMob();
        Debugger.write("MythicMobDeathEvent triggered for mob: " + activeMob.getDisplayName());

        if (SharedMobs.isEnabled()) {
            Debugger.write("SharedMobs is enabled, processing threat table for mob: " + activeMob.getDisplayName());
            final ActiveMob.ThreatTable threatTable = activeMob.getThreatTable();

            if (threatTable == null || threatTable.getAllThreatTargets().isEmpty()) {
                Debugger.write("Threat table is null or empty for mob: " + activeMob.getDisplayName() + ", falling back to classic progression.");
                if (event.getKiller() instanceof Player player) {
                    setPlayerQuestProgression(event, player, 1, "MYTHIC_MOBS");
                }
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

        if (event.getKiller() instanceof Player player) {
            setPlayerQuestProgression(event, player, 1, "MYTHIC_MOBS");
        }
    }
}
