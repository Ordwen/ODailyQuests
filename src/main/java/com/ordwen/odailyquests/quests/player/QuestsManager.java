package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
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
    private final ODailyQuests plugin;

    /**
     * Class instance constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public QuestsManager(ODailyQuests oDailyQuests) {
        this.plugin = oDailyQuests;
    }

    private static final Map<String, PlayerQuests> activeQuests = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Debugger.write("EVENT START");
        Debugger.write("PlayerJoinEvent triggered.");

        final String playerName = event.getPlayer().getName();

        Debugger.write("Player " + playerName + " joined the server.");

        if (!activeQuests.containsKey(playerName)) {
            Debugger.write("Player " + playerName + " is not in the array.");
            plugin.getDatabaseManager().loadQuestsForPlayer(playerName);
        } else {

            Debugger.write("Player " + playerName + " is already in the array.");

            PluginLogger.warn(playerName + " detected into the array. This is not supposed to happen!");
            PluginLogger.warn("If the player can't make his quests progress, please contact the plugin developer.");
        }

        Debugger.write("[EVENT END]");

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Debugger.write("[EVENT START]");
        Debugger.write("PlayerQuitEvent triggered.");

        String playerName = event.getPlayer().getName();

        Debugger.write("Player " + playerName + " left the server.");

        final PlayerQuests playerQuests = activeQuests.get(playerName);

        if (playerQuests == null) {
            Debugger.write("Player " + playerName + " not found in the array.");


            PluginLogger.warn("Player quests not found for player " + playerName);
            return;
        }

        plugin.getDatabaseManager().saveProgressionForPlayer(playerName, playerQuests);
        activeQuests.remove(playerName);

        Debugger.write("Player " + playerName + " removed from the array.");
        Debugger.write("[EVENT END]");

    }

    /**
     * Select random quests.
     */
    public static Map<AbstractQuest, Progression> selectRandomQuests() {

        final Map<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        if (Modes.getQuestsMode() == 1) {
            ArrayList<AbstractQuest> globalQuests = CategoriesLoader.getGlobalQuests();

            for (int i = 0; i < QuestsAmount.getQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), globalQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else if (Modes.getQuestsMode() == 2) {

            final ArrayList<AbstractQuest> easyQuests = CategoriesLoader.getEasyQuests();
            final ArrayList<AbstractQuest> mediumQuests = CategoriesLoader.getMediumQuests();
            final ArrayList<AbstractQuest> hardQuests = CategoriesLoader.getHardQuests();

            for (int i = 0; i < QuestsAmount.getEasyQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), easyQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < QuestsAmount.getMediumQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), mediumQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }

            for (int i = 0; i < QuestsAmount.getHardQuestsAmount(); i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), hardQuests);
                final Progression progression = new Progression(0, false);
                quests.put(quest, progression);
            }
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to select quests for player. The selected mode is incorrect.");

        return quests;
    }

    /**
     * Get a random quest that is not already in the player's quests.
     * @param currentQuests the player's current quests
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

    /**
     * Get active quests map.
     *
     * @return quests map.
     */
    public static Map<String, PlayerQuests> getActiveQuests() {
        return activeQuests;
    }

}
