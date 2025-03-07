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
            final List<String> allCompletions = new ArrayList<>(Arrays.asList("reload", "reset", "reroll", "add", "show", "complete", "customcomplete", "help", "convert"));
            final List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 2) {
            final List<String> allCompletions;
            final List<String> completions = new ArrayList<>();

            switch (args[0]) {
                case "reset" -> allCompletions = new ArrayList<>(List.of("quests", "total"));
                case "add" -> allCompletions = new ArrayList<>(List.of("total"));
                case "convert" -> allCompletions = new ArrayList<>(List.of("mysql", "sqlite"));
                default -> {
                    return null;
                }
            }

            StringUtil.copyPartialMatches(args[1], allCompletions, completions);
            Collections.sort(completions);
            return completions;
        } else if (args.length == 3) {
            final List<String> allCompletions = new ArrayList<>();
            final List<String> completions = new ArrayList<>();

            if (args[0].equalsIgnoreCase("convert")) {
                allCompletions.addAll(List.of("mysql", "sqlite"));
            } else if (args[0].equalsIgnoreCase("reroll") || args[0].equalsIgnoreCase("complete")) {
                for (int i = 1; i <= QuestsAmount.getQuestsAmount(); i++) {
                    allCompletions.add(String.valueOf(i));
                }
            }

            StringUtil.copyPartialMatches(args[2], allCompletions, completions);
            Collections.sort(completions);

            if (allCompletions.isEmpty()) return null;
            return completions;
        }

        return null;
    }
}

