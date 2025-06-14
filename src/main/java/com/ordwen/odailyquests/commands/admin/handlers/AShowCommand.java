package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public class AShowCommand extends AdminCommandBase {

    private final PlayerQuestsInterface playerQuestsInterface;

    public AShowCommand(PlayerQuestsInterface playerQuestsInterface) {
        this.playerQuestsInterface = playerQuestsInterface;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            final Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            final Inventory inventory = playerQuestsInterface.getPlayerQuestsInterface(target);
            if (inventory == null) {
                player.sendMessage(QuestsMessages.ERROR_INVENTORY.toString());
                player.sendMessage(QuestsMessages.CHECK_CONSOLE.toString());
                return;
            }

            player.openInventory(inventory);

        } else {
            final String msg = QuestsMessages.PLAYER_ONLY.toString();
            if (msg != null) sender.sendMessage(msg);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            return Collections.emptyList();
        }

        return null;
    }
}
