package com.ordwen.odailyquests.events.listeners.inventory.types;

import org.bukkit.Location;

public class LocationQuest extends AbstractQuest {

    final Location requiredLocation;
    final int radius;

    /**
     * Quest constructor.
     *
     * @param globalQuest      parent quest.
     * @param requiredLocation required location.
     */
    public LocationQuest(GlobalQuest globalQuest, Location requiredLocation, int radius) {
        super(globalQuest);
        this.requiredLocation = requiredLocation;
        this.radius = radius;
    }

    /**
     * Get the location required by the quest.
     *
     * @return quest location-required.
     */
    public Location getRequiredLocation() {
        return this.requiredLocation;
    }

    /**
     * Get the radius of the location required by the quest.
     *
     * @return quest location-required radius.
     */
    public int getRadius() {
        return this.radius;
    }
}
