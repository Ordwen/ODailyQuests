package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.api.commands.player.PlayerCommandBase;
import com.ordwen.odailyquests.api.commands.player.PlayerCommandRegistry;
import com.ordwen.odailyquests.configuration.functionalities.CommandAliases;
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
            final List<String> suggestions = new ArrayList<>();

            for (PlayerCommandBase cmd : commandRegistry.getCommandHandlers()) {
                if (!sender.hasPermission(cmd.getPermission())) continue;

                final List<String> aliases = CommandAliases.getSubcommandAliases(cmd.getName());
                if (CommandAliases.isKeepingOnlyAliases() && !aliases.isEmpty()) {
                    suggestions.addAll(aliases);
                    continue;
                }

                suggestions.add(cmd.getName());
                suggestions.addAll(aliases);
            }

            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        } else {
            final PlayerCommandBase subCommand = commandRegistry.getCommandHandler(args[0]);
            if (subCommand == null) return Collections.emptyList();

            return subCommand.onTabComplete(sender, args);
        }
    }
}
