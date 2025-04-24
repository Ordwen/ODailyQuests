package com.ordwen.odailyquests.quests.types.custom.vote;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class NuVotifierQuest extends AbstractQuest {

    public NuVotifierQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "NU_VOTIFIER";
    }

    @Override
    public boolean canProgress(Event provided, Progression progression) {
        return provided instanceof VotifierEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Votifier")) {
            PluginLogger.configurationError(file, index, null, "You must have Votifier installed to use this quest.");
            return false;
        }

        return true;
    }

}
