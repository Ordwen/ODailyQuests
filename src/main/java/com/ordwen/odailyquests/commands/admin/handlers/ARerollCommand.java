package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ARerollCommand extends AdminCommandBase {

    @Override
    public String getName() {
        return "reroll";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[1] != null && args[2] != null) {

            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            int index;
            try {
                index = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                help(sender);
                return;
            }

            reroll(sender, target, index);

        } else help(sender);
    }

    /**
     * Rerolls a specific quest for a player.
     *
     * @param sender the command sender
     * @param target the player to reroll the quest for
     * @param index  the index of the quest to reroll
     */
    private void reroll(CommandSender sender, Player target, int index) {
        final String playerName = target.getName();
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();

        if (index < 1 || index > activeQuests.get(playerName).getQuests().size()) {
            invalidQuest(sender);
            return;
        }

        if (activeQuests.containsKey(playerName)) {
            final PlayerQuests playerQuests = activeQuests.get(playerName);
            if (playerQuests.rerollQuest(index - 1, target)) {
                confirmationToSender(sender, index, playerName);
                confirmationToTarget(index, target);
            }
        }
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param sender the command sender
     * @param index  the index of the quest that was rerolled
     * @param target the name of the player who had their quest rerolled
     */
    private void confirmationToSender(CommandSender sender, int index, String target) {
        final String msg = QuestsMessages.QUEST_REROLLED_ADMIN.toString();
        if (msg != null) {
            sender.sendMessage(msg
                    .replace("%index%", String.valueOf(index))
                    .replace("%target%", target));
        }
    }

    /**
     * Sends the confirmation message to the target player.
     *
     * @param index  the index of the quest that was rerolled
     * @param target the player who had their quest rerolled
     */
    private void confirmationToTarget(int index, Player target) {
        final String msg = QuestsMessages.QUEST_REROLLED.toString();
        if (msg != null) target.sendMessage(msg.replace("%index%", String.valueOf(index)));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 3) {
            List<String> questNumbers = new ArrayList<>();
            for (int i = 1; i <= QuestsPerCategory.getTotalQuestsAmount(); i++) {
                questNumbers.add(String.valueOf(i));
            }
            return questNumbers;
        }

        if (args.length >= 4) {
            return Collections.emptyList();
        }

        return null;
    }
}
