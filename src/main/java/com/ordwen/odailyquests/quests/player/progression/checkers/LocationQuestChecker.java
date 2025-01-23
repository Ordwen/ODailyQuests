package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.inventory.LocationQuest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationQuestChecker extends QuestChecker<LocationQuest> {

    public LocationQuestChecker(Player player, Progression progression, LocationQuest quest) {
        super(player, progression, quest);
    }

    /**
     * Validate LOCATION quest type.
     */
    @Override
    public void validateAndComplete() {
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
