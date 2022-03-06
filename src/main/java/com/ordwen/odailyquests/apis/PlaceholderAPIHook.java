package com.ordwen.odailyquests.apis;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final ODailyQuests oDailyQuests;

    /**
     * Constructor.
     * @param oDailyQuests main class.
     */
    public PlaceholderAPIHook(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
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
        return null;
    }
}
