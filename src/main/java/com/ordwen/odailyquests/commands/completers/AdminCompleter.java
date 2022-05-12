package com.ordwen.odailyquests.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 1) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("reload", "reset", "show", "complete", "help", "holo"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        }
        else if (args.length == 2 && args[0].equalsIgnoreCase("holo")) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("create", "delete"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[1], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("holo") && args[1].equalsIgnoreCase("create")) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("global", "easy", "medium", "hard"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
