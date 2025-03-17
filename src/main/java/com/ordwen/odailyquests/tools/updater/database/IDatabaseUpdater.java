package com.ordwen.odailyquests.tools.updater.database;

import com.ordwen.odailyquests.ODailyQuests;

public interface IDatabaseUpdater {
    void apply(ODailyQuests plugin, String version);

    void applyMySQL();
    void applySQLite();
    void applyYAML();
}