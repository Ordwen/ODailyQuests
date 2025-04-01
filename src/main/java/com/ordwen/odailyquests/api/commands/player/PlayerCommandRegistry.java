package com.ordwen.odailyquests.api.commands.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCommandRegistry extends HashMap<String, IPlayerCommand> {

    public void registerCommand(IPlayerCommand handler) {
        this.put(handler.getName(), handler);
    }

    public IPlayerCommand getCommandHandler(String name) {
        return this.get(name);
    }

    public Map<String, IPlayerCommand> getCommandHandlers() {
        return this;
    }
}
