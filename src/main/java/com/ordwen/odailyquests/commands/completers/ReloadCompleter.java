package com.ordwen.odailyquests.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length > 1) return Collections.emptyList();

        List<String> allCompletions = new ArrayList<>(Arrays.asList("config", "messages", "quests", "all"));
        List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(args[0], allCompletions, completions);
        Collections.sort(completions);

        return completions;
    }
}
