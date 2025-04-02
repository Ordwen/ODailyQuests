package com.ordwen.odailyquests.api.commands.admin;

import java.util.HashMap;
import java.util.Map;

public class AdminCommandRegistry extends HashMap<String, AdminCommandBase> {

    public void registerCommand(AdminCommandBase handler) {
        this.put(handler.getName(), handler);
    }

    public AdminCommandBase getCommandHandler(String name) {
        return this.get(name);
    }

    public Map<String, AdminCommandBase> getCommandHandlers() {
        return this;
    }
}
