package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.ConfigUpdater;

public class Update225to230 extends ConfigUpdater {

    public Update225to230(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        setDefaultConfigItem("reroll_only_if_not_achieved", false, config, configFile);
        setDefaultConfigItem("shared_mobs", false, config, configFile);

        setDefaultConfigItem("progress_bar.symbol", "|", config, configFile);
        setDefaultConfigItem("progress_bar.completed_color", "&a", config, configFile);
        setDefaultConfigItem("progress_bar.remaining_color", "&7", config, configFile);
        setDefaultConfigItem("progress_bar.amount_of_symbols", 20, config, configFile);

        updateVersion(version);
    }
}
