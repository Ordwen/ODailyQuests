package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.ChatColor;

public class NPCNames {

    private static String playerNPCName;
    private static String globalNPCName;
    private static String easyNPCName;
    private static String mediumNPCName;
    private static String hardNPCName;
    private final ConfigurationFiles configurationFiles;
    public NPCNames(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Get player NPC name.
     *
     * @return player NPC name.
     */
    public static String getPlayerNPCName() {
        return playerNPCName;
    }

    /**
     * Get global NPC name.
     *
     * @return global NPC name.
     */
    public static String getGlobalNPCName() {
        return globalNPCName;
    }

    /**
     * Get easy NPC name.
     *
     * @return easy NPC name.
     */
    public static String getEasyNPCName() {
        return easyNPCName;
    }

    /**
     * Get medium NPC name.
     *
     * @return medium NPC name.
     */
    public static String getMediumNPCName() {
        return mediumNPCName;
    }

    /**
     * Get hard NPC name.
     *
     * @return hard NPC name.
     */
    public static String getHardNPCName() {
        return hardNPCName;
    }

    /**
     * Load all NPC names.
     */
    public void loadNPCNames() {
        playerNPCName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_player"));
        globalNPCName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_global"));
        easyNPCName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_easy"));
        mediumNPCName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_medium"));
        hardNPCName = ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection("npcs").getString(".name_hard"));
    }
}
