package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class PlayerCommandBase extends PlayerMessages implements PlayerCommand, PlayerCommandCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}