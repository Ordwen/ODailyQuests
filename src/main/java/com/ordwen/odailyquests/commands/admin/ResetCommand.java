package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.commands.ACommandHandler;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ResetCommand extends ACommandHandler {

    public ResetCommand(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void handle() {
        if (args.length >= 3 && args[1] != null && args[2] != null) {

            final Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                invalidPlayer();
                return;
            }

            switch (args[1]) {
                case "quests" -> quests(target);
                case "total" -> total(target);
                default -> help();
            }

        } else help();
    }

    /**
     * Resets the current active quests of the player.
     * @param target the player to reset
     */
    public void quests(Player target) {
        final String playerName = target.getName();
        final HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        int totalAchievedQuests = activeQuests.get(playerName).getTotalAchievedQuests();

        activeQuests.remove(playerName);

        final LinkedHashMap<AbstractQuest, Progression> quests = QuestsManager.selectRandomQuests();
        final PlayerQuests playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);

        playerQuests.setTotalAchievedQuests(totalAchievedQuests);
        playerQuests.setAchievedQuests(0);

        activeQuests.put(playerName, playerQuests);

        PluginLogger.fine(playerName + " inserted into the array.");

        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

        msg = QuestsMessages.QUESTS_RENEWED.getMessage(target);
        if (msg != null) target.sendMessage(msg);
    }

    /**
     * Resets the total amount of quests achieved by the player.
     * @param target the player to reset
     */
    public void total(Player target) {
        QuestsManager.getActiveQuests().get(target.getName()).setTotalAchievedQuests(0);

        String msg = QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

        msg = QuestsMessages.TOTAL_AMOUNT_RESET.getMessage(target);
        if (msg != null) target.sendMessage(msg);
    }
}
