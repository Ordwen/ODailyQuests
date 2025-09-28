package com.ordwen.odailyquests.quests.player.progression.clickable.commands;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestCommand;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import com.ordwen.odailyquests.quests.types.AbstractQuest;

public class ManualCompletionQuestCommand extends QuestCommand<AbstractQuest> {

    public ManualCompletionQuestCommand(QuestContext context, Progression progression, AbstractQuest quest) {
        super(context, progression, quest);
    }

    @Override
    public void execute() {
        if (progression.getAdvancement() < progression.getRequiredAmount()) {
            return;
        }

        completeQuest();
    }
}
