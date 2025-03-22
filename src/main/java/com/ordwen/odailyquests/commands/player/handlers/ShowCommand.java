package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.commands.interfaces.QuestsInterfaces;
import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends PCommandHandler {

    private static final String PERMISSION_PREFIX = "odailyquests.";

    private final QuestsInterfaces questsInterfaces;

    public ShowCommand(QuestsInterfaces questsInterfaces, Player player, String[] args) {
        super(player, args);
        this.questsInterfaces = questsInterfaces;
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

        openCategory(args[1]);
    }

    /**
     * Opens the category interface.
     * @param category the category.
     */
    private void openCategory(String category) {
        if (!CategoriesLoader.hasCategory(category)) {
            invalidCategory(player);
            return;
        }

        if (!player.hasPermission(PERMISSION_PREFIX + category)) {
            noPermissionCategory(player);
            return;
        }

        final Inventory inventory = questsInterfaces.getInterfaceFirstPage(category, player);
        player.openInventory(inventory);
    }
}
