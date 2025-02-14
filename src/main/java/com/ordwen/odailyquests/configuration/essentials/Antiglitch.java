package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.NamespacedKey;

public class Antiglitch {

    private Antiglitch() {}

    private static boolean storePlacedBlocks = false;
    private static boolean storeBrokenBlocks = false;
    private static boolean storeDroppedItems = false;

    public static final NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_broken");
    public static final NamespacedKey PLACED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_placed");
    public static final NamespacedKey DROPPED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_dropped");

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
