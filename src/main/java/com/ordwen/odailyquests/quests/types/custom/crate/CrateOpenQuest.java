package com.ordwen.odailyquests.quests.types.custom.crate;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.PluginUtils;
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
    public boolean canProgress(@Nullable Event provided, Progression progression) {
        if (provided instanceof CrateOpenEvent event) {
            final String crate = event.getCrate().getName();
            Debugger.write("CrateOpenQuest: canProgress checking crate " + crate);
            return expectedCrate.isEmpty() || expectedCrate.contains(crate.toLowerCase());
        }

        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        expectedCrate.clear();

        if (!PluginUtils.isPluginEnabled("ExcellentCrates")) {
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
