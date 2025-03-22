package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length <= 1) {
            final List<String> allCompletions = new ArrayList<>(Arrays.asList("show", "me", "help"));
            final List<String> completions = new ArrayList<>();
            if (sender.hasPermission("odailyquests.reroll")) { allCompletions.add("reroll"); }

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "reroll" -> {
                    final List<String> allCompletions = new ArrayList<>();
                    final List<String> completions = new ArrayList<>();

                    for (int i = 1; i <= QuestsPerCategory.getTotalQuestsAmount(); i++) {
                        allCompletions.add(String.valueOf(i));
                    }

                    StringUtil.copyPartialMatches(args[1], allCompletions, completions);
                    Collections.sort(completions);

                    return completions;
                }
                case "show" -> {
                    final List<String> allCompletions = new ArrayList<>(Arrays.asList("global", "easy", "medium", "hard"));
                    final List<String> completions = new ArrayList<>();

                    StringUtil.copyPartialMatches(args[1], allCompletions, completions);
                    Collections.sort(completions);

                    return completions;
                }
                default -> {
                    return null;
                }
            }
        }

        return null;
    }
}
