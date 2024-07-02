package com.ordwen.odailyquests.quests.types.custom.mobs;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class EliteMobsQuest extends CustomMobQuest {

    public EliteMobsQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "ELITE_MOBS";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EliteMobDeathEvent event) {
            return super.isRequiredEntity(event.getEliteEntity().getName().substring(event.getEliteEntity().getName().indexOf(' ') + 1));
        }
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("EliteMobs")) {
            PluginLogger.configurationError(file, index, null, "EliteMobs is not enabled on the server.");
            return false;
        }

        return super.loadParameters(section, file, index);
    }
}
