package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.api.commands.player.PlayerCommandBase;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PRerollCommand extends PlayerCommandBase {

    @Override
    public String getName() {
        return "reroll";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_PLAYER_REROLL.get();
    }

    @Override
    public void execute(Player player, String[] args) {
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

        reroll(player, index);
    }

    /**
     * Rerolls a specific quest for a player.
     * @param player the player who wants to reroll the quest
     * @param index the index of the quest to reroll
     */
    private void reroll(Player player, int index) {
        final String playerName = player.getName();
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (index < 1 || index > activeQuests.get(playerName).getQuests().size()) {
            invalidQuest(player);
            return;
        }

        if (activeQuests.containsKey(playerName)) {
            final PlayerQuests playerQuests = activeQuests.get(playerName);
            if (playerQuests.rerollQuest(index - 1, player)) {
                rerollConfirm(index, player);
            }
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
    protected void invalidQuest(Player player) {
        final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
        if (msg != null) player.sendMessage(msg);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> questNumbers = new ArrayList<>();
            for (int i = 1; i <= QuestsPerCategory.getTotalQuestsAmount(); i++) {
                questNumbers.add(String.valueOf(i));
            }
            return questNumbers;
        }

        return Collections.emptyList();
    }
}
