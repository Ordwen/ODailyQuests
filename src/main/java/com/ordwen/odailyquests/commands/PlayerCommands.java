package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("quests")) {
            if (sender instanceof Player) {
                if (sender.hasPermission(QuestsPermissions.QUEST_USE.getPermission())) {
                    if (args.length >= 1) {
                        switch (args[0]) {
                            case "show":
                                if (args.length == 2) {
                                    switch (args[1]) {
                                        case "global":
                                            ((Player) sender).openInventory(GlobalQuestsInterface.getGlobalQuestsInventory());
                                            break;
                                        case "easy":
                                            break;
                                        case "medium":
                                            break;
                                        case "hard":
                                            break;
                                        default:
                                            sender.sendMessage(QuestsMessages.INVALID_CATEGORY.getMessage());
                                            break;
                                    }
                                } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.getMessage());
                                break;
                            case "me":
                                break;
                            default:
                                sender.sendMessage(QuestsMessages.INVALID_SYNTAX.getMessage());
                                break;
                        }
                    } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.getMessage());

                } else sender.sendMessage(QuestsMessages.NO_PERMISSION.getMessage());
            }
        }
        return false;
    }
}
