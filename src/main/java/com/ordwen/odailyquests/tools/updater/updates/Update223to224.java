package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.ConfigUpdater;

public class Update223to224 extends ConfigUpdater {

    public Update223to224(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin) {
        addDefaultConfigItem("use_custom_furnace_results", false, config, configFile);
        addDefaultConfigItem("disable_logs", false, config, configFile);
        addDefaultConfigItem("use_oraxen", false, config, configFile);

        addDefaultConfigItem("player_interface.disable_status", false, playerInterface, playerInterfaceFile);
    }
}
