package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResetCommand extends AdminCommandBase {

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[1] != null && args[2] != null) {

            final Player target = getTargetPlayer(sender, args[2]);
            if (target == null) {
                return;
            }

            switch (args[1]) {
                case "quests" -> quests(sender, target);
                case "total" -> total(sender, target);
                default -> help(sender);
            }

        } else help(sender);
    }

    /**
     * Resets the current active quests of the player.
     * @param sender the command sender
     * @param target the player to reset
     */
    public void quests(CommandSender sender, Player target) {
        final String playerName = target.getName();
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        int totalAchievedQuests = activeQuests.get(playerName).getTotalAchievedQuests();
        QuestLoaderUtils.loadNewPlayerQuests(playerName, QuestsManager.getActiveQuests(), totalAchievedQuests);

        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));
    }

    /**
     * Resets the total amount of quests achieved by the player.
     * @param sender the command sender
     * @param target the player to reset
     */
    public void total(CommandSender sender, Player target) {
        ODailyQuestsAPI.getPlayerQuests(target.getName()).setTotalAchievedQuests(0);

        String msg = QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

        msg = QuestsMessages.TOTAL_AMOUNT_RESET.getMessage(target);
        if (msg != null) target.sendMessage(msg);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("quests", "total");
        }

        if (args.length >= 4) {
            return Collections.emptyList();
        }

        return null;
    }
}
