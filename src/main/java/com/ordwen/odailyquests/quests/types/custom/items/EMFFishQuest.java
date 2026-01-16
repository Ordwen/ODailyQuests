package com.ordwen.odailyquests.quests.types.custom.items;

import com.oheers.fish.api.events.EMFFishCaughtEvent;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import java.util.HashSet;
import java.util.Set;

public class EMFFishQuest extends AbstractQuest {

    private final Set<String> expectedFish = new HashSet<>();

    public EMFFishQuest(BasicQuest basicQuest) {
        super(basicQuest);
    }

    @Override
    public String getType() {
        return "EMF_FISH";
    }

    @Override
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof EMFFishCaughtEvent event) {
            if (expectedFish.isEmpty()) return true;

            final String fishName = event.getFish().getName().toLowerCase();
            return expectedFish.contains(fishName);
        }

        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        expectedFish.clear();

        if (!Bukkit.getPluginManager().isPluginEnabled("EvenMoreFish")) {
            PluginLogger.configurationError(file, index, null, "You must have EvenMoreFish installed to use this quest.");
            return false;
        }

        if (section.isList("required")) {
            for (String fish : section.getStringList("required")) {
                expectedFish.add(fish.toLowerCase());
            }
        } else if (section.isString("required")) {
            final String fish = section.getString("required");
            expectedFish.add(fish.toLowerCase());
        }

        return true;
    }
}