package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.commands.admin.IAdminCommand;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.commands.admin.AdminMessages;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CompleteCommand extends AdminMessages implements IAdminCommand {

    @Override
    public String getName() {
        return "complete";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            invalidPlayer(sender);
            return;
        }

        if (args.length < 3) {
            help(sender);
            return;
        }

        int questIndex;
        try {
            questIndex = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            help(sender);
            return;
        }

        complete(sender, questIndex, target);
    }

    /**
     * Completes a quest for a player
     *
     * @param sender the command sender
     * @param questIndex the index of the quest
     * @param target     the player
     */
    private void complete(CommandSender sender, int questIndex, Player target) {
        if (questIndex >= 1 && questIndex <= QuestsPerCategory.getTotalQuestsAmount()) {
            final Map<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(target.getName()).getQuests();

            int index = 0;
            for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.entrySet()) {
                if (index != questIndex - 1) {
                    index++;
                    continue;
                }

                final AbstractQuest quest = entry.getKey();
                final Progression progression = entry.getValue();

                if (playerQuests.get(quest).isAchieved()) {
                    final String msg = QuestsMessages.QUEST_ALREADY_ACHIEVED.toString();
                    if (msg != null) sender.sendMessage(msg);
                    return;
                }

                final QuestCompletedEvent event = new QuestCompletedEvent(target, progression, quest);
                ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);

                return;
            }
            return;
        }
        invalidQuest(sender);
    }
}
