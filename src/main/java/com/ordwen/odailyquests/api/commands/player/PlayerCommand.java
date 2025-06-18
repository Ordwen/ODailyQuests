package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.entity.Player;

/**
 * Represents a player subcommand that can be executed through the plugin's player command system.
 * <p>
 * Implementing classes must define the command logic, the name of the subcommand, and the required permission.
 * These commands are intended to be used by players only (not the console), and are typically registered
 * in the {@code PlayerCommandRegistry}.
 */
public interface PlayerCommand {

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

    /**
     * Executes the command logic when the command is invoked by a player.
     *
     * @param player the player executing the command
     * @param args   the arguments passed to the command
     */
    void execute(Player player, String[] args);
}
