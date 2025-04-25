package com.ordwen.odailyquests.api.commands.admin;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface AdminCommandCompleter {

    /**
     * Provides tab-completion suggestions for the command based on the current arguments.
     * <p>
     * This method is called when a player or console user presses the <kbd>TAB</kbd> key
     * while typing a command. You can use it to offer context-aware completions such as subcommands,
     * player names, or any other dynamic options.
     * <p>
     * Returning {@code null} will allow Bukkit to automatically suggest online player names.
     * Returning an empty list will disable tab completion at that argument position.
     *
     * @param sender the command sender (can be a player or the console)
     * @param args   the arguments currently entered for the command
     * @return a list of possible completions, {@code null} to let Bukkit suggest player names, or an empty list to disable suggestions
     */
    List<String> onTabComplete(CommandSender sender, String[] args);
}
