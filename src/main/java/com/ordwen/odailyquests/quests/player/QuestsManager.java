package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class QuestsManager implements Listener {

    /**
     * Getting instance of classes.
     */
    private static ConfigurationFiles configurationFiles;
    private final SQLManager sqlManager;
    private final YamlManager  yamlManager;

    /**
     * Class instance constructor.
     * @param oDailyQuests main class instance.
     */
    public QuestsManager(ODailyQuests oDailyQuests, boolean useSQL) {
        configurationFiles = oDailyQuests.getConfigurationFiles();

        if (useSQL) {
            this.sqlManager = oDailyQuests.getSQLManager();
            this.yamlManager = null;
        } else {
            this.yamlManager = oDailyQuests.getYamlManager();
            this.sqlManager = null;
        }
    }

    private static final HashMap<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        if (!activeQuests.containsKey(playerName)) {
            switch (configurationFiles.getConfigFile().getString("storage_mode")) {
                case "YAML" -> yamlManager.getLoadProgressionYAML().loadPlayerQuests(playerName, activeQuests,
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
                case "MySQL", "H2" -> sqlManager.getLoadProgressionSQL().loadProgression(playerName, activeQuests,
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
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
        final PlayerQuests playerQuests = activeQuests.get(playerName);

        if (playerQuests == null) {
            PluginLogger.warn("Player quests not found for player " + playerName);
            return;
        }

        switch (configurationFiles.getConfigFile().getString("storage_mode")) {
            case "YAML" -> yamlManager.getSaveProgressionYAML().saveProgression(playerName, playerQuests);
            case "MySQL", "H2" -> sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerQuests, true);
            default -> PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
        }
        activeQuests.remove(playerName);
    }

    /**
     * Select random quests.
     */
    public static LinkedHashMap<AbstractQuest, Progression> selectRandomQuests() {

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        if (Modes.getQuestsMode() == 1) {
            ArrayList<AbstractQuest> globalQuests = LoadQuests.getGlobalQuests();

            for (int i = 0; i < QuestsAmount.getQuestsAmount(); i++) {
                AbstractQuest quest;
                do {
                    quest = getRandomQuest(globalQuests);
                } while (quests.containsKey(quest));

                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else if (Modes.getQuestsMode() == 2) {

            final ArrayList<AbstractQuest> easyQuests = LoadQuests.getEasyQuests();
            final ArrayList<AbstractQuest> mediumQuests = LoadQuests.getMediumQuests();
            final ArrayList<AbstractQuest> hardQuests = LoadQuests.getHardQuests();

            for (int i = 0; i < QuestsAmount.getEasyQuestsAmount(); i++) {
                AbstractQuest quest;
                do {
                    quest = getRandomQuest(easyQuests);
                } while (quests.containsKey(quest));

                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < QuestsAmount.getMediumQuestsAmount(); i++) {
                AbstractQuest quest;
                do {
                    quest = getRandomQuest(mediumQuests);
                } while (quests.containsKey(quest));

                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < QuestsAmount.getHardQuestsAmount(); i++) {
                AbstractQuest quest;
                do {
                    quest = getRandomQuest(hardQuests);
                } while (quests.containsKey(quest));

                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else PluginLogger.error(ChatColor.RED + "Impossible to select quests for player. The selected mode is incorrect.");

        return quests;
    }

    /**
     * Get random quest.
     *
     * @param quests array of quests
     * @return a quest.
     */
    public static AbstractQuest getRandomQuest(ArrayList<AbstractQuest> quests) {
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
