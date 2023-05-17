package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.NamespacedKey;

public class Antiglitch {

    private static boolean storePlacedBlocks = false;
    private static boolean storeBrokenBlocks = false;
    private static boolean storeDroppedItems = false;

    public static NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "broken");
    public static NamespacedKey DROPPED_BY = new NamespacedKey(ODailyQuests.INSTANCE, "dropped");

    /**
     * Set the configuration values for the anti-glitch system
     * @param storePlacedBlocks if the plugin should store the blocks that are placed by the player
     * @param storeBrokenBlocks if the plugin should store the blocks that are broken by the player
     * @param storeDroppedItems if the plugin should store the items that are dropped by the player
     */
    public static void setStoreValues(boolean storePlacedBlocks, boolean storeBrokenBlocks, boolean storeDroppedItems) {
        Antiglitch.storePlacedBlocks = storePlacedBlocks;
        Antiglitch.storeBrokenBlocks = storeBrokenBlocks;
        Antiglitch.storeDroppedItems = storeDroppedItems;
    }

    /**
     * Check if the plugin should store the blocks that are placed by the player
     * @return configuration value
     */
    public static boolean isStorePlacedBlocks() {
        return storePlacedBlocks;
    }

    /**
     * Check if the plugin should store the blocks that are broken by the player
     * @return configuration value
     */
    public static boolean isStoreBrokenBlocks() {
        return storeBrokenBlocks;
    }

    /**
     * Check if the plugin should store the items that are dropped by the player
     * @return configuration value
     */
    public static boolean isStoreDroppedItems() {
        return storeDroppedItems;
    }
}
