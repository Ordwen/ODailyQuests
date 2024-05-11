package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.commands.interfaces.QuestInventory;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends ACommandHandler {

    public ShowCommand(CommandSender sender, String[] args, QuestSystem questSystem) {
        super(sender, args, questSystem);
    }


    @Override
    public void handle() {
        if (sender instanceof Player player) {
            final Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {
                invalidPlayer();
                return;
            }

            final QuestInventory inventory = PlayerQuestsInterface.getPlayerQuestsInterface(questSystem, target);
            if (inventory == null) {
                player.sendMessage(ChatColor.RED + "An error occurred while opening the inventory.");
                player.sendMessage(ChatColor.RED + "Please check the console for more information.");
                return;
            }

            player.openInventory(inventory.getInventory());

        } else {
            final String msg = QuestsMessages.PLAYER_ONLY.toString();
            if (msg != null) sender.sendMessage(msg);
        }
    }
}
