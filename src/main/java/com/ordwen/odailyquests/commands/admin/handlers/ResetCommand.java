package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        int totalAchievedQuests = activeQuests.get(playerName).getTotalAchievedQuests();
        QuestLoaderUtils.loadNewPlayerQuests(playerName, QuestsManager.getActiveQuests(), totalAchievedQuests);

        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));
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
