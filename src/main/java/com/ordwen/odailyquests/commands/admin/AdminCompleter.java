package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandRegistry;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCompleter implements TabCompleter {

    private final AdminCommandRegistry commandRegistry;

    public AdminCompleter(AdminCommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            final List<String> subCommands = new ArrayList<>(commandRegistry.getCommandNames());
            subCommands.add("reload");

            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
        } else {
            final AdminCommandBase subCommand = commandRegistry.getCommandHandler(args[0]);
            if (subCommand == null) {
                return Collections.emptyList();
            }
            return subCommand.onTabComplete(sender, args);
        }
    }
}

