package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface PlayerCommandCompleter {
    List<String> onTabComplete(CommandSender sender, String[] args);
}
