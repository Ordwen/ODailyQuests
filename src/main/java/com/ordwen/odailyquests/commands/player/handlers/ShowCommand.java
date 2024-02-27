package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends PCommandHandler {

    public ShowCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void handle() {
        if (!player.hasPermission(QuestsPermissions.QUEST_SHOW.getPermission())) {
            noPermission(player);
            return;
        }

        if (args.length == 2) {
            help(player);
            return;
        }

        switch (args[1]) {

            case "global" -> {
                if (Modes.getQuestsMode() == 2) {
                    categorizedEnabled(player);
                    return;
                }

                if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                    noPermissionCategory(player);
                    return;
                }
                final Inventory inventory = InterfacesManager.getInterfaceFirstPage("global", player);
                player.openInventory(inventory);
            }

            case "easy" -> {
                if (Modes.getQuestsMode() == 1) {
                    categorizedDisabled(player);
                    return;
                }

                if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                    noPermissionCategory(player);
                    return;
                }
                final Inventory inventory = InterfacesManager.getInterfaceFirstPage("easy", player);
                player.openInventory(inventory);

            }

            case "medium" -> {
                if (Modes.getQuestsMode() == 1) {
                    categorizedDisabled(player);
                    return;
                }

                if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                    noPermissionCategory(player);
                    return;
                }
                final Inventory inventory = InterfacesManager.getInterfaceFirstPage("medium", player);
                player.openInventory(inventory);

            }

            case "hard" -> {
                if (Modes.getQuestsMode() == 1) {
                    categorizedDisabled(player);
                    return;
                }

                if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                    noPermissionCategory(player);
                    return;
                }
                final Inventory inventory = InterfacesManager.getInterfaceFirstPage("hard", player);
                player.openInventory(inventory);

            }

            default -> invalidCategory(player);
        }
    }
}
