package com.ordwen.odailyquests.api.commands.admin;

import java.util.HashMap;
import java.util.Map;

public class AdminCommandRegistry extends HashMap<String, IAdminCommand> {

    public void registerCommand(IAdminCommand handler) {
        this.put(handler.getName(), handler);
    }

    public IAdminCommand getCommandHandler(String name) {
        return this.get(name);
    }

    public Map<String, IAdminCommand> getCommandHandlers() {
        return this;
    }
}
