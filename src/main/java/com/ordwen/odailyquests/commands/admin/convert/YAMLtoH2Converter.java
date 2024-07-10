package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.h2.H2Manager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class YAMLtoH2Converter extends SQLConverter {

    public boolean convert(ODailyQuests oDailyQuests) {

        try {
            Bukkit.getScheduler().runTaskAsynchronously(oDailyQuests, () -> {

                final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
                final SQLManager sqlManager = new H2Manager(ODailyQuests.INSTANCE);

                convertData(progressionFile, sqlManager);
            });
        } catch (Exception e) {
            PluginLogger.error("An error occurred while converting YAML to H2.");
            PluginLogger.error(e.getMessage());
            return false;
        }

        return true;
    }
}
