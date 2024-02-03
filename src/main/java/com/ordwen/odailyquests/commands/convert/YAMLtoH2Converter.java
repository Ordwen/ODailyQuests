package com.ordwen.odailyquests.commands.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.h2.H2Manager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;

public class YAMLtoH2Converter {

    public boolean convert(ODailyQuests oDailyQuests) {

        try {
            Bukkit.getScheduler().runTaskAsynchronously(oDailyQuests, () -> {

                final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
                final SQLManager sqlManager = new H2Manager(ODailyQuests.INSTANCE);

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
                        AbstractQuest quest = Utils.findQuest(playerName, questIndex, Integer.parseInt(string));

                        quests.put(quest, progression);
                    }

                    playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerQuests, true);
                }

            });
        } catch (Exception e) {
            PluginLogger.error("An error occurred while converting YAML to H2.");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
