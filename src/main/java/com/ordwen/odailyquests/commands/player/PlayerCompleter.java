package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.api.commands.player.PlayerCommandBase;
import com.ordwen.odailyquests.api.commands.player.PlayerCommandRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerCompleter implements TabCompleter {

    private final PlayerCommandRegistry commandRegistry;

    public PlayerCompleter(PlayerCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            final List<String> subCommands = new ArrayList<>(commandRegistry.keySet());
            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        } else {
            final PlayerCommandBase subCommand = commandRegistry.getCommandHandler(args[0]);
            if (subCommand == null) {
                return Collections.emptyList();
            }
            return subCommand.onTabComplete(sender, args);
        }
    }
}
