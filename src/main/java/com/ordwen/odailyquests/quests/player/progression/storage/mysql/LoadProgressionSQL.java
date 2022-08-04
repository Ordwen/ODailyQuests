package com.ordwen.odailyquests.quests.player.progression.storage.mysql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoadProgressionSQL {

    /* instance of SQLManager */
    private final MySQLManager mySqlManager;

    /**
     * Constructor.
     *
     * @param mySqlManager instance of MySQLManager.
     */
    public LoadProgressionSQL(MySQLManager mySqlManager) {
        this.mySqlManager = mySqlManager;
    }

    /**
     * Load player quests progression.
     *
     * @param playerName name of the player.
     */
    public void loadProgression(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode, int temporalityMode) {

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try {
                Connection connection = mySqlManager.getConnection();
                String getTimestampQuery = "SELECT PLAYERTIMESTAMP,ACHIEVEDQUESTS,TOTALACHIEVEDQUESTS FROM PLAYER WHERE PLAYERNAME = '" + playerName + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(getTimestampQuery);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    hasStoredData = true;
                    timestamp = resultSet.getLong("PLAYERTIMESTAMP");
                    achievedQuests = resultSet.getInt("ACHIEVEDQUESTS");
                    totalAchievedQuests = resultSet.getInt("TOTALACHIEVEDQUESTS");
                }

                connection.close();
                resultSet.close();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (hasStoredData) {
                if (Utils.checkTimestamp(timestampConfigMode, temporalityMode, timestamp)) {
                    Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
                }
                else {
                    loadPlayerQuests(playerName, questsConfigMode, quests);

                    PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    activeQuests.put(playerName, playerQuests);

                    PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been loaded.");

                    final String msg = QuestsMessages.QUESTS_IN_PROGRESS.toString();
                    if (msg != null) Bukkit.getPlayer(playerName).sendMessage(msg);
                }
            } else {
                Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
            }
        });
    }

    /**
     * Load player quests.
     *
     * @param playerName player.
     * @param questsConfigMode configuration mode.
     * @param quests list of player quests.
     */
    private void loadPlayerQuests(String playerName, int questsConfigMode, LinkedHashMap<AbstractQuest, Progression> quests) {

        try {
            Connection connection = mySqlManager.getConnection();
            String getQuestProgressionQuery = "SELECT * FROM PROGRESSION WHERE PLAYERNAME = '" + playerName + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(getQuestProgressionQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            int id = 1;

            resultSet.next();
            do {
                int questIndex = resultSet.getInt("QUESTINDEX");
                int advancement = resultSet.getInt("ADVANCEMENT");
                boolean isAchieved = resultSet.getBoolean("ISACHIEVED");

                Progression progression = new Progression(advancement, isAchieved);
                AbstractQuest quest = Utils.findQuest(playerName, questsConfigMode, questIndex, id);

                quests.put(quest, progression);

                id++;
            } while (resultSet.next());

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}