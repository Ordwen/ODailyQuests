package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CompleteCommand extends ACommandHandler {

    public CompleteCommand(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void handle() {

        final Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            invalidPlayer();
            return;
        }

        if (args.length < 3) {
            help();
            return;
        }

        int questIndex;
        try {
            questIndex = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            help();
            return;
        }

        complete(questIndex, target);
    }

    /**
     * Completes a quest for a player
     *
     * @param questIndex the index of the quest
     * @param target     the player
     */
    private void complete(int questIndex, Player target) {
        if (questIndex >= 1 && questIndex <= QuestsAmount.getQuestsAmount()) {
            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(args[1]).getPlayerQuests();

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
        invalidQuest();
    }
}
