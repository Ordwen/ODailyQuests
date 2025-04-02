package com.ordwen.odailyquests.api.commands.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCommandRegistry extends HashMap<String, PlayerCommandBase> {

    public void registerCommand(PlayerCommandBase handler) {
        this.put(handler.getName(), handler);
    }

    public PlayerCommandBase getCommandHandler(String name) {
        return this.get(name);
    }

    public Map<String, PlayerCommandBase> getCommandHandlers() {
        return this;
    }
}
