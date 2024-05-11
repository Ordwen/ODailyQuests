package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
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

import java.util.*;

public class QuestsManager implements Listener {

    /**
     * Getting instance of classes.
     */
    private final SQLManager sqlManager;
    private final YamlManager yamlManager;
    private final ODailyQuests plugin;

    /**
     * Class instance constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public QuestsManager(ODailyQuests oDailyQuests, boolean useSQL) {
        this.plugin = oDailyQuests;

        if (useSQL) {
            this.sqlManager = oDailyQuests.getSQLManager();
            this.yamlManager = null;
        } else {
            this.yamlManager = oDailyQuests.getYamlManager();
            this.sqlManager = null;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Debugger.addDebug("EVENT START");
        Debugger.addDebug("PlayerJoinEvent triggered.");

        final String playerName = event.getPlayer().getName();

        Debugger.addDebug("Player " + playerName + " joined the server.");

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            if (!questSystem.getActiveQuests().containsKey(playerName)) {
                Debugger.addDebug("Player " + playerName + " is not in the " + questSystem.getSystemName() + " array.");
                switch (Modes.getStorageMode()) {
                    case "YAML" ->
                            yamlManager.getLoadProgressionYAML().loadPlayerQuests(questSystem, playerName, questSystem.getActiveQuests());
                    case "MySQL", "H2" ->
                            sqlManager.getLoadProgressionSQL().loadProgression(questSystem, playerName, questSystem.getActiveQuests());
                    default ->
                            PluginLogger.error("Impossible to load player " + questSystem.getSystemName() + " quests : the selected storage mode is incorrect !");
                }
            } else {
                Debugger.addDebug("Player " + playerName + " is already in the " + questSystem.getSystemName() + " array.");
                PluginLogger.warn(playerName + " detected into the " + questSystem.getSystemName() + " array. This is not supposed to happen!");
                PluginLogger.warn("If the player can't make his quests progress, please contact the plugin developer.");
            }
        });

        Debugger.addDebug("[EVENT END]");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Debugger.addDebug("[EVENT START]");
        Debugger.addDebug("PlayerQuitEvent triggered.");

        String playerName = event.getPlayer().getName();

        Debugger.addDebug("Player " + playerName + " left the server.");

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            final PlayerQuests playerQuests = questSystem.getActiveQuests().get(playerName);

            if (playerQuests == null) {
                Debugger.addDebug("Player " + playerName + " not found in the array.");


                PluginLogger.warn("Player quests not found for player " + playerName);
                return;
            }

            switch (Modes.getStorageMode()) {
                case "YAML" ->
                        yamlManager.getSaveProgressionYAML().saveProgression(questSystem, playerName, playerQuests, !plugin.isServerStopping());
                case "MySQL", "H2" ->
                        sqlManager.getSaveProgressionSQL().saveProgression(questSystem, playerName, playerQuests, !plugin.isServerStopping());
                default ->
                        PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
            }

            questSystem.getActiveQuests().remove(playerName);
        });

        Debugger.addDebug("Player " + playerName + " removed from the array.");
        Debugger.addDebug("[EVENT END]");

    }

    /**
     * Select random quests.
     */
    public static LinkedHashMap<AbstractQuest, Progression> selectRandomQuests(QuestSystem questSystem) {

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        if (questSystem.getQuestsMode() == 1) {
            ArrayList<AbstractQuest> globalQuests = questSystem.getGlobalCategory();

            for (int i = 0; i < questSystem.getGlobalQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), globalQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else if (questSystem.getQuestsMode() == 2) {

            final ArrayList<AbstractQuest> easyQuests = questSystem.getEasyCategory();
            final ArrayList<AbstractQuest> mediumQuests = questSystem.getMediumCategory();
            final ArrayList<AbstractQuest> hardQuests = questSystem.getHardCategory();

            for (int i = 0; i < questSystem.getEasyQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), easyQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < questSystem.getMediumQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), mediumQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < questSystem.getHardQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), hardQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to select quests for player. The " + questSystem.getSystemName() + " selected mode is incorrect.");

        return quests;
    }

    /**
     * Get a random quest that is not already in the player's quests.
     *
     * @param currentQuests   the player's current quests
     * @param availableQuests the available quests
     * @return a quest
     */
    public static AbstractQuest getRandomQuestForPlayer(Set<AbstractQuest> currentQuests, List<AbstractQuest> availableQuests) {
        AbstractQuest quest;
        do {
            quest = getRandomQuestInCategory(availableQuests);
        } while (currentQuests.contains(quest));
        return quest;
    }

    /**
     * Get random quest.
     *
     * @param quests array of quests
     * @return a quest.
     */
    public static AbstractQuest getRandomQuestInCategory(List<AbstractQuest> quests) {
        int questNumber = new Random().nextInt(quests.size());
        return quests.get(questNumber);
    }
}
