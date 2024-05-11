package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.ChatColor;

public class NPCNames {

    private final ConfigurationFiles configurationFiles;

    public NPCNames(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Load all NPC names.
     */
    public void loadNPCNames() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setPlayerNPCName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "npcs").getString(".name_player")));
            questSystem.setGlobalNPCName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "npcs").getString(".name_global")));
            questSystem.setEasyNPCName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "npcs").getString(".name_easy")));
            questSystem.setMediumNPCName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "npcs").getString(".name_medium")));
            questSystem.setHardNPCName(ChatColor.translateAlternateColorCodes('&', configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "npcs").getString(".name_hard")));
        });
    }
}
