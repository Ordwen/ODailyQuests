package com.ordwen.odailyquests.api.commands.admin;

import org.bukkit.command.CommandSender;

/**
 * Represents an administrative subcommand that can be executed through the plugin's admin command system.
 * <p>
 * Implementing classes must define the execution logic, the name of the subcommand, and the required permission.
 * These commands are typically registered in an {@code AdminCommandRegistry}.
 */
public interface AdminCommand {

    /**
     * Executes the command logic when the command is invoked.
     *
     * @param sender the command sender (can be a player or the console)
     * @param args   the arguments passed to the command
     */
    void execute(CommandSender sender, String[] args);

    /**
     * Gets the name of the command. This is used as the subcommand identifier (e.g., {@code /command <name>}).
     *
     * @return the subcommand name
     */
    String getName();

    /**
     * Gets the required permission to execute the command.
     *
     * @return the permission node as a string
     */
    String getPermission();
}

