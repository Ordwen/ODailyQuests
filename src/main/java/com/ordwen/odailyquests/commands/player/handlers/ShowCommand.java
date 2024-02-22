package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends PCommandHandler {

    public ShowCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void handle() {
        if (player.hasPermission(QuestsPermissions.QUEST_SHOW.getPermission())) {
            if (args.length == 2) {
                switch (args[1]) {

                    case "global" -> {
                        if (Modes.getQuestsMode() == 2) {
                            final String msg = QuestsMessages.CATEGORIZED_ENABLED.toString();
                            if (msg != null) player.sendMessage(msg);
                            return;
                        }

                        if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                            final Inventory inventory = InterfacesManager.getInterfaceFirstPage("global", player);
                            player.openInventory(inventory);
                        } else noPermissionCategory();
                    }

                    case "easy" -> {
                        if (Modes.getQuestsMode() == 1) {
                            categorizedDisabled();
                            return;
                        }

                        if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                            final Inventory inventory = InterfacesManager.getInterfaceFirstPage("easy", player);
                            player.openInventory(inventory);
                        } else noPermissionCategory();

                    }

                    case "medium" -> {
                        if (Modes.getQuestsMode() == 1) {
                            categorizedDisabled();
                            return;
                        }

                        if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                            final Inventory inventory = InterfacesManager.getInterfaceFirstPage("medium", player);
                            player.openInventory(inventory);
                        } else noPermissionCategory();

                    }

                    case "hard" -> {
                        if (Modes.getQuestsMode() == 1) {
                            categorizedDisabled();
                            return;
                        }

                        if (player.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                            final Inventory inventory = InterfacesManager.getInterfaceFirstPage("hard", player);
                            player.openInventory(inventory);
                        } else noPermissionCategory();

                    }

                    default -> {
                        final String msg = QuestsMessages.INVALID_CATEGORY.toString();
                        if (msg != null) player.sendMessage(msg);
                    }
                }
            } else help();
        } else noPermission();
    }
}
