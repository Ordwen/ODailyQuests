package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.admin.handlers.*;
import com.ordwen.odailyquests.commands.admin.convert.ConverterManager;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AdminCommands implements CommandExecutor {

    private final ReloadService reloadService;

    public AdminCommands(ODailyQuests oDailyQuests) {
        this.reloadService = oDailyQuests.getReloadService();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    reloadService.reload();
                    sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
                } else help(sender);
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "convert" -> new ConverterManager(sender, args).handle();
                    case "reset" -> new ResetCommand(sender, args).handle();
                    case "add" -> new AddCommand(sender, args).handle();
                    case "reroll" -> new ARerollCommand(sender, args).handle();
                    case "show" -> new ShowCommand(sender, args).handle();
                    case "open" -> new OpenCommand(sender, args).handle();
                    case "complete" -> new CompleteCommand(sender, args).handle();
                    case "customcomplete" -> new CustomCompleteCommand(sender, args).handle();
                    default -> help(sender);
                }
            } else help(sender);
        } else {
            final String msg = QuestsMessages.NO_PERMISSION.toString();
            if (msg != null) sender.sendMessage(msg);
        }
        return false;
    }

    /**
     * Sends the admin help message to the sender.
     * @param sender the command sender.
     */
    private void help(CommandSender sender) {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
