package com.ordwen.odailyquests.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCompleter implements TabCompleter {

    private final String CONVERT = "convert";

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length <= 1) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("reload", "reset", "show", "complete", "help", "holo", CONVERT));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        }
        else if (args.length == 2 ) {
            if (args[0].equalsIgnoreCase("holo")) {
                List<String> allCompletions = new ArrayList<>(Arrays.asList("create", "delete"));
                List<String> completions = new ArrayList<>();

                StringUtil.copyPartialMatches(args[1], allCompletions, completions);
                Collections.sort(completions);
                return completions;
            }
            else if (args[0].equalsIgnoreCase("reset")) {
                List<String> allCompletions = new ArrayList<>(Arrays.asList("quests", "total"));
                List<String> completions = new ArrayList<>();

                StringUtil.copyPartialMatches(args[1], allCompletions, completions);
                Collections.sort(completions);
                return completions;
            }
            else if (args[0].equalsIgnoreCase(CONVERT)) {
                List<String> allCompletions = new ArrayList<>(List.of("yaml"));
                List<String> completions = new ArrayList<>();

                StringUtil.copyPartialMatches(args[1], allCompletions, completions);
                Collections.sort(completions);
                return completions;
            }
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("holo") && args[1].equalsIgnoreCase("create")) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("global", "easy", "medium", "hard"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase(CONVERT)) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("mysql", "h2"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
