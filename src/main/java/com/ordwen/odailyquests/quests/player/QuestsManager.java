package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.MySQLManager;
import com.ordwen.odailyquests.files.ConfigurationFiles;
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
    private final MySQLManager mySqlManager;
    private final YamlManager  yamlManager;

    /**
     * Class instance constructor.
     * @param oDailyQuests main class instance.
     */
    public QuestsManager(ODailyQuests oDailyQuests, boolean useMySQL) {
        configurationFiles = oDailyQuests.getConfigurationFiles();

        if (useMySQL) {
            this.mySqlManager = oDailyQuests.getMySqlManager();
            this.yamlManager = null;
        } else {
            this.yamlManager = oDailyQuests.getYamlManager();
            this.mySqlManager = null;
        }
    }

    private static final HashMap<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!activeQuests.containsKey(playerName)) {
            switch (configurationFiles.getConfigFile().getString("storage_mode")) {
                case "YAML" -> yamlManager.getLoadProgressionYAML().loadPlayerQuests(playerName, activeQuests,
                        configurationFiles.getConfigFile().getInt("quests_mode"),
                        configurationFiles.getConfigFile().getInt("timestamp_mode"),
                        configurationFiles.getConfigFile().getInt("temporality_mode"));
                case "MySQL" -> mySqlManager.getLoadProgressionSQL().loadProgression(playerName, activeQuests,
                        configurationFiles.getConfigFile().getInt("quests_mode"),
                        configurationFiles.getConfigFile().getInt("timestamp_mode"),
                        configurationFiles.getConfigFile().getInt("temporality_mode"));
                default -> PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
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
            case "YAML" -> yamlManager.getSaveProgressionYAML().saveProgression(playerName, activeQuests);
            case "MySQL" -> mySqlManager.getSaveProgressionSQL().saveProgression(playerName, activeQuests, true);
            default -> PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
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
                Quest quest = switch (i) {
                    case 0 -> getRandomQuest(LoadQuests.getEasyQuests());
                    case 1 -> getRandomQuest(LoadQuests.getMediumQuests());
                    case 2 -> getRandomQuest(LoadQuests.getHardQuests());
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
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
