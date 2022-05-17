package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class NPCNames {

    private final ConfigurationFiles configurationFiles;

    public NPCNames(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static String playerNPCName;
    private static String globalNPCName;
    private static String easyNPCName;
    private static String mediumNPCName;
    private static String hardNPCName;

    /**
     * Load all NPC names.
     */
    public void loadNPCNames() {
        playerNPCName = configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_player");
        globalNPCName = configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_global");
        easyNPCName = configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_easy");
        mediumNPCName = configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_medium");
        hardNPCName = configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_hard");
    }

    /**
     * Get player NPC name.
     * @return player NPC name.
     */
    public static String getPlayerNPCName() {
        return playerNPCName;
    }

    /**
     * Get global NPC name.
     * @return global NPC name.
     */
    public static String getGlobalNPCName() {
        return globalNPCName;
    }

    /**
     * Get easy NPC name.
     * @return easy NPC name.
     */
    public static String getEasyNPCName() {
        return easyNPCName;
    }

    /**
     * Get medium NPC name.
     * @return medium NPC name.
     */
    public static String getMediumNPCName() {
        return mediumNPCName;
    }

    /**
     * Get hard NPC name.
     * @return hard NPC name.
     */
    public static String getHardNPCName() {
        return hardNPCName;
    }
}
