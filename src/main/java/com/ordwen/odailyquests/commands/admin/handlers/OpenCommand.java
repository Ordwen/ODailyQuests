package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.IAdminCommand;
import com.ordwen.odailyquests.commands.admin.AdminMessages;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenCommand extends AdminMessages implements IAdminCommand {

    private final PlayerQuestsInterface playerQuestsInterface;

    public OpenCommand(PlayerQuestsInterface playerQuestsInterface) {
        this.playerQuestsInterface = playerQuestsInterface;
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Bukkit.getPlayerExact(args[1]) != null) {
            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target != null) {
                final Inventory inventory = playerQuestsInterface.getPlayerQuestsInterface(target);
                if (inventory == null) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while opening the inventory.");
                    sender.sendMessage(ChatColor.RED + "Please check the console for more information.");
                    return;
                }
                target.openInventory(inventory);
            } else invalidPlayer(sender);
        } else help(sender);
    }
}
