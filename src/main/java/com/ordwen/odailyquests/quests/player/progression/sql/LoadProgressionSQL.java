package com.ordwen.odailyquests.quests.player.progression.sql;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Constructor.
     * @param sqlManager SQLManager instance.
     */
    public LoadProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /* init variables */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    long timestamp;

    int questId;
    int advancement;
    boolean isAchieved;

    PlayerQuests playerQuests;
    Progression progression;
    Quest quest;

    HashMap<Quest, Progression> quests = new HashMap<>();

    /* requests */
    private final String getTimestampQuery = "SELECT timestamp FROM progressions WHERE playerName = ";
    private final String getQuestProgressionQuery = "SELECT questId1, advancement1, isAchieved1 FROM progressions WHERE playerName = ";

    /**
     * Load player quests progression.
     * @param playerName name of the player.
     */
    public void loadProgression(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode) {

        try {
            Connection connection = sqlManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getTimestampQuery + playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            timestamp = resultSet.getLong("timestamp");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean timeToRedraw = false;

        /* check if last quests renewed day before */
        if (timestampConfigMode == 1) {
            Calendar oldCal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);
            currentCal.setTimeInMillis(System.currentTimeMillis());
            if (oldCal.get(Calendar.DATE) < currentCal.get(Calendar.DATE)) {
                timeToRedraw = true;
            }
        }

        /* check if last quests renewed is older than 24 hours */
        else if (timestampConfigMode == 2) {
            if (System.currentTimeMillis() - timestamp >= 86400000) {
                timeToRedraw = true;
            }
        }
        else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");

        /* renew quests */
        if (timeToRedraw) {
            activeQuests.remove(playerName);
            QuestsManager.selectRandomQuests(quests);
            if (timestampConfigMode == 1) {
                playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
            } else {
                playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
            }
            activeQuests.put(playerName, playerQuests);
            Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());

            logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been renewed.");
        }

        /* load non-achieved quests */
        else {

            try {
                Connection connection = sqlManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getQuestProgressionQuery + playerName);
                ResultSet resultSet = preparedStatement.executeQuery();

                questId = resultSet.getInt("questId1");
                advancement = resultSet.getInt("advancement1");
                isAchieved = resultSet.getBoolean("isAchieved1");

            } catch (SQLException e) {
                e.printStackTrace();
            }

            progression = new Progression(advancement, isAchieved);

            if (questsConfigMode == 1) {
                quest = LoadQuests.getGlobalQuests().get(questId);
            } else if (questsConfigMode == 2) {
                switch(Integer.parseInt(string)) {
                    case 1:
                        quest = LoadQuests.getEasyQuests().get(questId);
                        break;
                    case 2:
                        quest = LoadQuests.getMediumQuests().get(questId);
                        break;
                    case 3:
                        quest = LoadQuests.getHardQuests().get(questId);
                        break;
                }
            } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests. The selected mode is incorrect.");

            if (quest == null) {
                logger.info(ChatColor.RED + "An error occurred while loading " + ChatColor.GOLD + playerName + ChatColor.RED + "'s quests.");
                logger.info(ChatColor.RED + "Quest number " + string + " of player is null.");
                logger.info(ChatColor.RED + "Try to do the following command to reset the player's progress :");
                logger.info(ChatColor.GOLD + "/questsadmin reset " + playerName);
                logger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            }

            quests.put(quest, progression);

            // SCHEMA BD
            // STOCKER TOUTES LES QUETES ET UTILISER LES ID ? (ex: table Quests, table Progressions : name, timestamp, questId1, questId2, questId3)

            /*
            questId1
            advancement1
            isAchieved1

            questId2
            advancement2
            isAchieved2

            questId3
            advancement3
            isAchieved3
             */
        }

    }
}