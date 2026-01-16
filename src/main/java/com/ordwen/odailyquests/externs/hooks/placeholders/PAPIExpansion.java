package com.ordwen.odailyquests.externs.hooks.placeholders;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.DisplayName;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.QuestPlaceholders;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class PAPIExpansion extends PlaceholderExpansion {

    private static final String INVALID_INDEX = ChatColor.RED + "Invalid index.";
    private static final String INVALID_LINE = ChatColor.RED + "Invalid line.";
    private static final String INVALID_PLACEHOLDER = ChatColor.RED + "Invalid placeholder.";
    private static final String INVALID_CATEGORY = ChatColor.RED + "Invalid category.";

    private final PlayerQuestsInterface playerQuestsInterface;

    public PAPIExpansion(PlayerQuestsInterface playerQuestsInterface) {
        TextFormatter.setPlaceholderAPIEnabled(true);
        this.playerQuestsInterface = playerQuestsInterface;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "odailyquests";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Ordwen";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.4";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @NotNull
    @Override
    public List<String> getPlaceholders() {
        final List<String> placeholdersList = new ArrayList<>();
        placeholdersList.add("%odailyquests_total%");
        placeholdersList.add("%odailyquests_achieved%");
        placeholdersList.add("%odailyquests_drawin%");

        placeholdersList.add("%odailyquests_name_");
        placeholdersList.add("%odailyquests_desc_");
        placeholdersList.add("%odailyquests_progress_");
        placeholdersList.add("%odailyquests_progressbar_");
        placeholdersList.add("%odailyquests_iscompleted_");
        placeholdersList.add("%odailyquests_status_");
        placeholdersList.add("%odailyquests_requiredamount_");
        placeholdersList.add("%odailyquests_requireddisplayname_");

        final Map<String, Category> categoryMap = CategoriesLoader.getAllCategories();
        for (String categoryKey : categoryMap.keySet()) {
            placeholdersList.add("%odailyquests_" + categoryKey + "_");
            placeholdersList.add("%odailyquests_total_" + categoryKey + "%");
        }

        return placeholdersList;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        final String playerName = offlinePlayer.getName();
        if (playerName == null) return null;

        if (!QuestsManager.getActiveQuests().containsKey(playerName)) return null;

        final Player player = offlinePlayer.getPlayer();
        if (player == null) return null;

        if (QuestLoaderUtils.isTimeToRenew(player, QuestsManager.getActiveQuests())) return null;

        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(playerName);
        if (playerQuests == null) return null;

        final Map<String, Function<String, String>> placeholders = new HashMap<>();
        placeholders.put("total", placeholder -> getTotalAchievedQuests(placeholder, playerQuests));
        placeholders.put("achieved", placeholder -> String.valueOf(playerQuests.getAchievedQuests()));
        placeholders.put("drawin", placeholder -> TimeRemain.timeRemain(playerName));
        placeholders.put("interface", placeholder -> getInterfaceMessage(placeholder, player, playerQuests));
        placeholders.put("progressbar", placeholder -> getProgressBar(placeholder, playerQuests));
        placeholders.put("progress", placeholder -> String.valueOf(getPlayerQuestProgression(placeholder, playerQuests)));
        placeholders.put("name", placeholder -> getPlayerQuestName(placeholder, player, playerQuests));
        placeholders.put("desc", placeholder -> getPlayerQuestDescription(placeholder, player, playerQuests));
        placeholders.put("iscompleted", placeholder -> isPlayerQuestCompleted(placeholder, playerQuests));
        placeholders.put("status", placeholder -> getQuestStatus(placeholder, playerQuests));
        placeholders.put("requiredamount", placeholder -> getPlayerQuestRequiredAmount(placeholder, playerQuests));
        placeholders.put("requireddisplayname", placeholder -> getPlayerQuestDisplayName(placeholder, playerQuests));

        for (Map.Entry<String, Function<String, String>> entry : placeholders.entrySet()) {
            if (params.startsWith(entry.getKey())) {
                return entry.getValue().apply(params);
            }
        }

        return getQuestNameByCategory(params);
    }

    /**
     * Simple container holding a quest and its associated progression.
     *
     * @param quest       the quest instance
     * @param progression the player's progression for this quest
     */
    private record QuestCtx(AbstractQuest quest, Progression progression) {
    }

    /**
     * Retrieves the quest and its progression by a zero-based index.
     * <p>
     * The index is resolved using the iteration order of the underlying
     * {@link java.util.LinkedHashMap}, ensuring stable and deterministic ordering.
     *
     * @param playerQuests the player's quests container
     * @param index0       zero-based quest index
     * @return an {@link Optional} containing the quest context, or empty if the index is out of bounds
     */
    private Optional<QuestCtx> getQuestCtxByIndex(PlayerQuests playerQuests, int index0) {
        if (index0 < 0) return Optional.empty();

        int i = 0;
        for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.getQuests().entrySet()) {
            if (i == index0) {
                return Optional.of(new QuestCtx(entry.getKey(), entry.getValue()));
            }
            i++;
        }
        return Optional.empty();
    }

    /**
     * Parses a single 1-based quest index from a placeholder of the form {@code xxx_<index>}.
     * <p>
     * Example: {@code name_2} → index {@code 1} (zero-based).
     *
     * @param params the placeholder parameters
     * @return an {@link OptionalInt} containing the zero-based index, or empty if parsing fails
     */
    private OptionalInt parseSingleIndex0(String params) {
        try {
            final int idx1 = Integer.parseInt(params.substring(params.indexOf('_') + 1));
            return OptionalInt.of(idx1 - 1);
        } catch (Exception ignored) {
            return OptionalInt.empty();
        }
    }

    /**
     * Parses a quest index and a description line index from a placeholder of the form
     * {@code desc_<questIndex>_<lineIndex>}.
     * <p>
     * Both indices are expected to be 1-based in the placeholder and are converted
     * to zero-based indices.
     *
     * @param params the placeholder parameters
     * @return an {@link Optional} containing an array {@code [questIndex0, lineIndex0]},
     * or empty if parsing fails
     */
    private Optional<int[]> parseQuestIndexAndLine0(String params) {
        try {
            final int firstUnderscore = params.indexOf('_');
            final int lastUnderscore = params.lastIndexOf('_');
            if (firstUnderscore < 0 || lastUnderscore <= firstUnderscore) return Optional.empty();

            final int questIdx1 = Integer.parseInt(params.substring(firstUnderscore + 1, lastUnderscore));
            final int lineIdx1 = Integer.parseInt(params.substring(lastUnderscore + 1));
            return Optional.of(new int[]{questIdx1 - 1, lineIdx1 - 1});
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    /**
     * Resolves the total number of achieved quests.
     * <p>
     * Supported formats:
     * <ul>
     *   <li>{@code total}</li>
     *   <li>{@code total_<category>}</li>
     * </ul>
     *
     * @param p            the placeholder parameter
     * @param playerQuests the player's quests
     * @return the total achieved quests as a string, or an error message
     */
    private String getTotalAchievedQuests(String p, PlayerQuests playerQuests) {
        if (p.equals("total")) {
            return String.valueOf(playerQuests.getTotalAchievedQuests());
        }

        if (p.startsWith("total_")) {
            final String categoryName = p.substring("total_".length());
            if (CategoriesLoader.getAllCategories().containsKey(categoryName)) {
                return String.valueOf(playerQuests.getTotalAchievedQuestsByCategory(categoryName));
            } else {
                return INVALID_CATEGORY;
            }
        }

        return INVALID_PLACEHOLDER;
    }

    /**
     * Resolves a quest name using a category-based placeholder.
     *
     * @param params the placeholder parameters
     * @return the quest name, or {@code null} if the category does not match
     */
    private String getQuestNameByCategory(String params) {
        final Map<String, Category> categoryMap = CategoriesLoader.getAllCategories();

        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            if (params.startsWith(entry.getKey())) {
                return getQuestName(params, entry.getValue());
            }
        }

        return null;
    }

    /**
     * Resolves interface-related placeholders.
     * <p>
     * Supported placeholders:
     * <ul>
     *   <li>{@code interface_complete_get_type}</li>
     *   <li>{@code interface_status_<index>}</li>
     * </ul>
     *
     * @param params       the placeholder parameters
     * @param player       the online player
     * @param playerQuests the player's quests
     * @return the formatted interface message or an error message
     */
    private String getInterfaceMessage(String params, Player player, PlayerQuests playerQuests) {
        if (params.equals("interface_complete_get_type")) {
            return TextFormatter.format(player, playerQuestsInterface.getCompleteGetTypeStr());
        } else if (params.startsWith("interface_status_")) {
            final String supposedIndex = params.substring("interface_status_".length());
            final int index0;
            try {
                index0 = Integer.parseInt(supposedIndex) - 1;
            } catch (Exception e) {
                return INVALID_INDEX;
            }
            return TextFormatter.format(player, getQuestProgress(index0, playerQuests));
        }

        return INVALID_PLACEHOLDER;
    }

    /**
     * Checks whether a quest is completed.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return {@code true} or {@code false} as a string, or an error message
     */
    private String isPlayerQuestCompleted(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> String.valueOf(ctx.progression().isAchieved()))
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves the status message for a quest.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return the quest status message or an error message
     */
    private String getQuestStatus(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        final QuestCtx ctx = getQuestCtxByIndex(playerQuests, idx0.getAsInt()).orElse(null);
        if (ctx == null) return INVALID_INDEX;

        final String achieved = TextFormatter.format(playerQuestsInterface.getAchievedStr());
        final String notAchieved = TextFormatter.format(playerQuestsInterface.getNotAchievedStr());

        final String raw = ctx.progression().isAchieved() ? achieved : notAchieved;
        return (raw == null || raw.isBlank()) ? INVALID_PLACEHOLDER : raw;
    }

    /**
     * Returns the formatted status message for a quest.
     * <p>
     * The message differs depending on whether the quest is completed or still in progress.
     *
     * @param index0       zero-based quest index
     * @param playerQuests the player's quests
     * @return the formatted status message or an error message
     */
    private String getQuestProgress(int index0, PlayerQuests playerQuests) {
        return getQuestCtxByIndex(playerQuests, index0)
                .map(ctx -> {
                    final Progression progression = ctx.progression();
                    final String template = progression.isAchieved()
                            ? playerQuestsInterface.getAchievedStr()
                            : playerQuestsInterface.getProgressStr();

                    return QuestPlaceholders.replaceProgressPlaceholders(template, progression.getAdvancement(), progression.getRequiredAmount(), progression.getRewardAmount());
                })
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves the name of a player's quest by index.
     *
     * @param params       the placeholder parameters
     * @param player       the online player
     * @param playerQuests the player's quests
     * @return the quest name or an error message
     */
    private String getPlayerQuestName(String params, Player player, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> {
                    final AbstractQuest quest = ctx.quest();
                    final Progression progression = playerQuests.getQuests().get(quest);
                    return QuestPlaceholders.replaceQuestPlaceholders(quest.getQuestName(), player, quest, progression, playerQuests, playerQuestsInterface.getQuestStatus(progression, player));
                })
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves the required amount for a quest.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return the required amount as a string, or an error message
     */
    private String getPlayerQuestRequiredAmount(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> String.valueOf(ctx.progression().getRequiredAmount()))
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves a specific line of a quest description.
     *
     * @param params       the placeholder parameters
     * @param player       the online player
     * @param playerQuests the player's quests
     * @return the description line, or an error message if the index or line is invalid
     */
    private String getPlayerQuestDescription(String params, Player player, PlayerQuests playerQuests) {
        final Optional<int[]> parsed = parseQuestIndexAndLine0(params);
        if (parsed.isEmpty()) return INVALID_INDEX;

        final int questIndex0 = parsed.get()[0];
        final int lineIndex0 = parsed.get()[1];
        if (lineIndex0 < 0) return INVALID_LINE;

        return getQuestCtxByIndex(playerQuests, questIndex0)
                .map(ctx -> {
                    final AbstractQuest quest = ctx.quest();
                    final List<String> desc = quest.getQuestDesc();
                    if (desc == null || lineIndex0 >= desc.size()) {
                        return INVALID_LINE;
                    }

                    final Progression progression = playerQuests.getQuests().get(quest);
                    return QuestPlaceholders.replaceQuestPlaceholders(desc.get(lineIndex0), player, quest, progression, playerQuests, playerQuestsInterface.getQuestStatus(progression, player));
                })
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves the current progression value of a quest.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return the current progression value, or {@code -1} if invalid
     */
    public int getPlayerQuestProgression(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return -1;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> ctx.progression().getAdvancement())
                .orElse(-1);
    }

    /**
     * Retrieves the visual progress bar of a quest.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return the progress bar string, or an error message
     */
    private String getProgressBar(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> {
                    final AbstractQuest quest = ctx.quest();
                    final Progression progression = playerQuests.getQuests().get(quest);
                    return QuestPlaceholders.replaceProgressPlaceholders("%progressBar%", progression.getAdvancement(), progression.getRequiredAmount(), progression.getRewardAmount());
                })
                .orElse(INVALID_INDEX);
    }

    /**
     * Retrieves a quest name from a provided list using a placeholder index.
     *
     * @param params the placeholder parameters
     * @param quests the quest list
     * @return the quest name, or an error message if the index is invalid
     */
    private String getQuestName(String params, List<AbstractQuest> quests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        final int index0 = idx0.getAsInt();
        if (quests == null || index0 < 0 || index0 >= quests.size()) return INVALID_INDEX;

        return quests.get(index0).getQuestName();
    }

    /**
     * Retrieves the display name of the randomly selected required objective
     * for a player's quest.
     *
     * @param params       the placeholder parameters
     * @param playerQuests the player's quests
     * @return the display name of the selected required objective,
     * or an error message if the index is invalid
     */
    private String getPlayerQuestDisplayName(String params, PlayerQuests playerQuests) {
        final OptionalInt idx0 = parseSingleIndex0(params);
        if (idx0.isEmpty()) return INVALID_INDEX;

        return getQuestCtxByIndex(playerQuests, idx0.getAsInt())
                .map(ctx -> DisplayName.getDisplayName(ctx.quest(), ctx.progression().getSelectedRequiredIndex()))
                .orElse(INVALID_INDEX);
    }
}
