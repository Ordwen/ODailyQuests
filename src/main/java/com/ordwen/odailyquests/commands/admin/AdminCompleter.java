package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length <= 1) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("reload", "reset", "reroll", "add", "show", "complete", "help", "holo", "convert"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        } else if (args.length == 2) {
            final List<String> allCompletions;
            final List<String> completions = new ArrayList<>();

            switch (args[0]) {
                case "holo" -> allCompletions = new ArrayList<>(List.of("create", "delete"));
                case "reset" -> allCompletions = new ArrayList<>(List.of("quests", "total"));
                case "add" -> allCompletions = new ArrayList<>(List.of("total"));
                case "convert" -> allCompletions = new ArrayList<>(List.of("mysql", "h2"));
                case "reroll" -> {
                    allCompletions = new ArrayList<>();
                    for (int i = 1; i <= QuestsAmount.getQuestsAmount(); i++) {
                        allCompletions.add(String.valueOf(i));
                    }
                }
                default -> allCompletions = new ArrayList<>();
            }

            StringUtil.copyPartialMatches(args[1], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("holo") && args[1].equalsIgnoreCase("create")) {
            final List<String> allCompletions = new ArrayList<>(Arrays.asList("global", "easy", "medium", "hard"));
            final List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("convert")) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("mysql", "h2"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}