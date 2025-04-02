package com.ordwen.odailyquests.api.commands.admin;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface AdminCommandCompleter {
    List<String> onTabComplete(CommandSender sender, String[] args);
}
