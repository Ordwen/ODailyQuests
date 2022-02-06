package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    private ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public QuestsManager(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static HashMap<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (activeQuests.containsKey(playerName)) {
            logger.info(ChatColor.GREEN + playerName + ChatColor.YELLOW + " detected in the array.");

            PlayerQuests playerQuests = activeQuests.get(playerName);
            if (System.currentTimeMillis() - playerQuests.timestamp >= 86400000) {
                // renew quests
            }
        }
        else {
            HashMap<Quest, Progression> quests = new HashMap<>();
            selectRandomQuests(quests);
            PlayerQuests playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
            activeQuests.put(playerName, playerQuests);

            logger.info(ChatColor.GREEN + playerName + ChatColor.YELLOW + " insert into the array.");
        }
    }

    /**
     * Select 3 random quests.
     * @param quests array of quests.
     */
    public void selectRandomQuests(HashMap<Quest, Progression> quests) {
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
        // second mode
    }

    /**
     * Get random quest.
     * @param quests array of quests
     * @return a quest.
     */
    public Quest getRandomQuest(ArrayList<Quest> quests) {
        int questNumber = new Random().nextInt(quests.size());
        return quests.get(questNumber);
    }

    /**
     * Get active quests array.
     * @return
     */
    public static HashMap<String, PlayerQuests> getActiveQuests() {
        return activeQuests;
    }
}
