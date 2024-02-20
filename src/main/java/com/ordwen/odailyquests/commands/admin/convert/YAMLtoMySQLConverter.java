package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql.MySQLManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;

public class YAMLtoMySQLConverter {

    public boolean convert(ODailyQuests oDailyQuests) {

        try {
            Bukkit.getScheduler().runTaskAsynchronously(oDailyQuests, () -> {

                final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
                final SQLManager sqlManager = new MySQLManager(ODailyQuests.INSTANCE);

                /* init variables */
                long timestamp;
                int achievedQuests;
                int totalAchievedQuests;
                PlayerQuests playerQuests;

                final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

                for (String playerName : progressionFile.getKeys(false)) {
                    timestamp = progressionFile.getConfigurationSection(playerName).getLong(".timestamp");
                    achievedQuests = progressionFile.getConfigurationSection(playerName).getInt(".achievedQuests");
                    totalAchievedQuests = progressionFile.getConfigurationSection(playerName).getInt(".totalAchievedQuests");

                    for (String string : progressionFile.getConfigurationSection(playerName + ".quests").getKeys(false)) {
                        int questIndex = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                        int advancement = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                        boolean isAchieved = progressionFile.getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                        Progression progression = new Progression(advancement, isAchieved);
                        AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, questIndex, Integer.parseInt(string));

                        quests.put(quest, progression);
                    }

                    playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerQuests, true);
                }

            });
        } catch (Exception e) {
            PluginLogger.error("An error occurred while converting YAML to MySQL.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
