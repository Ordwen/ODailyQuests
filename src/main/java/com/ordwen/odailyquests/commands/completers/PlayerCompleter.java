package com.ordwen.odailyquests.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length <= 1) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("show", "me", "help"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[0], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        }
        else if (args.length == 2) {
            List<String> allCompletions = new ArrayList<>(Arrays.asList("global", "easy", "medium", "hard"));
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(args[1], allCompletions, completions);
            Collections.sort(completions);

            return completions;
        }
        return null;
    }
}
