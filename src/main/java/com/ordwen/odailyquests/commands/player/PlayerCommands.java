package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.player.handlers.PRerollCommand;
import com.ordwen.odailyquests.commands.player.handlers.ShowCommand;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands extends PlayerMessages implements CommandExecutor {

    private final InterfacesManager interfacesManager;

    public PlayerCommands(InterfacesManager interfacesManager) {
        this.interfacesManager = interfacesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            playerOnly(sender);
            return false;
        }

        if (!sender.hasPermission(QuestsPermissions.QUEST_USE.getPermission())) {
            noPermission(sender);
            return true;
        }

        if (args.length >= 1) {
            switch (args[0]) {
                case "show" -> new ShowCommand(interfacesManager.getQuestsInterfaces(), player, args).handle();
                case "reroll" -> new PRerollCommand(player, args).handle();
                case "me" -> openInventory(player);
                default -> help(player);
            }
        } else openInventory(player);

        return true;
    }

    /**
     * Opens the quests interface for the player.
     *
     * @param player the player.
     */
    private void openInventory(Player player) {
        final Inventory inventory = interfacesManager.getPlayerQuestsInterface().getPlayerQuestsInterface(player);
        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "Impossible to open the quests interface. Is the plugin still loading?");
            player.sendMessage(ChatColor.RED + "If the problem persists, please contact the server administrator.");
            return;
        }

        player.openInventory(inventory);
    }
}
