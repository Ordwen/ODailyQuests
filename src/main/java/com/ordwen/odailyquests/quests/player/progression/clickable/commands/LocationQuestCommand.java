package com.ordwen.odailyquests.quests.player.progression.clickable.commands;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestCommand;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import com.ordwen.odailyquests.quests.types.inventory.LocationQuest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationQuestCommand extends QuestCommand<LocationQuest> {

    public LocationQuestCommand(QuestContext context, Progression progression, LocationQuest quest) {
        super(context, progression, quest);
    }

    /**
     * Validate LOCATION quest type.
     */
    @Override
    public void execute() {
        final var player = context.getPlayer();
        if (!quest.isAllowedToProgress(player, quest)) return;

        final Location requiredLocation = quest.getRequiredLocation();
        final World requiredWorld = requiredLocation.getWorld();

        if (requiredWorld != null && !requiredWorld.equals(player.getLocation().getWorld())) {
            sendMessage(QuestsMessages.BAD_WORLD_LOCATION);
            return;
        }

        double distance = player.getLocation().distance(requiredLocation);
        if (distance <= quest.getRadius()) {
            final QuestCompletedEvent event = new QuestCompletedEvent(player, progression, quest);
            Bukkit.getPluginManager().callEvent(event);

            player.closeInventory();
        } else {
            sendMessage(QuestsMessages.TOO_FAR_FROM_LOCATION);
        }
    }
}
