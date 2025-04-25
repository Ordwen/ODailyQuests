package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class PlayerCommandBase extends PlayerMessages implements PlayerCommand, PlayerCommandCompleter {

    /**
     * Provides tab-completion suggestions for the command.
     * <p>
     * This method is called automatically by Bukkit when the user presses the <kbd>TAB</kbd> key while typing the command.
     * By default, this implementation returns an empty list, which disables tab-completion.
     * <p>
     * To enable suggestions, override this method in your command class and return a list of possible completions.
     * Returning {@code null} allows Bukkit to auto-complete player names.
     *
     * @param sender the source of the command (player or console)
     * @param args   the current command arguments
     * @return a list of completions, an empty list to disable suggestions, or {@code null} to let Bukkit suggest player names
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}