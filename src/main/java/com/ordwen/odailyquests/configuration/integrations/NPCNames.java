package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

public class NPCNames implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public NPCNames(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private String playerNPCName;
    private String globalNPCName;
    private String easyNPCName;
    private String mediumNPCName;
    private String hardNPCName;

    @Override
    public void load() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("npcs");

        if (section == null) {
            PluginLogger.error("NPCs names section not found in the config. NPCs names will not be loaded.");
            return;
        }

        playerNPCName = TextFormatter.format(section.getString(".name_player"));
        globalNPCName = TextFormatter.format(section.getString(".name_global"));
        easyNPCName = TextFormatter.format(section.getString(".name_easy"));
        mediumNPCName = TextFormatter.format(section.getString(".name_medium"));
        hardNPCName = TextFormatter.format(section.getString(".name_hard"));
    }

    private static NPCNames getInstance() {
        return ConfigFactory.getConfig(NPCNames.class);
    }

    public static String getPlayerNPCName() {
        return getInstance().playerNPCName;
    }

    public static String getGlobalNPCName() {
        return getInstance().globalNPCName;
    }

    public static String getEasyNPCName() {
        return getInstance().easyNPCName;
    }

    public static String getMediumNPCName() {
        return getInstance().mediumNPCName;
    }

    public static String getHardNPCName() {
        return getInstance().hardNPCName;
    }
}
