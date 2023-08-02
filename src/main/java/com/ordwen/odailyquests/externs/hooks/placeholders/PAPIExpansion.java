package com.ordwen.odailyquests.externs.hooks.placeholders;

import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

public class PAPIExpansion extends PlaceholderExpansion {

    public PAPIExpansion() {
        PAPIHook.setPlaceholderAPIHooked(true);
    }

    @Override
    public String getIdentifier() {
        return "odailyquests";
    }

    @Override
    public String getAuthor() {
        return "Ordwen";
    }

    @Override
    public String getVersion() {
        return "1.0.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) return null;
        final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(player.getName());

        // player placeholders
        if (params.equalsIgnoreCase("total")) {
            return String.valueOf(playerQuests.getTotalAchievedQuests());
        }
        if (params.equalsIgnoreCase("achieved")) {
            return String.valueOf(playerQuests.getAchievedQuests());
        }
        if (params.equalsIgnoreCase("drawin")) {
            return TimeRemain.timeRemain(player.getName());
        }
        if (params.startsWith("progress")) {
            return String.valueOf(getPlayerQuestProgression(params, playerQuests));
        }
        if (params.startsWith("name")) {
            return getPlayerQuestName(params, playerQuests);
        }
        if (params.startsWith("desc")) {
            return getPlayerQuestDescription(params, playerQuests);
        }
        if (params.startsWith("iscompleted")) {
            return isPlayerQuestCompleted(params, playerQuests);
        }
        if (params.startsWith("requiredamount")) {
            return getPlayerQuestRequiredAmount(params, playerQuests);
        }

        // quests placeholders
        if (params.startsWith("global")) {
            return getQuestName(params, CategoriesLoader.getGlobalQuests());
        }
        if (params.startsWith("easy")) {
            return getQuestName(params, CategoriesLoader.getEasyQuests());
        }
        if (params.startsWith("medium")) {
            return getQuestName(params, CategoriesLoader.getMediumQuests());
        }
        if (params.startsWith("hard")) {
            return getQuestName(params, CategoriesLoader.getHardQuests());
        }

        return null;
    }

    /**
     * Check if the quest is completed.
     * @param params the placeholder.
     * @param playerQuests the player quests.
     * @return the result.
     */
    private String isPlayerQuestCompleted(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return String.valueOf(playerQuests.getPlayerQuests().get(quest).isAchieved());
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the name of the current player quest.
     * @param params the placeholder.
     * @param playerQuests the player quests.
     * @return the name of the quest.
     */
    private String getPlayerQuestName(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return quest.getQuestName();
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the required amount of a quest.
     * @param params the placeholder.
     * @param playerQuests the player quests.
     * @return the required amount.
     */
    private String getPlayerQuestRequiredAmount(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return String.valueOf(quest.getAmountRequired());
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the line of a specified quest description
     * @param params the placeholder
     * @param playerQuests the player quests
     * @return the line of the description
     */
    private String getPlayerQuestDescription(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1, params.lastIndexOf("_"))) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int line;
        try {
            line = Integer.parseInt(params.substring(params.lastIndexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid line.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                if (line <= quest.getQuestDesc().size()) return quest.getQuestDesc().get(line);
                else return ChatColor.RED + "Invalid line.";
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get player quest progression.
     *
     * @param params the placeholder
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
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return playerQuests.getPlayerQuests().get(quest).getProgression();
            }
            i++;
        }
        return -1;
    }

    /**
     * Get quest name by index & list.
     *
     * @param params placeholder
     * @param quests list where find the quest
     * @return the name of the quest
     */
    private String getQuestName(String params, ArrayList<AbstractQuest> quests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }
        if (quests.size() - 1 >= index) {
            return quests.get(index).getQuestName();
        } else return ChatColor.RED + "Invalid index.";
    }
}
