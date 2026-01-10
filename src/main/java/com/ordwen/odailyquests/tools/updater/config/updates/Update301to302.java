package com.ordwen.odailyquests.tools.updater.config.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.config.ConfigUpdater;

public class Update301to302 extends ConfigUpdater {

    public Update301to302(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        setDefaultConfigItem("reroll_maximum", -1, config, configFile, false);

        updateVersion(version);
    }
}
