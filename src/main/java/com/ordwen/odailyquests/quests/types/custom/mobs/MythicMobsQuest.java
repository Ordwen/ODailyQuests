package com.ordwen.odailyquests.quests.types.custom.mobs;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class MythicMobsQuest extends CustomMobQuest {

    public MythicMobsQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "MYTHIC_MOBS";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof MythicMobDeathEvent event) {
            Debugger.addDebug("MythicMobsQuest: Checking if required entity was killed.");
            Debugger.addDebug("MythicMobsQuest: Killed entity: " + event.getMobType().getInternalName());
            return super.isRequiredEntity(event.getMobType().getInternalName());
        }
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            PluginLogger.configurationError(file, index, null, "MythicMobs is not enabled on the server.");
            return false;
        }

        return super.loadParameters(section, file, index);
    }
}
