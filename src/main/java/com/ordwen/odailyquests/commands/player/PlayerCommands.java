package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.commands.player.handlers.ShowCommand;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (sender.hasPermission(QuestsPermissions.QUEST_USE.getPermission())) {
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "show" -> new ShowCommand(player, args).handle();
                        case "me" -> openInventory(player);
                        default -> {
                            final String msg = QuestsMessages.PLAYER_HELP.toString();
                            if (msg != null) sender.sendMessage(msg);
                        }
                    }
                } else openInventory(player);
            } else {
                final String msg = QuestsMessages.NO_PERMISSION.toString();
                if (msg != null) sender.sendMessage(msg);
            }
        }
        return false;
    }

    /**
     * Opens the quests interface for the player.
     * @param player the player.
     */
    private void openInventory(Player player) {
        final Inventory inventory = PlayerQuestsInterface.getPlayerQuestsInterface(player);
        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "Impossible to open the quests interface. Is the plugin still loading?");
            player.sendMessage(ChatColor.RED + "If the problem persists, please contact the server administrator.");
            return;
        }

        player.openInventory(inventory);
    }
}
