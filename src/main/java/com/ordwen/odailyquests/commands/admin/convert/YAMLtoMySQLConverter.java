package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql.MySQLManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class YAMLtoMySQLConverter extends SQLConverter {

    public boolean convert(ODailyQuests oDailyQuests, QuestSystem questSystem) {

        try {
            Bukkit.getScheduler().runTaskAsynchronously(oDailyQuests, () -> {

                final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
                final SQLManager sqlManager = new MySQLManager(ODailyQuests.INSTANCE);

                convertData(progressionFile, sqlManager, questSystem);
            });
        } catch (Exception e) {
            PluginLogger.error("An error occurred while converting YAML to MySQL.");
            PluginLogger.error(e.getMessage());
            return false;
        }

        return true;
    }
}
