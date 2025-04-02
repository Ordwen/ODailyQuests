package com.ordwen.odailyquests.api.commands.player;

import org.bukkit.entity.Player;

public interface PlayerCommand {
    void execute(Player player, String[] args);
    String getName();
    String getPermission();
}
