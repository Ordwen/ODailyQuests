package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommands implements CommandExecutor {

    private final ConfigurationFiles configurationFiles;

    /**
     * ConfigurationFiles class instance constructor.
     *
     * @param configurationFiles files class.
     */
    public PlayerCommands(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission(QuestsPermissions.QUEST_USE.getPermission())) {
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "show":
                            if (sender.hasPermission(QuestsPermissions.QUEST_SHOW.getPermission())) {
                                if (args.length == 2) {
                                    switch (args[1]) {
                                        case "global":
                                            if (configurationFiles.getConfigFile().getInt("quests_mode") != 1) {
                                                sender.sendMessage(QuestsMessages.CATEGORIZED_ENABLED.toString());
                                            } else if (sender.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
                                                ((Player) sender).openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsInterfaceFirstPage());
                                            } else sender.sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
                                            break;
                                        case "easy":
                                            if (configurationFiles.getConfigFile().getInt("quests_mode") != 2) {
                                                sender.sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
                                            } else {
                                                if (sender.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
                                                    ((Player) sender).openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInterfaceFirstPage());
                                                } else
                                                    sender.sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
                                            }
                                            break;
                                        case "medium":
                                            if (configurationFiles.getConfigFile().getInt("quests_mode") != 2) {
                                                sender.sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
                                            } else {
                                                if (sender.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
                                                    ((Player) sender).openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInterfaceFirstPage());
                                                } else
                                                    sender.sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
                                            }
                                            break;
                                        case "hard":
                                            if (configurationFiles.getConfigFile().getInt("quests_mode") != 2) {
                                                sender.sendMessage(QuestsMessages.CATEGORIZED_DISABLED.toString());
                                            } else {
                                                if (sender.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
                                                    ((Player) sender).openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInterfaceFirstPage());
                                                } else
                                                    sender.sendMessage(QuestsMessages.NO_PERMISSION_CATEGORY.toString());
                                            }
                                            break;
                                        default:
                                            sender.sendMessage(QuestsMessages.INVALID_CATEGORY.toString());
                                            break;
                                    }
                                } else sender.sendMessage(QuestsMessages.PLAYER_HELP.toString());
                            } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
                            break;
                        case "me":
                            ((Player) sender).openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(sender.getName()));
                            break;
                        case "help":
                        default:
                            sender.sendMessage(QuestsMessages.PLAYER_HELP.toString());
                            break;
                    }
                } else
                    ((Player) sender).openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(sender.getName()));
            } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
        }
        return false;
    }
}
