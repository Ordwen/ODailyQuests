package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public class OpenCommand extends AdminCommandBase {

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
                    sender.sendMessage(QuestsMessages.ERROR_INVENTORY.toString());
                    sender.sendMessage(QuestsMessages.CHECK_CONSOLE.toString());
                    return;
                }
                target.openInventory(inventory);
            } else invalidPlayer(sender);
        } else help(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            return Collections.emptyList();
        }

        return null;
    }
}
