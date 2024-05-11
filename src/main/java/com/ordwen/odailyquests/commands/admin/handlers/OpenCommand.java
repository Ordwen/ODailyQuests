package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.commands.interfaces.QuestInventory;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenCommand extends ACommandHandler {

    public OpenCommand(CommandSender sender, String[] args, QuestSystem questSystem) {
        super(sender, args, questSystem);
    }

    @Override
    public void handle() {
        if (Bukkit.getPlayerExact(args[1]) != null) {
            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target != null) {
                final QuestInventory inventory = PlayerQuestsInterface.getPlayerQuestsInterface(questSystem, target);
                if (inventory == null) {
                    sender.sendMessage(ChatColor.RED + "An error occurred while opening the inventory.");
                    sender.sendMessage(ChatColor.RED + "Please check the console for more information.");
                    return;
                }
                target.openInventory(inventory.getInventory());
            } else invalidPlayer();
        } else help();
    }
}
