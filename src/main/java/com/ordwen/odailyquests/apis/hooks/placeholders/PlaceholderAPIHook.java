package com.ordwen.odailyquests.apis.hooks.placeholders;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.GetPlaceholders;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final ODailyQuests oDailyQuests;

    public PlaceholderAPIHook(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
        GetPlaceholders.isPlaceholderAPIHooked = true;
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
        return "1.0.2";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

       /*
        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {

            if (oDailyQuests.getYamlManager() != null) {
                oDailyQuests.getYamlManager().getLoadProgressionYAML().loadPlayerQuests(
                        player.getName(),
                        QuestsManager.getActiveQuests(),
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
            } else {
                oDailyQuests.getSQLManager().getLoadProgressionSQL().loadProgression(
                        player.getName(),
                        QuestsManager.getActiveQuests(),
                        Modes.getQuestsMode(),
                        Modes.getTimestampMode(),
                        Temporality.getTemporalityMode());
            }
        }
        */

        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) return null;
        final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(player.getName());

        if (params.equalsIgnoreCase("total")) {
            return String.valueOf(playerQuests.getTotalAchievedQuests());
        }
        if (params.equalsIgnoreCase("achieved")) {
            return String.valueOf(playerQuests.getAchievedQuests());
        }
        if (params.equalsIgnoreCase("drawin")) {
            return TimeRemain.timeRemain(player.getName());
        }
        if (params.startsWith("global_")) {
            return getQuestName(params, LoadQuests.getGlobalQuests());
        }
        if (params.startsWith("easy_")) {
            return getQuestName(params, LoadQuests.getEasyQuests());
        }
        if (params.startsWith("medium_")) {
            return getQuestName(params, LoadQuests.getMediumQuests());
        }
        if (params.startsWith("hard_")) {
            return getQuestName(params, LoadQuests.getHardQuests());
        }

        if (params.startsWith("progress_")) {
            final int index = Integer.parseInt(params.substring(params.indexOf('_') + 1));
            return String.valueOf(getQuestProgression(index, player.getName()));
        }

        return null;
    }

    /**
     * Get player quest progression.
     *
     * @param index      player quest number
     * @param playerName player
     * @return quest progression
     */
    public int getQuestProgression(int index, String playerName) {
        int i = 0;
        for (AbstractQuest quest : QuestsManager.getActiveQuests().get(playerName).getPlayerQuests().keySet()) {
            if (i == index) {
                return QuestsManager.getActiveQuests().get(playerName).getPlayerQuests().get(quest).getProgression();
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
