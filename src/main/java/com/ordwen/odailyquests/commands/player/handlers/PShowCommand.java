package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.api.commands.player.IPlayerCommand;
import com.ordwen.odailyquests.commands.interfaces.QuestsInterfaces;
import com.ordwen.odailyquests.commands.player.PlayerMessages;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PShowCommand extends PlayerMessages implements IPlayerCommand {

    private static final String PERMISSION_PREFIX = "odailyquests.";

    private final QuestsInterfaces questsInterfaces;

    public PShowCommand(QuestsInterfaces questsInterfaces) {
        this.questsInterfaces = questsInterfaces;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getPermission() {
        return "odailyquests.show";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission(getPermission())) {
            noPermission(player);
            return;
        }

        if (args.length != 2) {
            help(player);
            return;
        }

        openCategory(player, args[1]);
    }

    /**
     * Opens the category interface.
     * @param player the player who wants to open the category.
     * @param category the category.
     */
    private void openCategory(Player player, String category) {
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
