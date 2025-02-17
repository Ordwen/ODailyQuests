package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import org.bukkit.NamespacedKey;

public class Antiglitch implements IConfigurable {

    public static final NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_broken");
    public static final NamespacedKey PLACED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_placed");
    public static final NamespacedKey DROPPED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_dropped");

    private final ConfigurationFile configurationFile;

    private boolean storePlacedBlocks;
    private boolean storeBrokenBlocks;
    private boolean storeDroppedItems;

    public Antiglitch(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        storePlacedBlocks = configurationFile.getConfigFile().getBoolean("store_placed_blocks");
        storeBrokenBlocks = configurationFile.getConfigFile().getBoolean("store_broken_blocks");
        storeDroppedItems = configurationFile.getConfigFile().getBoolean("store_dropped_items");
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

    /**
     * Check if the plugin should store the items that are dropped by the player
     *
     * @return configuration value
     */
    public static boolean isStoreDroppedItems() {
        return getInstance().storeDroppedItems;
    }
}
