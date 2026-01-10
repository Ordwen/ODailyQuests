package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.JoinMessageDelay;
import com.ordwen.odailyquests.configuration.essentials.Logs;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ProgressionLoader {

    private static final String PLAYER = "Player ";

    protected static final String NEW_QUESTS = "New quests will be drawn.";
    protected static final String CONFIG_CHANGE = "This can happen if the quest has been modified in the config file.";

    public record StoredPlayerProgression(
            long timestamp,
            int achievedQuests,
            int totalAchievedQuests,
            int recentRerolls
    ) {
    }

    protected void registerLoadedPlayerQuests(
            Player player,
            Map<String, PlayerQuests> activeQuests,
            Map<String, Integer> categoryStats,
            LinkedHashMap<AbstractQuest, Progression> quests,
            StoredPlayerProgression data,
            boolean sendStatusMessage
    ) {
        final String playerName = player.getName();

        final PlayerQuests playerQuests = new PlayerQuests(data.timestamp(), quests);
        playerQuests.setAchievedQuests(data.achievedQuests());
        playerQuests.setTotalAchievedQuests(data.totalAchievedQuests());
        playerQuests.setTotalAchievedQuestsByCategory(categoryStats);
        playerQuests.setRecentRerolls(data.recentRerolls());

        activeQuests.put(playerName, playerQuests);

        if (Logs.isEnabled()) {
            PluginLogger.info(playerName + "'s quests have been loaded.");
        }

        if (sendStatusMessage) {
            sendQuestStatusMessage(player, data.achievedQuests(), playerQuests);
        }
    }

    protected void handleNewPlayer(String playerName, Map<String, PlayerQuests> activeQuests) {
        Debugger.write(PLAYER + playerName + " has no data in progression file.");
        QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, new HashMap<>(), 0);
    }

    protected void sendQuestStatusMessage(Player player, int achievedQuests, PlayerQuests playerQuests) {
        final String msg = (achievedQuests == playerQuests.getQuests().size()) ?
                QuestsMessages.ALL_QUESTS_ACHIEVED_CONNECT.getMessage(player.getName()) :
                QuestsMessages.QUESTS_IN_PROGRESS.getMessage(player.getName());

        if (msg != null && player.hasPermission(QuestsPermissions.QUESTS_PROGRESS.get())) {
            double delay = JoinMessageDelay.getDelay() * 20;
            ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player).runDelayed(() -> player.sendMessage(msg), null, (long) delay);
        }
    }

    protected void handleMissingQuests(String playerName) {
        Debugger.write(PLAYER + playerName + " was detected in the progression file but has no quests. This is not normal.");
        PluginLogger.error(PLAYER + playerName + " was detected in the progression file but has no quests. This is not normal.");
    }

    protected void handlePlayerDisconnected(String playerName) {
        Debugger.write(PLAYER + playerName + " is null. Impossible to load quests.");
        PluginLogger.warn("It looks like " + playerName + " has disconnected before their quests were loaded.");
    }

    protected void requiredAmountIsZero(String playerName) {
        PluginLogger.warn("Required amount is 0 for player " + playerName + ". " + NEW_QUESTS);
        PluginLogger.warn(CONFIG_CHANGE);
    }

    protected void requiredAmountNotEqual(String playerName) {
        PluginLogger.warn("Required amount is not equal to quest required amount for player " + playerName + ". " + NEW_QUESTS);
        PluginLogger.warn(CONFIG_CHANGE);
    }

    protected void error(String player, String message) {
        PluginLogger.error(ChatColor.RED + "An error occurred while loading player " + player + "'s quests.");
        Debugger.write("An error occurred while loading player " + player + "'s quests.");
        Debugger.write(message);
        PluginLogger.error(message);
    }

    protected boolean isSelectedRequiredInvalid(AbstractQuest quest, int selectedRequired, String playerName) {
        if (quest.isRandomRequired()) {
            if (selectedRequired == -1) {
                PluginLogger.warn("Random required is null for player " + playerName + ". " + NEW_QUESTS);
                PluginLogger.warn(CONFIG_CHANGE);
                return true;
            }

            if (quest instanceof EntityQuest eq && eq.getRequiredEntities().size() <= selectedRequired) {
                PluginLogger.warn("Selected required index is out of bounds for player " + playerName + ". " + NEW_QUESTS);
                PluginLogger.warn(CONFIG_CHANGE);
                return true;
            }

            if (quest instanceof ItemQuest iq && iq.getRequiredItems().size() <= selectedRequired) {
                PluginLogger.warn("Selected required index is out of bounds for player " + playerName + ". " + NEW_QUESTS);
                PluginLogger.warn(CONFIG_CHANGE);
                return true;
            }
        }

        return false;
    }
}
