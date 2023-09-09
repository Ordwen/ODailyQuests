package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

import java.util.HashSet;

public class DisabledWorlds {

    private final ConfigurationFiles configurationFiles;

    public DisabledWorlds(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /* store disabled worlds */
    private static HashSet<String> disabledWorlds;

    /**
     * Load disabled worlds.
     */
    public void loadDisabledWorlds() {
        disabledWorlds = new HashSet<>();
        disabledWorlds.addAll(configurationFiles.getConfigFile().getStringList("disabled_worlds"));
    }

    /**
     * Check if a world is disabled.
     * @param world name of the world to check.
     * @return true if the world is disabled.
     */
    public static boolean isWorldDisabled(String world) {
        boolean isDisabled = false;
        for (String disabledWorld : disabledWorlds) {
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
}
