package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.SaveProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.LoadProgressionYAML;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.SaveProgressionYAML;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class QuestsManager implements Listener {

    /**
     * Getting instance of classes.
     */
    private static ConfigurationFiles configurationFiles;
    private final LoadProgressionSQL loadProgressionSQL;
    private final SaveProgressionSQL saveProgressionSQL;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public QuestsManager(ConfigurationFiles configurationFiles,
                         LoadProgressionSQL loadProgressionSQL,
                         SaveProgressionSQL saveProgressionSQL) {
        QuestsManager.configurationFiles = configurationFiles;
        this.loadProgressionSQL = loadProgressionSQL;
        this.saveProgressionSQL = saveProgressionSQL;
    }

    private static final HashMap<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!activeQuests.containsKey(playerName)) {
            switch (configurationFiles.getConfigFile().getString("storage_mode")) {
                case "YAML":
                    LoadProgressionYAML.loadPlayerQuests(playerName, activeQuests,
                            configurationFiles.getConfigFile().getInt("quests_mode"),
                            configurationFiles.getConfigFile().getInt("timestamp_mode"),
                            configurationFiles.getConfigFile().getInt("temporality_mode"));
                    break;
                case "MySQL":
                    loadProgressionSQL.loadProgression(playerName, activeQuests,
                            configurationFiles.getConfigFile().getInt("quests_mode"),
                            configurationFiles.getConfigFile().getInt("timestamp_mode"),
                            configurationFiles.getConfigFile().getInt("temporality_mode"));
                    break;
                default:
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
                    break;
            }
        } else {
            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.RED + " detected into the array.");
            PluginLogger.info(ChatColor.RED + "THAT IS NOT NORMAL.");
            PluginLogger.info(ChatColor.RED + "The player quests will be never renewed.");
            PluginLogger.info(ChatColor.RED + "Please inform developer.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        switch (configurationFiles.getConfigFile().getString("storage_mode")) {
            case "YAML":
                SaveProgressionYAML.saveProgression(playerName, activeQuests);
                break;
            case "MySQL":
                saveProgressionSQL.saveProgression(playerName, activeQuests);
                break;
            default:
                PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
                break;
        }
        activeQuests.remove(playerName);
    }

    /**
     * Select 3 random quests.
     *
     * @param quests array of quests.
     */
    public static void selectRandomQuests(HashMap<Quest, Progression> quests) {
        if (configurationFiles.getConfigFile().getInt("quests_mode") == 1) {
            ArrayList<Quest> globalQuests = LoadQuests.getGlobalQuests();
            for (int i = 0; i < 3; i++) {
                Quest quest;
                do {
                    quest = getRandomQuest(globalQuests);
                } while (quests.containsKey(quest));
                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else if (configurationFiles.getConfigFile().getInt("quests_mode") == 2) {
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
                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to select quests for player. The selected mode is incorrect.");
    }

    /**
     * Get random quest.
     *
     * @param quests array of quests
     * @return a quest.
     */
    public static Quest getRandomQuest(ArrayList<Quest> quests) {
        int questNumber = new Random().nextInt(quests.size());
        return quests.get(questNumber);
    }

    /**
     * Get active quests map.
     *
     * @return quests map.
     */
    public static HashMap<String, PlayerQuests> getActiveQuests() {
        return activeQuests;
    }

}
