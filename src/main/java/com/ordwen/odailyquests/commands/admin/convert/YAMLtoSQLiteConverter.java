package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.sqlite.SQLiteManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.file.FileConfiguration;

public class YAMLtoSQLiteConverter extends SQLConverter {

    public boolean convert() {

        try {
            ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {

                final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
                final SQLManager sqlManager = new SQLiteManager();

                convertData(progressionFile, sqlManager);
            });
        } catch (Exception e) {
            PluginLogger.error("An error occurred while converting YAML to SQLite.");
            PluginLogger.error(e.getMessage());
            return false;
        }

        return true;
    }
}
