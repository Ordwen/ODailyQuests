package com.ordwen.odailyquests.api.commands.admin;

import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AdminCommandBase extends AdminMessages implements AdminCommand, AdminCommandCompleter {

    protected Player getTargetPlayer(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            invalidPlayer(sender);
        }
        return target;
    }

    protected int parseQuestIndex(CommandSender sender, String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(QuestsMessages.INVALID_QUEST_INDEX.toString());
            return -1;
        }
    }
}