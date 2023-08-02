package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
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
    private final SQLManager sqlManager;
    private final YamlManager yamlManager;

    /**
     * Class instance constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public QuestsManager(ODailyQuests oDailyQuests, boolean useSQL) {
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

        Debugger.addDebug("EVENT START");
        Debugger.addDebug("PlayerJoinEvent triggered.");

        final String playerName = event.getPlayer().getName();

        Debugger.addDebug("Player " + playerName + " joined the server.");

        if (!activeQuests.containsKey(playerName)) {

            Debugger.addDebug("Player " + playerName + " is not in the array.");


            switch (Modes.getStorageMode()) {
                case "YAML" -> yamlManager.getLoadProgressionYAML().loadPlayerQuests(playerName, activeQuests,
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
                case "MySQL", "H2" -> sqlManager.getLoadProgressionSQL().loadProgression(playerName, activeQuests,
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
                default ->
                        PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
            }
        } else {

            Debugger.addDebug("Player " + playerName + " is already in the array.");

            PluginLogger.error(playerName + " detected into the array.");
            PluginLogger.error("THAT IS NOT NORMAL.");
            PluginLogger.error("The player quests will be never renewed.");
            PluginLogger.error("Please inform developer.");
        }

        Debugger.addDebug("[EVENT END]");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Debugger.addDebug("[EVENT START]");
        Debugger.addDebug("PlayerQuitEvent triggered.");

        String playerName = event.getPlayer().getName();

        Debugger.addDebug("Player " + playerName + " left the server.");

        final PlayerQuests playerQuests = activeQuests.get(playerName);

        if (playerQuests == null) {
            Debugger.addDebug("Player " + playerName + " not found in the array.");


            PluginLogger.warn("Player quests not found for player " + playerName);
            return;
        }

        switch (Modes.getStorageMode()) {
            case "YAML" -> yamlManager.getSaveProgressionYAML().saveProgression(playerName, playerQuests, true);
            case "MySQL", "H2" -> sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerQuests, true);
            default ->
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
        }

        activeQuests.remove(playerName);

        Debugger.addDebug("Player " + playerName + " removed from the array.");
        Debugger.addDebug("[EVENT END]");

    }

    /**
     * Select random quests.
     */
    public static LinkedHashMap<AbstractQuest, Progression> selectRandomQuests() {

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        if (Modes.getQuestsMode() == 1) {
            ArrayList<AbstractQuest> globalQuests = CategoriesLoader.getGlobalQuests();

            for (int i = 0; i < QuestsAmount.getQuestsAmount(); i++) {
                AbstractQuest quest;
                do {
                    quest = getRandomQuest(globalQuests);
                } while (quests.containsKey(quest));

                Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else if (Modes.getQuestsMode() == 2) {

            final ArrayList<AbstractQuest> easyQuests = CategoriesLoader.getEasyQuests();
            final ArrayList<AbstractQuest> mediumQuests = CategoriesLoader.getMediumQuests();
            final ArrayList<AbstractQuest> hardQuests = CategoriesLoader.getHardQuests();

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
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to select quests for player. The selected mode is incorrect.");

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
