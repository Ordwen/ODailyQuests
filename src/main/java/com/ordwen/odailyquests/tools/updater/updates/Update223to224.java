package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.ConfigUpdater;

public class Update223to224 extends ConfigUpdater {

    public Update223to224(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        setDefaultConfigItem("use_custom_furnace_results", false, config, configFile);
        setDefaultConfigItem("disable_logs", false, config, configFile);
        setDefaultConfigItem("use_oraxen", false, config, configFile);

        setDefaultConfigItem("player_interface.disable_status", false, playerInterface, playerInterfaceFile);

        updateVersion(version);
    }
}
