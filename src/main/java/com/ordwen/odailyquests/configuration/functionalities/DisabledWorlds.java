package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

import java.util.HashSet;
import java.util.Set;

public class DisabledWorlds implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public DisabledWorlds(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private Set<String> disabledWorldNames;

    @Override
    public void load() {
        disabledWorldNames = new HashSet<>();
        disabledWorldNames.addAll(configurationFile.getConfig().getStringList("disabled_worlds"));
    }

    public boolean isWorldDisabledInternal(String world) {
        boolean isDisabled = false;
        for (String disabledWorld : disabledWorldNames) {
            if (disabledWorld.startsWith("?")) {
                final String worldLabel = disabledWorld.substring(1);
                isDisabled = world.endsWith(worldLabel);
            }
            else if (disabledWorld.endsWith("?")) {
                final String worldLabel = disabledWorld.substring(0, disabledWorld.length() - 1);
                isDisabled = world.startsWith(worldLabel);
            }
            else {
                isDisabled = world.equals(disabledWorld);
            }

            if (isDisabled) break;
        }

        return isDisabled;
    }

    private static DisabledWorlds getInstance() {
        return ConfigFactory.getConfig(DisabledWorlds.class);
    }

    public static boolean isWorldDisabled(String world) {
        return getInstance().isWorldDisabledInternal(world);
    }
}
