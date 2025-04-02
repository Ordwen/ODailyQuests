package com.ordwen.odailyquests.api.commands.admin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AdminCommandBase extends AdminMessages implements AdminCommand, AdminCommandCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return null;
    }
}