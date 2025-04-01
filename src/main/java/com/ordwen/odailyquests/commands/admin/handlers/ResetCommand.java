package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.IAdminCommand;
import com.ordwen.odailyquests.commands.admin.AdminMessages;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ResetCommand extends AdminMessages implements IAdminCommand {

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[1] != null && args[2] != null) {

            final Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            switch (args[1]) {
                case "quests" -> quests(sender, target);
                case "total" -> total(sender, target);
                default -> help(sender);
            }

        } else help(sender);
    }

    /**
     * Resets the current active quests of the player.
     * @param sender the command sender
     * @param target the player to reset
     */
    public void quests(CommandSender sender, Player target) {
        final String playerName = target.getName();
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        int totalAchievedQuests = activeQuests.get(playerName).getTotalAchievedQuests();
        QuestLoaderUtils.loadNewPlayerQuests(playerName, QuestsManager.getActiveQuests(), totalAchievedQuests);

        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));
    }

    /**
     * Resets the total amount of quests achieved by the player.
     * @param sender the command sender
     * @param target the player to reset
     */
    public void total(CommandSender sender, Player target) {
        QuestsManager.getActiveQuests().get(target.getName()).setTotalAchievedQuests(0);

        String msg = QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

        msg = QuestsMessages.TOTAL_AMOUNT_RESET.getMessage(target);
        if (msg != null) target.sendMessage(msg);
    }
}
