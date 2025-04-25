package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Interface for providing tab-completion suggestions for player commands.
 * <p>
 * This interface is used to offer context-aware tab-completion for commands in Minecraft.
 * Implementing classes should define the {@link #onTabComplete(CommandSender, String[])} method
 * to provide custom completions for commands, such as subcommands, player names, or other dynamic options.
 */
public interface PlayerCommandCompleter {

    /**
     * Provides tab-completion suggestions for the command.
     * <p>
     * This method is invoked by Bukkit when the user presses the <kbd>TAB</kbd> key while typing the command.
     * It allows you to offer context-aware completions, such as subcommands, player names, or other dynamic options.
     * <p>
     * Returning {@code null} will let Bukkit suggest player names automatically.
     * Returning an empty list disables tab-completion suggestions for the current argument.
     *
     * @param sender the command sender (can be a player or the console)
     * @param args   the current arguments entered for the command
     * @return a list of possible completions, or an empty list if no completions are available, or {@code null} to allow Bukkit to automatically complete player names
     */
    List<String> onTabComplete(CommandSender sender, String[] args);
}
