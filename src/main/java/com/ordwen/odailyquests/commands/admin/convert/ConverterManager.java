package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.ACommandHandler;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConverterManager extends ACommandHandler {

    final ODailyQuests oDailyQuests;

    public ConverterManager(CommandSender sender, String[] args) {
        super(sender, args);
        this.oDailyQuests = ODailyQuests.INSTANCE;
    }

    /**
     * Handles the conversion of the storage format.
     */
    @Override
    public void handle() {
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

        switch (oldFormat) {
            case "YAML", "yaml" -> {
                switch (newFormat) {
                    case "MySQL", "mysql" -> {
                        return new YAMLtoMySQLConverter().convert(oDailyQuests);
                    }
                    case "H2", "h2" -> {
                        return new YAMLtoH2Converter().convert(oDailyQuests);
                    }
                    default -> {
                        PluginLogger.error("The new format is not supported.");
                        return false;
                    }
                }
            }
            default -> {
                PluginLogger.error("The old format is not supported.");
                return false;
            }
        }
    }
}
