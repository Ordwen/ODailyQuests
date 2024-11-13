package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

import java.util.HashSet;

public class DisabledWorlds {

    /* store disabled worlds */
    private static HashSet<String> disabledWorlds;
    private final ConfigurationFiles configurationFiles;

    public DisabledWorlds(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Check if a world is disabled.
     *
     * @param world name of the world to check.
     * @return true if the world is disabled.
     */
    public static boolean isWorldDisabled(final String world) {
        boolean isDisabled = false;
        for (final String disabledWorld : disabledWorlds) {
            if (disabledWorld.startsWith("?")) {
                final String worldLabel = disabledWorld.substring(1);
                isDisabled = world.endsWith(worldLabel);
            } else if (disabledWorld.endsWith("?")) {
                final String worldLabel = disabledWorld.substring(0, disabledWorld.length() - 1);
                isDisabled = world.startsWith(worldLabel);
            } else {
                isDisabled = world.equals(disabledWorld);
            }

            if (isDisabled) break;
        }

        return isDisabled;
    }

    /**
     * Load disabled worlds.
     */
    public void loadDisabledWorlds() {
        disabledWorlds = new HashSet<>();
        disabledWorlds.addAll(configurationFiles.getConfigFile().getStringList("disabled_worlds"));
    }
}
