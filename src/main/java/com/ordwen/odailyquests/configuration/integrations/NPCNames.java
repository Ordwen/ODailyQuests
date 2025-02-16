package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

public class NPCNames implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public NPCNames(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private String playerNPCName;
    private String globalNPCName;
    private String easyNPCName;
    private String mediumNPCName;
    private String hardNPCName;

    @Override
    public void load() {
        final ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("npcs");

        if (section == null) {
            PluginLogger.error("NPCs names section not found in the config. NPCs names will not be loaded.");
            return;
        }

        playerNPCName = ColorConvert.convertColorCode(section.getString(".name_player"));
        globalNPCName = ColorConvert.convertColorCode(section.getString(".name_global"));
        easyNPCName = ColorConvert.convertColorCode(section.getString(".name_easy"));
        mediumNPCName = ColorConvert.convertColorCode(section.getString(".name_medium"));
        hardNPCName = ColorConvert.convertColorCode(section.getString(".name_hard"));
    }

    public static NPCNames getInstance() {
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
