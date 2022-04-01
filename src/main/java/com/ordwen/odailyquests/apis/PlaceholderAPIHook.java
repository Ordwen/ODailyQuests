package com.ordwen.odailyquests.apis;

import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

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
        return "1.0.0";
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
            return PlayerQuestsInterface.timeRemain(player.getName());
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
        return null;
    }

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
}
