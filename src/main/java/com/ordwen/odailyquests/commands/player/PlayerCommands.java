package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.player.handlers.PRerollCommand;
import com.ordwen.odailyquests.commands.player.handlers.ShowCommand;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands extends PlayerMessages implements CommandExecutor {

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
                case "show" -> new ShowCommand(player, args).handle();
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
        final Inventory inventory = PlayerQuestsInterface.getPlayerQuestsInterface(player);
        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "Impossible to open the quests interface. Is the plugin still loading?");
            player.sendMessage(ChatColor.RED + "If the problem persists, please contact the server administrator.");
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
            FloodgateApi api = FloodgateApi.getInstance();
            if (api.isFloodgatePlayer(player.getUniqueId())) {
                api.sendForm(player.getUniqueId(), PlayerQuestsInterface.getPlayerQuestsForm(player));
            } else {
                player.openInventory(inventory);
            }
        } else {
            player.openInventory(inventory);
        }

    }
}
