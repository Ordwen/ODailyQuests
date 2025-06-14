package com.ordwen.odailyquests.commands.player;

import com.ordwen.odailyquests.api.commands.player.PlayerCommand;
import com.ordwen.odailyquests.api.commands.player.PlayerCommandRegistry;
import com.ordwen.odailyquests.api.commands.player.PlayerMessages;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands extends PlayerMessages implements CommandExecutor {

    private final InterfacesManager interfacesManager;
    private final PlayerCommandRegistry playerCommandRegistry;

    public PlayerCommands(InterfacesManager interfacesManager, PlayerCommandRegistry playerCommandRegistry) {
        this.interfacesManager = interfacesManager;
        this.playerCommandRegistry = playerCommandRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            playerOnly(sender);
            return true;
        }

        if (!sender.hasPermission(QuestsPermissions.QUESTS_PLAYER_USE.getPermission())) {
            noPermission(sender);
            return true;
        }

        if (args.length >= 1) {
            final PlayerCommand handler = playerCommandRegistry.getCommandHandler(args[0]);
            if (handler != null) {
                if (player.hasPermission(handler.getPermission())) {
                    handler.execute(player, args);
                } else {
                    noPermission(player);
                }
            } else {
                help(player);
            }
        } else {
            openInventory(player);
        }

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
            player.sendMessage(QuestsMessages.IMPOSSIBLE_TO_OPEN_INVENTORY.toString());
            player.sendMessage(QuestsMessages.CONTACT_ADMIN.toString());
            return;
        }

        player.openInventory(inventory);
    }
}
