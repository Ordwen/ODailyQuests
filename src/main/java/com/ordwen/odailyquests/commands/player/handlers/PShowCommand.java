package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.api.commands.player.PlayerCommandBase;
import com.ordwen.odailyquests.commands.interfaces.QuestsInterfaces;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PShowCommand extends PlayerCommandBase {

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
        return QuestsPermissions.QUESTS_PLAYER_SHOW.getPermission();
    }

    @Override
    public void execute(Player player, String[] args) {
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
        if (inventory == null) {
            player.sendMessage(ChatColor.RED + "A configuration error prevents the interface from being displayed. Please inform an administrator.");
            return;
        }

        player.openInventory(inventory);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> categories = new ArrayList<>(CategoriesLoader.getAllCategories().keySet());
            Collections.sort(categories);
            return categories;
        }

        return Collections.emptyList();
    }
}
