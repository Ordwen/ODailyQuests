package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends PCommandHandler {

    private static final String GLOBAL = "global";
    private static final String EASY = "easy";
    private static final String MEDIUM = "medium";
    private static final String HARD = "hard";

    public ShowCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void handle() {
        if (!player.hasPermission(QuestsPermissions.QUEST_SHOW.getPermission())) {
            noPermission(player);
            return;
        }

        if (args.length != 2) {
            help(player);
            return;
        }

        if (args[1].equalsIgnoreCase(GLOBAL)) {
            openGlobal();
        } else openCategory(args[1]);
    }

    /**
     * Opens the category interface.
     *
     * @param category the category.
     */
    private void openCategory(String category) {
        if (Modes.getQuestsMode() == 1) {
            categorizedDisabled(player);
            return;
        }

        switch (category) {
            case GLOBAL -> openGlobal();
            case EASY -> openEasy();
            case MEDIUM -> openMedium();
            case HARD -> openHard();
            default -> invalidCategory(player);
        }
    }

    /**
     * Opens the global interface.
     */
    private void openGlobal() {
        if (Modes.getQuestsMode() == 2) {
            categorizedEnabled(player);
            return;
        }

        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
            noPermissionCategory(player);
            return;
        }

        final Inventory inventory = InterfacesManager.getInterfaceFirstPage(GLOBAL, player);
        player.openInventory(inventory);
    }

    /**
     * Opens the easy interface.
     */
    private void openEasy() {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
            noPermissionCategory(player);
            return;
        }
        final Inventory inventory = InterfacesManager.getInterfaceFirstPage(EASY, player);
        player.openInventory(inventory);
    }

    /**
     * Opens the medium interface.
     */
    private void openMedium() {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
            noPermissionCategory(player);
            return;
        }
        final Inventory inventory = InterfacesManager.getInterfaceFirstPage(MEDIUM, player);
        player.openInventory(inventory);
    }

    /**
     * Opens the hard interface.
     */
    private void openHard() {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
            noPermissionCategory(player);
            return;
        }
        final Inventory inventory = InterfacesManager.getInterfaceFirstPage(HARD, player);
        player.openInventory(inventory);
    }
}
