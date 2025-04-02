package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandRegistry;
import com.ordwen.odailyquests.api.commands.admin.AdminCommand;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AdminCommands implements CommandExecutor {

    private final ODailyQuests plugin;
    private final AdminCommandRegistry adminCommandRegistry;

    public AdminCommands(ODailyQuests plugin, AdminCommandRegistry adminCommandRegistry) {
        this.plugin = plugin;
        this.adminCommandRegistry = adminCommandRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            noPermission(sender);
        }

        if (args.length == 1) {
            if (args[0].equals("reload")) {
                plugin.getReloadService().reload();
                sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
            } else help(sender);
        } else if (args.length >= 2) {
            final AdminCommand handler = adminCommandRegistry.getCommandHandler(args[0]);
            if (handler != null) {
                if (sender.hasPermission(handler.getPermission())) {
                    handler.execute(sender, args);
                } else {
                    noPermission(sender);
                }
            } else help(sender);
        } else help(sender);


        return false;
    }

    /**
     * Sends the admin help message to the sender.
     *
     * @param sender the command sender.
     */
    private void help(CommandSender sender) {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Sends a message to the sender if they do not have permission to use the command.
     *
     * @param sender the command sender.
     */
    private void noPermission(CommandSender sender) {
        final String msg = QuestsMessages.NO_PERMISSION.toString();
        if (msg != null) sender.sendMessage(msg);
    }
}
