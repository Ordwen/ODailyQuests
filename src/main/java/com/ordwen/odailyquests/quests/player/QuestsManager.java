package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class QuestsManager implements Listener {

    private static final String PLAYER = "Player ";
    private static final Random random = new Random();

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

        Debugger.write("[EVENT START]");
        Debugger.write("PlayerJoinEvent triggered.");

        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final UUID uuid = player.getUniqueId();

        Debugger.write(PLAYER + playerName + " joined the server.");
        Debugger.write("Player UUID is " + uuid);

        if (!activeQuests.containsKey(playerName)) {
            Debugger.write(PLAYER + playerName + " is not in the array.");
            plugin.getDatabaseManager().loadQuestsForPlayer(playerName);
        } else {

            Debugger.write(PLAYER + playerName + " is already in the array.");

            PluginLogger.warn(playerName + " detected into the array. This is not supposed to happen!");
            PluginLogger.warn("If the player can't make his quests progress, please contact the plugin developer.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Debugger.write("[EVENT START]");
        Debugger.write("PlayerQuitEvent triggered.");

        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final String playerUUID = player.getUniqueId().toString();

        Debugger.write(PLAYER + playerName + " left the server.");

        final PlayerQuests playerQuests = activeQuests.get(playerName);

        if (playerQuests == null) {
            Debugger.write(PLAYER + playerName + " not found in the array.");
            PluginLogger.warn("Player quests not found for player " + playerName);
            return;
        }

        plugin.getDatabaseManager().saveProgressionForPlayer(playerName, playerUUID, playerQuests);
        activeQuests.remove(playerName);

        Debugger.write(PLAYER + playerName + " removed from the array.");
    }

    /**
     * Select random quests for player, based on the selected mode and the amount of quests.
     *
     * @return quests map.
     */
    public static Map<AbstractQuest, Progression> selectRandomQuests(Player player) {
        final Map<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        final Map<String, Category> categoryMap = CategoriesLoader.getAllCategories();

        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            final String categoryName = entry.getKey();
            final Category category = entry.getValue();

            int requiredAmount = QuestsPerCategory.getAmountForCategory(categoryName);

            for (int i = 0; i < requiredAmount; i++) {
                final AbstractQuest quest = getRandomQuestForPlayer(quests.keySet(), category, player);
                if (quest == null) {
                    PluginLogger.warn("Not enough quests available to assign to " + player.getName() + " in category " + categoryName + ".");
                    break;
                }

                final int questRequiredAmount = getDynamicRequiredAmount(quest.getRequiredAmountRaw());
                final Progression progression = new Progression(questRequiredAmount, 0, false);

                if (quest.isRandomRequired()) {
                    final int selected = getRandomIndexFrom(quest);
                    progression.setSelectedRequiredIndex(selected);
                }

                quests.put(quest, progression);
            }
        }

        return quests;
    }

    public static int getDynamicRequiredAmount(String requiredAmountRaw) {
        if (requiredAmountRaw.contains("-")) {
            String[] parts = requiredAmountRaw.split("-");
            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());
            if (min < 1) min = 1;

            return random.nextInt(min, max + 1);
        }

        int amount = Integer.parseInt(requiredAmountRaw);
        return Math.max(amount, 1);
    }

    private static int getRandomIndexFrom(AbstractQuest quest) {
        if (quest instanceof EntityQuest eq) {
            return random.nextInt(eq.getRequiredEntities().size());
        }

        if (quest instanceof ItemQuest iq) {
            return random.nextInt(iq.getRequiredItems().size());
        }

        return 0;
    }

    /**
     * Get a random quest that is not already in the player's quests.
     *
     * @param currentQuests   the player's current quests.
     * @param availableQuests the available quests.
     * @return a random quest or null if none found.
     */
    public static AbstractQuest getRandomQuestForPlayer(Set<AbstractQuest> currentQuests, List<AbstractQuest> availableQuests, Player player) {
        final List<AbstractQuest> filteredQuests = new ArrayList<>();

        for (AbstractQuest quest : availableQuests) {
            if (hasAllPermissions(player, quest.getRequiredPermissions()) && !currentQuests.contains(quest)) {
                filteredQuests.add(quest);
            }
        }

        if (filteredQuests.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(filteredQuests.size());
        return filteredQuests.get(randomIndex);
    }

    /**
     * Check if player has all required permissions.
     *
     * @param player      the player to check.
     * @param permissions list of permissions.
     * @return true if player has all permissions, false otherwise.
     */
    private static boolean hasAllPermissions(Player player, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }

        for (String permission : permissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
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
