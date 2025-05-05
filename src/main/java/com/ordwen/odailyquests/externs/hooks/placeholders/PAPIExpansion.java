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
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PAPIExpansion extends PlaceholderExpansion {

    private static final String INVALID_INDEX = ChatColor.RED + "Invalid index.";

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
        placeholdersList.add("%odailyquests_requiredamount_");
        placeholdersList.add("%odailyquests_total_category_");

        final Map<String, Category> categoryMap = CategoriesLoader.getAllCategories();
        for (String categoryKey : categoryMap.keySet()) {
            placeholdersList.add("%odailyquests_" + categoryKey + "_");
        }

        return placeholdersList;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) return null;
        if (QuestLoaderUtils.isTimeToRenew((Player) player, QuestsManager.getActiveQuests())) return null;

        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(player.getName());

        final Map<String, Function<String, String>> placeholders = new HashMap<>();
        placeholders.put("total", p -> String.valueOf(playerQuests.getTotalAchievedQuests()));
        placeholders.put("achieved", p -> String.valueOf(playerQuests.getAchievedQuests()));
        placeholders.put("drawin", p -> TimeRemain.timeRemain(player.getName()));
        placeholders.put("interface", p -> getInterfaceMessage(p, player, playerQuests));
        placeholders.put("progressbar", p -> getProgressBar(p, playerQuests));
        placeholders.put("progress", p -> String.valueOf(getPlayerQuestProgression(p, playerQuests)));
        placeholders.put("name", p -> getPlayerQuestName(p, playerQuests));
        placeholders.put("desc", p -> getPlayerQuestDescription(p, playerQuests));
        placeholders.put("iscompleted", p -> isPlayerQuestCompleted(p, playerQuests));
        placeholders.put("requiredamount", p -> getPlayerQuestRequiredAmount(p, playerQuests));
        placeholders.put("total_category", p -> getTotalAchievedQuestsByCategory(p, playerQuests));

        for (Map.Entry<String, Function<String, String>> entry : placeholders.entrySet()) {
            if (params.startsWith(entry.getKey())) {
                return entry.getValue().apply(params);
            }
        }

        return getQuestNameByCategory(params);
    }

    private String getTotalAchievedQuestsByCategory(String p, PlayerQuests playerQuests) {
        final String categoryName = p.substring(15);
        if (CategoriesLoader.getAllCategories().containsKey(categoryName)) {
            return String.valueOf(playerQuests.getTotalAchievedQuestsByCategory(categoryName));
        } else {
            return ChatColor.RED + "Invalid category.";
        }
    }

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
     * Get interface placeholders.
     *
     * @param params       the placeholder.
     * @param player       the player.
     * @param playerQuests the player quests.
     * @return the result.
     */
    private String getInterfaceMessage(String params, OfflinePlayer player, PlayerQuests playerQuests) {
        if (params.equals("interface_complete_get_type")) {
            return TextFormatter.format((Player) player, playerQuestsInterface.getCompleteGetTypeStr());
        } else if (params.startsWith("interface_status_")) {
            final String supposedIndex = params.substring("interface_status_".length());
            int index;

            try {
                index = Integer.parseInt(supposedIndex) - 1;
            } catch (Exception e) {
                return INVALID_INDEX;
            }

            return TextFormatter.format((Player) player, getQuestStatus(index, playerQuests));
        }

        return ChatColor.RED + "Invalid placeholder.";
    }

    /**
     * Check if the quest is completed.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the result.
     */
    private String isPlayerQuestCompleted(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                return String.valueOf(playerQuests.getQuests().get(quest).isAchieved());
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get the corresponding text for the quest status.
     *
     * @param index        the quest index.
     * @param playerQuests the player quests.
     * @return the achieved message or the progress message.
     */
    private String getQuestStatus(int index, PlayerQuests playerQuests) {

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                final Progression progression = playerQuests.getQuests().get(quest);

                return (playerQuests.getQuests().get(quest).isAchieved() ? playerQuestsInterface.getAchievedStr() : playerQuestsInterface.getProgressStr())
                        .replace("%progress%", String.valueOf(progression.getAdvancement()))
                        .replace("%required%", String.valueOf(progression.getRequiredAmount()))
                        .replace("%progressBar%", ProgressBar.getProgressBar(progression.getAdvancement(), progression.getRequiredAmount()));
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get the name of the current player quest.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the name of the quest.
     */
    private String getPlayerQuestName(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                return quest.getQuestName();
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get the required amount of a quest.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the required amount.
     */
    private String getPlayerQuestRequiredAmount(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                final Progression progression = playerQuests.getQuests().get(quest);
                return String.valueOf(progression.getRequiredAmount());
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get the line of a specified quest description
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return the line of the description
     */
    private String getPlayerQuestDescription(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1, params.lastIndexOf("_"))) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }

        int line;
        try {
            line = Integer.parseInt(params.substring(params.lastIndexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid line.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                if (line <= quest.getQuestDesc().size()) return quest.getQuestDesc().get(line);
                else return ChatColor.RED + "Invalid line.";
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get player quest progression.
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return current progression
     */
    public int getPlayerQuestProgression(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return -1;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                return playerQuests.getQuests().get(quest).getAdvancement();
            }
            i++;
        }
        return -1;
    }

    /**
     * Get the progress bar of a quest.
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return the progress bar
     */
    private String getProgressBar(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getQuests().keySet()) {
            if (i == index) {
                final Progression progression = playerQuests.getQuests().get(quest);
                return ProgressBar.getProgressBar(progression.getAdvancement(), progression.getRequiredAmount());
            }
            i++;
        }

        return INVALID_INDEX;
    }

    /**
     * Get quest name by index & list.
     *
     * @param params placeholder
     * @param quests list where find the quest
     * @return the name of the quest
     */
    private String getQuestName(String params, List<AbstractQuest> quests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return INVALID_INDEX;
        }
        if (quests.size() - 1 >= index) {
            return quests.get(index).getQuestName();
        } else return INVALID_INDEX;
    }
}
