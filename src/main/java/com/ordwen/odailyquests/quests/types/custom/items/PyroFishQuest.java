package com.ordwen.odailyquests.quests.types.custom.items;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class PyroFishQuest extends AbstractQuest {

    private String tier;
    private int id;

    protected PyroFishQuest(BasicQuest basicQuest) {
        super(basicQuest);
    }

    @Override
    public String getType() {
        return "PYRO_FISH";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PyroFishCatchEvent event) {
            return event.getTier().equals(this.tier) && event.getFishNumber() == this.id;
        }

        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PyroFishingPro")) {
            PluginLogger.configurationError(file, index, null, "You must have PyroFishingPro installed to use this quest.");
            return false;
        }

        if (!section.contains("pyro_fish_tier")) {
            PluginLogger.configurationError(file, index, "pyro_fish_tier", "You must specify the tier of the Pyro Fish.");
            return false;
        }

        if (!section.contains("pyro_fish_id")) {
            PluginLogger.configurationError(file, index, "pyro_fish_id", "You must specify the id of the Pyro Fish.");
            return false;
        }

        this.tier = section.getString("pyro_fish_tier");
        this.id = section.getInt("pyro_fish_id");

        return true;
    }
}
