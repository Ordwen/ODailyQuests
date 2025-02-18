package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.IConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Update223to224 implements IConfigUpdater {

    @Override
    public void apply(ODailyQuests plugin) {
        final File configFile = new File(plugin.getDataFolder(), "config.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        addDefaultConfigItem("use_custom_furnace_results", false, config, configFile);
        addDefaultConfigItem("disable_logs", false, config, configFile);
        addDefaultConfigItem("use_oraxen", false, config, configFile);

        final File playerInterfaceFile = new File(plugin.getDataFolder(), "playerInterface.yml");
        final FileConfiguration playerInterface = YamlConfiguration.loadConfiguration(playerInterfaceFile);

        addDefaultConfigItem("player_interface.disable_status", false, playerInterface, playerInterfaceFile);
    }
}
