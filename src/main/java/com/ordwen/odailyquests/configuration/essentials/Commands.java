package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.ConfigurationSection;

public class Commands {

    private final ConfigurationFiles configurationFiles;

    public Commands(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public void loadPluginCommands() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            ConfigurationSection commandsSection = configurationFiles.getConfigFile().getConfigurationSection(questSystem.getConfigPath() + "commands");
            ConfigurationSection mainCommandSection = commandsSection.getConfigurationSection("main_command");
            questSystem.setCommandName(mainCommandSection.getString("name"));
            Debugger.addDebug("Registered " + questSystem.getSystemName() + " player-command " + questSystem.getCommandName());
            for (String alias : mainCommandSection.getStringList("aliases")) {
                questSystem.getCommandAliases().add(alias);
                Debugger.addDebug("Added " + questSystem.getSystemName() + " player-command-alias " + alias);
            }
            ConfigurationSection adminCommandSection = commandsSection.getConfigurationSection("admin_command");
            questSystem.setAdminCommandName(adminCommandSection.getString("name"));
            Debugger.addDebug("Registered " + questSystem.getSystemName() + " admin-command " + questSystem.getAdminCommandName());
            for (String alias : adminCommandSection.getStringList("aliases")) {
                questSystem.getAdminCommandAliases().add(alias);
                Debugger.addDebug("Added " + questSystem.getSystemName() + " admin-command-alias " + alias);
            }
        });
    }
}
