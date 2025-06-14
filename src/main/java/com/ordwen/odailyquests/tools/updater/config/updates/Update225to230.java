package com.ordwen.odailyquests.tools.updater.config.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.config.ConfigUpdater;

public class Update225to230 extends ConfigUpdater {

    public Update225to230(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        setDefaultConfigItem("reroll_only_if_not_achieved", false, config, configFile, false);
        setDefaultConfigItem("shared_mobs", false, config, configFile, false);

        setDefaultConfigItem("progress_bar.symbol", "|", config, configFile, false);
        setDefaultConfigItem("progress_bar.completed_color", "&a", config, configFile, false);
        setDefaultConfigItem("progress_bar.remaining_color", "&7", config, configFile, false);
        setDefaultConfigItem("progress_bar.amount_of_symbols", 20, config, configFile, false);

        updateVersion(version);
    }
}
