package com.ordwen.odailyquests.api.commands.admin;

import org.bukkit.command.CommandSender;

public interface AdminCommand {
    void execute(CommandSender sender, String[] args);
    String getName();
    String getPermission();
}
