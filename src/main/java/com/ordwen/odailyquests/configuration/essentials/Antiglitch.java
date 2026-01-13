package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import org.bukkit.NamespacedKey;

public class Antiglitch implements IConfigurable {

    public static final NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_broken");
    public static final NamespacedKey PLACED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_placed");
    public static final NamespacedKey DROPPED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_dropped");
    public static final NamespacedKey DROP_UNTIL_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_drop_until");

    private final ConfigurationFile configurationFile;

    private boolean storePlacedBlocks;

    private boolean storeBrokenBlocks;
    private int brokenBlocksPlaceWindowSeconds;
    private int brokenBlocksMaxMaterialsPerPlayer;

    private boolean storeDroppedItems;
    private int droppedItemsTtlSeconds;

    public Antiglitch(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        storePlacedBlocks = configurationFile.getConfig().getBoolean("store_placed_blocks");

        storeBrokenBlocks = configurationFile.getConfig().getBoolean("store_broken_blocks");
        brokenBlocksPlaceWindowSeconds = Math.max(1, configurationFile.getConfig().getInt("broken_blocks_place_window_seconds", 20));
        brokenBlocksMaxMaterialsPerPlayer = Math.max(8, configurationFile.getConfig().getInt("broken_blocks_max_materials_per_player", 32));

        storeDroppedItems = configurationFile.getConfig().getBoolean("store_dropped_items");
        droppedItemsTtlSeconds = Math.max(1, configurationFile.getConfig().getInt("dropped_items_ttl_seconds", 10));
    }

    private static Antiglitch getInstance() {
        return ConfigFactory.getConfig(Antiglitch.class);
    }

    /**
     * Check if the plugin should store the blocks that are placed by the player
     *
     * @return configuration value
     */
    public static boolean isStorePlacedBlocks() {
        return getInstance().storePlacedBlocks;
    }

    /**
     * Check if the plugin should store the blocks that are broken by the player
     *
     * @return configuration value
     */
    public static boolean isStoreBrokenBlocks() {
        return getInstance().storeBrokenBlocks;
    }

    public static long getBrokenBlocksPlaceWindowMillis() {
        return getInstance().brokenBlocksPlaceWindowSeconds * 1000L;
    }

    public static int getBrokenBlocksMaxMaterialsPerPlayer() {
        return getInstance().brokenBlocksMaxMaterialsPerPlayer;
    }

    /**
     * Check if the plugin should store the items that are dropped by the player
     *
     * @return configuration value
     */
    public static boolean isStoreDroppedItems() {
        return getInstance().storeDroppedItems;
    }

    public static long getDroppedItemsTtlMillis() {
        return getInstance().droppedItemsTtlSeconds * 1000L;
    }
}
