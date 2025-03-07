package com.ordwen.odailyquests.quests.types.custom.crate;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

import java.util.HashSet;
import java.util.Set;

public class CrateOpenQuest extends AbstractQuest {

    private final Set<String> expectedCrate = new HashSet<>();

    public CrateOpenQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "CRATE_OPEN";
    }

    @Override
    public boolean canProgress(@Nullable Event provided) {
        if (provided instanceof CrateOpenEvent event) {
            final String crate = event.getCrate().getName();
            return expectedCrate.isEmpty() || expectedCrate.contains(crate.toLowerCase());
        }

        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        expectedCrate.clear();

        if (!Bukkit.getPluginManager().isPluginEnabled("ExcellentCrates")) {
            PluginLogger.configurationError(file, index, null, "You must have ExcellentCrates installed to use this quest.");
            return false;
        }

        if (section.isList("required")) {
            for (String crate : section.getStringList("required")) {
                expectedCrate.add(crate.toLowerCase());
            }
        } else if (section.isString("required")) {
            final String crate = section.getString("required");
            expectedCrate.add(crate.toLowerCase());
        }

        return true;
    }
}
