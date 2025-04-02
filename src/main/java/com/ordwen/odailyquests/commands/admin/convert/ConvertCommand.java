package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.tools.PluginLogger;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConvertCommand extends AdminCommandBase {

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (!this.convert(args[1], args[2])) {
                sender.sendMessage(ChatColor.RED + "Conversion failed! Please check the console for more information.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Conversion successful!");
                sender.sendMessage(ChatColor.GREEN + "Please select the new storage mode in config file and restart the server to apply changes.");
            }
        } else sender.sendMessage(ChatColor.RED + "Usage: /dqa convert <old format> <new format>");
    }

    /**
     * Converts the storage format.
     *
     * @param oldFormat old storage format.
     * @param newFormat new storage format.
     * @return true if the conversion was successful, false otherwise.
     */
    public boolean convert(String oldFormat, String newFormat) {

        if (oldFormat.equalsIgnoreCase(newFormat)) {
            PluginLogger.error("The old and new format are the same.");
            return false;
        }

        if (oldFormat.equalsIgnoreCase("yaml")) {
            switch (newFormat) {
                case "MYSQL", "MySQL", "mysql" -> {
                    return new YAMLtoMySQLConverter().convert();
                }
                case "SQLITE", "SQLite", "sqlite" -> {
                    return new YAMLtoSQLiteConverter().convert();
                }
                default -> {
                    PluginLogger.error("The new format is not supported.");
                    return false;
                }
            }
        }

        PluginLogger.error("The old format is not supported.");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2 || args.length == 3) {
            return List.of("mysql", "sqlite");
        }

        return Collections.emptyList();
    }
}
