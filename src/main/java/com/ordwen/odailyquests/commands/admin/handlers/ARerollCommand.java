package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ARerollCommand extends ACommandHandler {

    public ARerollCommand(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void handle() {
        if (args.length >= 3 && args[1] != null && args[2] != null) {

            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                invalidPlayer();
                return;
            }

            int index;
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                help();
                return;
            }

            reroll(target, index);

        } else help();
    }

    /**
     * Rerolls a specific quest for a player.
     *
     * @param target the player to reroll the quest for
     * @param index  the index of the quest to reroll
     */
    private void reroll(Player target, int index) {
        final String playerName = target.getName();
        final HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (activeQuests.containsKey(playerName)) {
            final PlayerQuests playerQuests = activeQuests.get(playerName);
            playerQuests.rerollQuest(target.getName(), index - 1);
            sender.sendMessage("§aQuest number " + index + "rerolled for " + playerName + ".");
            target.sendMessage("§aYour quest number " + index + " has been rerolled.");
        }
    }
}
