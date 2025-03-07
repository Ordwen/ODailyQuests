package com.ordwen.odailyquests.tools.updater;

import com.ordwen.odailyquests.ODailyQuests;

public interface IConfigUpdater {
    void apply(ODailyQuests plugin, String version);
}