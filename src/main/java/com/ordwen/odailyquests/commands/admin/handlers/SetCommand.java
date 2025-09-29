package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Administrative command allowing an admin to set a specific quest
 * in a given slot for a target player.
 * <p>
 * Usage: <pre>/dqa set &lt;player&gt; &lt;slot&gt; &lt;category&gt; &lt;quest_id&gt;</pre>
 * <ul>
 *     <li><b>player</b>: target player name</li>
 *     <li><b>slot</b>: quest slot index (1-based)</li>
 *     <li><b>category</b>: category name (must exist in CategoriesLoader)</li>
 *     <li><b>quest_id</b>: quest file index (unique ID within the category)</li>
 * </ul>
 * This command uses {@link PlayerQuests#setQuestAtIndex(int, AbstractQuest)} to replace
 * an existing quest by another one, while respecting the rules:
 * <ul>
 *     <li>Prevents duplicates if the quest is already present</li>
 *     <li>Rejects invalid indices or unknown categories/quests</li>
 *     <li>Notifies both the command sender and the target player</li>
 * </ul>
 */
public class SetCommand extends AdminCommandBase {

    private static final String SET = "set";
    private static final String SLOT = "%slot%";
    private static final String QUEST = "%quest%";
    private static final String QUEST_ID = "%quest_id%";

    /**
     * @return the main command literal, i.e. "set"
     */
    @Override
    public String getName() {
        return SET;
    }

    /**
     * @return the permission node required to execute this command
     */
    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.get();
    }


    /**
     * Executes the admin command.
     * <p>
     * Steps:
     * <ol>
     *     <li>Validate arguments count (requires 5)</li>
     *     <li>Resolve target player and load its {@link PlayerQuests}</li>
     *     <li>Check slot index bounds</li>
     *     <li>Check that category and quest ID exist</li>
     *     <li>Attempt quest replacement and handle {@link PlayerQuests.ReplaceResult}</li>
     *     <li>Send feedback messages to sender and target</li>
     * </ol>
     *
     * @param sender the executor (admin)
     * @param args   command arguments
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 5) {
            help(sender);
            return;
        }

        final Player target = getTargetPlayer(sender, args[1]);
        if (target == null) {
            return;
        }

        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(target.getName());
        if (playerQuests == null) {
            final String msg = QuestsMessages.PLAYER_QUESTS_NOT_LOADED.toString();
            if (msg != null) sender.sendMessage(msg);
            return;
        }

        final int slotIndex = parseQuestIndex(sender, args[2]);
        if (slotIndex == -1) {
            return;
        }

        if (slotIndex < 1 || slotIndex > playerQuests.getQuests().size()) {
            invalidQuest(sender);
            return;
        }

        final String categoryName = args[3];
        if (!CategoriesLoader.hasCategory(categoryName)) {
            invalidCategory(sender);
            return;
        }

        final String questId = args[4];

        final Category category = CategoriesLoader.getCategoryByName(categoryName);
        final Optional<AbstractQuest> questOptional = findQuest(category, questId);
        if (questOptional.isEmpty()) {
            invalidQuestId(sender);
            return;
        }

        final AbstractQuest questToAssign = questOptional.get();

        final PlayerQuests.ReplaceResult replaceResult = playerQuests.setQuestAtIndex(slotIndex - 1, questToAssign);

        switch (replaceResult) {
            case SUCCESS -> {
                confirmationToSender(sender, target.getName(), questToAssign, questId, slotIndex, categoryName);
                confirmationToTarget(target, questToAssign, questId, slotIndex, categoryName);
            }
            case INVALID_INDEX -> invalidQuest(sender);
            case ALREADY_PRESENT -> {
                final String msg = QuestsMessages.QUEST_ALREADY_ASSIGNED.toString();
                if (msg != null) sender.sendMessage(msg.replace(QUEST, questToAssign.getQuestName()));
            }
        }
    }

    /**
     * Finds a quest within a category by its quest file index.
     *
     * @param category the category to search
     * @param questId  the quest ID to find
     * @return an Optional containing the quest if found, or empty otherwise
     */
    private Optional<AbstractQuest> findQuest(Category category, String questId) {
        if (category == null) {
            return Optional.empty();
        }

        return category.stream()
                .filter(quest -> quest.getFileIndex().equals(questId))
                .findFirst();
    }

    /**
     * Sends a confirmation message to the command sender
     * once the quest has been successfully replaced.
     */
    private void confirmationToSender(CommandSender sender, String targetName, AbstractQuest quest,
                                      String questId, int slotIndex, String categoryName) {
        final String msg = QuestsMessages.QUEST_SET_ADMIN.toString();
        if (msg != null) {
            sender.sendMessage(msg
                    .replace(TARGET, targetName)
                    .replace(SLOT, String.valueOf(slotIndex))
                    .replace(CATEGORY, categoryName)
                    .replace(QUEST_ID, String.valueOf(questId))
                    .replace(QUEST, quest.getQuestName()));
        }
    }

    /**
     * Sends a confirmation message to the target player
     * once their quest has been successfully replaced.
     */
    private void confirmationToTarget(Player target, AbstractQuest quest,
                                      String questId, int slotIndex, String categoryName) {
        final String msg = QuestsMessages.QUEST_SET_TARGET.toString();
        if (msg != null) {
            target.sendMessage(msg
                    .replace(SLOT, String.valueOf(slotIndex))
                    .replace(CATEGORY, categoryName)
                    .replace(QUEST_ID, String.valueOf(questId))
                    .replace(QUEST, quest.getQuestName()));
        }
    }

    /**
     * Provides tab-completion for the /dqa set command.
     * <ul>
     *     <li>Arg 2: list of online player names</li>
     *     <li>Arg 3: slot numbers available for the target player</li>
     *     <li>Arg 4: category names</li>
     *     <li>Arg 5: quest IDs from the chosen category</li>
     * </ul>
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> getOnlinePlayerNames();
            case 3 -> getQuestNumbers(args[1]);
            case 4 -> getCategoryNames();
            case 5 -> getQuestIds(args[3]);
            default -> args.length >= 6 ? Collections.emptyList() : null;
        };
    }

    /**
     * @return the names of all online players
     */
    private List<String> getOnlinePlayerNames() {
        final List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            names.add(p.getName());
        }
        return names;
    }

    /**
     * Builds a list of quest slot numbers for a given player.
     *
     * @param playerName target player's name
     * @return a list of integers from 1..N where N is the number of quests,
     * or an empty list if the player or their quests cannot be resolved.
     */
    private List<String> getQuestNumbers(String playerName) {
        final Player target = Bukkit.getPlayerExact(playerName);
        final PlayerQuests pq = (target != null) ? ODailyQuestsAPI.getPlayerQuests(target.getName()) : null;
        if (pq == null) return Collections.emptyList();

        final int size = pq.getQuests().size();
        final List<String> numbers = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            numbers.add(String.valueOf(i));
        }
        return numbers;
    }

    /**
     * @return a list of all registered category names
     */
    private List<String> getCategoryNames() {
        return new ArrayList<>(CategoriesLoader.getAllCategories().keySet());
    }

    /**
     * Builds a list of quest IDs belonging to a given category.
     *
     * @param categoryName the name of the category
     * @return list of quest IDs or empty if the category is invalid
     */
    private List<String> getQuestIds(String categoryName) {
        if (!CategoriesLoader.hasCategory(categoryName)) return Collections.emptyList();

        final Category category = CategoriesLoader.getCategoryByName(categoryName);
        if (category == null) return Collections.emptyList();

        final List<String> ids = new ArrayList<>();
        category.forEach(q -> ids.add(q.getFileIndex()));
        return ids;
    }
}
