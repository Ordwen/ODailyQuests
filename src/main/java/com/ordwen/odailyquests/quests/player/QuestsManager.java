package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.LoadProgression;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

public class QuestsManager implements Listener {

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("ODailyQuests");

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
        activeQuests.remove(event.getPlayer().getName());
    }

    /**
     * Select 3 random quests.
     * @param quests array of quests.
     */
    public static void selectRandomQuests(HashMap<Quest, Progression> quests) {
        Progression progression;

        if (configurationFiles.getConfigFile().getInt("mode") == 1) {
            ArrayList<Quest> globalQuests = LoadQuests.getGlobalQuests();

            for (int i = 0; i < 3; i++) {
                Quest quest;

                do {
                    quest = getRandomQuest(globalQuests);
                } while (quests.containsKey(quest));

                progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        }
        // second mode
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
