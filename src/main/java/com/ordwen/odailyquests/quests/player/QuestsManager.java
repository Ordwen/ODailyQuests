package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.LoadProgression;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.SaveProgression;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestsManager implements Listener {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Getting instance of classes.
     */
    private static ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public QuestsManager(ConfigurationFiles configurationFiles) {
        QuestsManager.configurationFiles = configurationFiles;
    }

    private static final HashMap<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!activeQuests.containsKey(playerName)) {
            LoadProgression.loadPlayerQuests(playerName, activeQuests, configurationFiles.getConfigFile().getInt("mode"));
        } else {
            logger.info(ChatColor.GOLD + playerName + ChatColor.RED + " detected into the array.");
            logger.info(ChatColor.RED + "THAT IS NOT NORMAL.");
            logger.info(ChatColor.RED + "The player quests will be never renewed.");
            logger.info(ChatColor.RED + "Please inform developer.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        SaveProgression.saveProgression(playerName, activeQuests);
        activeQuests.remove(playerName);
    }

    /**
     * Select 3 random quests.
     * @param quests array of quests.
     */
    public static void selectRandomQuests(HashMap<Quest, Progression> quests) {
        Progression progression = new Progression(0, false);
        if (configurationFiles.getConfigFile().getInt("mode") == 1) {
            ArrayList<Quest> globalQuests = LoadQuests.getGlobalQuests();
            for (int i = 0; i < 3; i++) {
                Quest quest;
                do {
                    quest = getRandomQuest(globalQuests);
                } while (quests.containsKey(quest));
                quests.put(quest, progression);
            }
        }
        else if (configurationFiles.getConfigFile().getInt("mode") == 2) {
            for (int i = 0; i < 3; i++) {
                Quest quest;
                switch (i) {
                    case 0:
                        quest = getRandomQuest(LoadQuests.getEasyQuests());
                        break;
                    case 1:
                        quest = getRandomQuest(LoadQuests.getMediumQuests());
                        break;
                    case 2:
                        quest = getRandomQuest(LoadQuests.getHardQuests());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + i);
                }
                quests.put(quest, progression);
            }
        } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to select quests for player. The selected mode is incorrect.");
    }

    /**
     * Get random quest.
     * @param quests array of quests
     * @return a quest.
     */
    public static Quest getRandomQuest(ArrayList<Quest> quests) {
        int questNumber = new Random().nextInt(quests.size());
        return quests.get(questNumber);
    }

    /**
     * Get active quests map.
     * @return quests map.
     */
    public static HashMap<String, PlayerQuests> getActiveQuests() {
        return activeQuests;
    }
}
