package com.ordwen.odailyquests.api.commands.admin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for all admin subcommands in the plugin.
 * <p>
 * This class extends {@link HashMap}, where each entry maps a subcommand name to its corresponding {@link AdminCommandBase} handler.
 * It provides utility methods to register and retrieve admin commands.
 * <p>
 * Addons can use this registry to register their own administrative commands.
 */
public class AdminCommandRegistry {

    private final Map<String, AdminCommandBase> handlers = new HashMap<>();

    /**
     * Registers a new admin subcommand handler.
     * <p>
     * The handler is stored using its {@link AdminCommandBase#getName()} as the key.
     *
     * @param handler the admin command handler to register
     */
    public void registerCommand(AdminCommandBase handler) {
        handlers.put(handler.getName(), handler);
    }

    /**
     * Retrieves the command handler associated with the given name.
     *
     * @param name the name of the subcommand
     * @return the corresponding {@link AdminCommandBase}, or {@code null} if not found
     */
    public AdminCommandBase getCommandHandler(String name) {
        return handlers.get(name);
    }

    /**
     * Gets a map of all registered admin command handlers.
     *
     * @return a map of subcommand names to their handlers
     */
    public Collection<AdminCommandBase> getCommandHandlers() {
        return handlers.values();
    }

    /**
     * Gets a set of all registered command names.
     *
     *
     * @return a set of subcommand names
     */
    public Set<String> getCommandNames() {
        return handlers.keySet();
    }
}
