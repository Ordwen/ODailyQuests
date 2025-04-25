package com.ordwen.odailyquests.api.commands.player;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all player subcommands in the plugin.
 * <p>
 * This class extends {@link HashMap}, where each entry maps a subcommand name to its corresponding {@link PlayerCommandBase} handler.
 * It provides utility methods to register and retrieve player commands.
 * <p>
 * Addons can use this registry to register their own player commands.
 */
public class PlayerCommandRegistry extends HashMap<String, PlayerCommandBase> {

    /**
     * Registers a new player subcommand handler.
     * <p>
     * The handler is stored using its {@link PlayerCommandBase#getName()} as the key.
     *
     * @param handler the player command handler to register
     */
    public void registerCommand(PlayerCommandBase handler) {
        this.put(handler.getName(), handler);
    }

    /**
     * Retrieves the command handler associated with the given name.
     *
     * @param name the name of the subcommand
     * @return the corresponding {@link PlayerCommandBase}, or {@code null} if not found
     */
    public PlayerCommandBase getCommandHandler(String name) {
        return this.get(name);
    }

    /**
     * Gets a map of all registered player command handlers.
     *
     * @return a map of subcommand names to their handlers
     */
    public Map<String, PlayerCommandBase> getCommandHandlers() {
        return this;
    }
}
