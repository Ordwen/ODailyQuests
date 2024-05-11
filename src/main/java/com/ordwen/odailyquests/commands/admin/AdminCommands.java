package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.admin.handlers.*;
import com.ordwen.odailyquests.commands.admin.convert.ConverterManager;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommands extends BukkitCommand {

    private final ReloadService reloadService;
    public QuestSystem questSystem;

    public AdminCommands(ODailyQuests oDailyQuests, QuestSystem questSystem, String commandName) {
        super(commandName);
        this.reloadService = oDailyQuests.getReloadService();
        this.questSystem = questSystem;
        this.description = "admin command";
        this.setPermission(QuestsPermissions.QUESTS_ADMIN.getPermission());
        this.setAliases(questSystem.getAdminCommandAliases());
        PluginLogger.info("Successfully registered command " + commandName);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    reloadService.reload();
                    sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
                } else help(sender);
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "convert" -> new ConverterManager(sender, args, questSystem).handle();
                    case "reset" -> new ResetCommand(sender, args, questSystem).handle();
                    case "add" -> new AddCommand(sender, args, questSystem).handle();
                    case "reroll" -> new ARerollCommand(sender, args, questSystem).handle();
                    case "show" -> new ShowCommand(sender, args, questSystem).handle();
                    case "open" -> new OpenCommand(sender, args, questSystem).handle();
                    case "complete" -> new CompleteCommand(sender, args, questSystem).handle();
                    case "holo" -> new HoloCommand(sender, args, questSystem).handle();
                    default -> help(sender);
                }
            } else help(sender);
        } else {
            final String msg = QuestsMessages.NO_PERMISSION.toString();
            if (msg != null) sender.sendMessage(msg);
        }
        return false;
    }

    /**
     * Sends the admin help message to the sender.
     * @param sender the command sender.
     */
    private void help(CommandSender sender) {
        final String msg = QuestsMessages.ADMIN_HELP.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        if (args.length <= 1) {
            final List<String> allCompletions = new ArrayList<>(Arrays.asList("reload", "reset", "reroll", "add", "show", "complete", "help", "holo", "convert"));
            final List<String> completions = new ArrayList<>();

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

            if (args[0].equalsIgnoreCase("holo") && args[1].equalsIgnoreCase("create")) {
                allCompletions.addAll(List.of("global", "easy", "medium", "hard"));
            } else if (args[0].equalsIgnoreCase("convert")) {
                allCompletions.addAll(List.of("mysql", "h2"));
            } else if (args[0].equalsIgnoreCase("reroll") || args[0].equalsIgnoreCase("complete")) {
                for (int i = 1; i <= questSystem.getQuestsAmount(); i++) {
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
