package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.interfaces.QuestInventory;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.player.handlers.PRerollCommand;
import com.ordwen.odailyquests.commands.player.handlers.ShowCommand;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlayerCommands extends BukkitCommand {

    public QuestSystem questSystem;
    PlayerMessages playerMessages = new PlayerMessages();

    public PlayerCommands(String commandName, QuestSystem questSystem) {
        super(commandName);
        this.questSystem = questSystem;
        this.description = "default player command";
        this.setPermission(QuestsPermissions.QUEST_USE.getPermission());
        this.setAliases(questSystem.getCommandAliases());
        PluginLogger.info("Successfully registered command " + commandName);
    }


    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            playerMessages.playerOnly(sender);
            return false;
        }

        if (!sender.hasPermission(QuestsPermissions.QUEST_USE.getPermission())) {
            playerMessages.noPermission(sender);
            return true;
        }
        if (args.length >= 1) {
            switch (args[0]) {
                case "show" -> new ShowCommand(player, args, questSystem, playerMessages).handle();
                case "reroll" -> new PRerollCommand(player, args, questSystem, playerMessages).handle();
                case "me" -> openInventory(questSystem, player);
                default -> playerMessages.help(player);
            }
        } else openInventory(questSystem, player);

        return true;
    }

    /**
     * Opens the quests interface for the player.
     *
     * @param player the player.
     */
    private void openInventory(QuestSystem questSystem, Player player) {
        final QuestInventory inventory = PlayerQuestsInterface.getPlayerQuestsInterface(questSystem, player);
        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "Impossible to open the quests interface. Is the plugin still loading?");
            player.sendMessage(ChatColor.RED + "If the problem persists, please contact the server administrator.");
            return;
        }

        player.openInventory(inventory.getInventory());
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

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

                    for (int i = 1; i <= questSystem.getQuestsAmount(); i++) {
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
