package com.ordwen.odailyquests.quests.types.custom.items;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

import java.util.HashSet;
import java.util.Set;

public class PyroFishQuest extends AbstractQuest {

    private final Set<String> expectedFish = new HashSet<>();

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
            if (expectedFish.isEmpty()) return true;

            final String tier = event.getTier();
            final int id = event.getFishNumber();

            final String concat = tier + ":" + id;
            return expectedFish.contains(concat);
        }

        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        expectedFish.clear();

        if (!Bukkit.getPluginManager().isPluginEnabled("PyroFishingPro")) {
            PluginLogger.configurationError(file, index, null, "You must have PyroFishingPro installed to use this quest.");
            return false;
        }

        if (section.isList("required")) {
            for (String fish : section.getStringList("required")) {
                if (checkFormat(fish, file, index)) expectedFish.add(fish);
            }
        } else if (section.isString("required")) {
            final String fish = section.getString("required");
            if (checkFormat(fish, file, index)) expectedFish.add(fish);
        }

        return true;
    }

    /**
     * Check the format of the fish
     *
     * @param fish  the fish to check
     * @param file  the file the fish is in
     * @param index the index of the quest
     * @return true if the format is correct
     */
    private boolean checkFormat(String fish, String file, int index) {
        final String[] split = fish.split(":");
        if (split.length != 2) {
            PluginLogger.configurationError(file, index, "required", "Invalid fish format: " + fish);
            return false;
        }

        return true;
    }
}
