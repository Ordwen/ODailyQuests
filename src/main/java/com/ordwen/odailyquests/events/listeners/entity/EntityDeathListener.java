package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.RoseStackerEnabled;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.tools.PluginUtils;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Optional;

public class EntityDeathListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if (killer == null) return;

        if (isExcludedByMythicMobs(entity)) return;
        if (isSpawnerKillWithoutBypass(entity, killer)) return;
        if (isHandledByWildOrRoseStacker(event, entity)) return;

        Debugger.write("EntityDeathListener: onEntityDeathEvent summoned by " + killer.getName() + " for " + entity.getType() + ".");
        setPlayerQuestProgression(event, killer, 1, "KILL");
    }

    /**
     * Returns true if the kill should be excluded due to MythicMobs rules.
     */
    private boolean isExcludedByMythicMobs(LivingEntity entity) {
        if (!PluginUtils.isPluginEnabled("MythicMobs")) return false;

        final Optional<ActiveMob> opt = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId());
        if (opt.isEmpty()) {
            Debugger.write("EntityDeathListener: Mob has no active mob.");
            return false;
        }

        Debugger.write("EntityDeathListener: Entity is a MythicMob, checking if it should be excluded.");

        final ActiveMob mythicMob = opt.get();
        final String internalName = mythicMob.getType().getInternalName();
        final String upper = internalName.toUpperCase();

        EntityType mapped = null;
        try {
            mapped = EntityType.valueOf(upper);
        } catch (IllegalArgumentException ignore) {
            // Internal name does not match any Bukkit EntityType -> custom MythicMob
        }

        if (mapped == null) {
            Debugger.write("EntityDeathListener: Entity is a custom MythicMob (" + internalName + "), cancelling progression.");
            return true;
        }

        if (mapped != entity.getType()) {
            Debugger.write("EntityDeathListener: InternalName maps to " + mapped + " but entity is " + entity.getType() + ", cancelling progression.");
            return true;
        }

        Debugger.write("EntityDeathListener: MythicMob matches vanilla type " + mapped + ", progression allowed.");
        return false;
    }

    /**
     * Returns true if entity comes from a spawner and the killer lacks the bypass permission.
     */
    private boolean isSpawnerKillWithoutBypass(LivingEntity entity, Player killer) {
        final boolean fromSpawner = EntitySource.isEntityFromSpawner(entity);
        final boolean hasBypass = killer.hasPermission(QuestsPermissions.QUESTS_PLAYER_BYPASS_SPAWNER.get());

        if (fromSpawner && !hasBypass) {
            Debugger.write("EntityDeathListener: Entity is from spawner, cancelling progression.");
            return true;
        }
        return false;
    }

    /**
     * Returns true if WildStacker/RoseStacker should handle this death instead.
     */
    private boolean isHandledByWildOrRoseStacker(EntityDeathEvent event, LivingEntity entity) {
        if (WildStackerEnabled.isEnabled()) {
            Debugger.write("EntityDeathListener: Entity is from WildStacker, must be handled by WildStacker.");
            return true;
        }

        if (RoseStackerEnabled.isEnabled()) {
            Debugger.write("EntityDeathListener: RoseStacker enabled, checking if it is stacked.");
            final StackedEntity stacked = RoseStackerAPI.getInstance().getStackedEntity(entity);
            if (stacked != null && stacked.areMultipleEntitiesDying(event)) {
                Debugger.write("EntityDeathListener: Entity is stacked and several are dying, must be handled by RoseStacker.");
                return true;
            }
        }
        return false;
    }
}
