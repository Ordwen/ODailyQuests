package com.ordwen.odailyquests.quests.types;

import org.bukkit.Location;

public class LocationQuest extends AbstractQuest {

    final Location requiredLocation;
    final int radius;

    /**
     * Quest constructor.
     *
     * @param basicQuest      parent quest.
     * @param requiredLocation required location.
     */
    public LocationQuest(BasicQuest basicQuest, Location requiredLocation, int radius) {
        super(basicQuest);
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
