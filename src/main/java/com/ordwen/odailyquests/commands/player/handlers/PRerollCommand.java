package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PRerollCommand extends PCommandHandler {

    public PRerollCommand(Player player, String[] args) {
        super(player, args);
    }

    @Override
    public void handle() {
        if (!player.hasPermission(QuestsPermissions.QUEST_REROLL.getPermission())) {
            noPermission(player);
            return;
        }

        if (args.length != 2) {
            help(player);
            return;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            help(player);
            return;
        }

        reroll(index);
    }

    /**
     * Rerolls a specific quest for a player.
     * @param index the index of the quest to reroll
     */
    private void reroll(int index) {
        final String playerName = player.getName();
        final HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (index < 1 || index > activeQuests.get(playerName).getPlayerQuests().size()) {
            invalidQuest();
            return;
        }

        if (activeQuests.containsKey(playerName)) {
            final PlayerQuests playerQuests = activeQuests.get(playerName);
            playerQuests.rerollQuest(index - 1, player);
            rerollConfirm(index, player);
        }
    }

    /**
     * Sends the confirmation message to the target player.
     *
     * @param index  the index of the quest that was rerolled
     * @param target the player who had their quest rerolled
     */
    private void rerollConfirm(int index, Player target) {
        final String msg = QuestsMessages.QUEST_REROLLED.toString();
        if (msg != null) target.sendMessage(msg.replace("%index%", String.valueOf(index)));
    }

    /**
     * Sends the invalid quest message to the sender.
     */
    protected void invalidQuest() {
        final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
        if (msg != null) player.sendMessage(msg);
    }
}
