package com.ordwen.odailyquests.apis.hooks.placeholders;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

public class PlaceholderAPIHook extends PlaceholderExpansion {

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
        return "1.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("achieved")) {
            return String.valueOf(QuestsManager.getActiveQuests().get(player.getName()).getAchievedQuests());
        }
        if (params.equalsIgnoreCase("drawin")) {
            return TimeRemain.timeRemain(player.getName());
        }
        if (params.equalsIgnoreCase("progress_1")) {
            return String.valueOf(getQuestProgression(0, player.getName()));
        }
        if (params.equalsIgnoreCase("progress_2")) {
            return String.valueOf(getQuestProgression(1, player.getName()));
        }
        if (params.equalsIgnoreCase("progress_3")) {
            return String.valueOf(getQuestProgression(2, player.getName()));
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
        return null;
    }

    /**
     * Get player quest progression.
     * @param index player quest number
     * @param playerName player
     * @return quest progression
     */
    public int getQuestProgression(int index, String playerName) {
        int i = 0;
        for (Quest quest : QuestsManager.getActiveQuests().get(playerName).getPlayerQuests().keySet()) {
            if (i == index) {
                return QuestsManager.getActiveQuests().get(playerName).getPlayerQuests().get(quest).getProgression();
            }
            i++;
        }
        return -1;
    }

    /**
     * Get quest name by index & list.
     * @param params placeholder
     * @param quests list where find the quest
     * @return the name of the quest
     */
    private String getQuestName(String params, ArrayList<Quest> quests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }
        if (quests.size()-1 >= index) {
            return quests.get(index).getQuestName();
        } else return ChatColor.RED + "Invalid index.";
    }
}
