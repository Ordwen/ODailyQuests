package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.NamespacedKey;

public class Antiglitch implements IConfigurable {

    public static final NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_broken");
    public static final NamespacedKey PLACED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_placed");
    public static final NamespacedKey DROPPED_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "odq_dropped");

    private final ConfigurationFiles configurationFiles;

    private boolean storePlacedBlocks = false;
    private boolean storeBrokenBlocks = false;
    private boolean storeDroppedItems = false;

    public Antiglitch(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        storePlacedBlocks = configurationFiles.getConfigFile().getBoolean("store_placed_blocks");
        storeBrokenBlocks = configurationFiles.getConfigFile().getBoolean("store_broken_blocks");
        storeDroppedItems = configurationFiles.getConfigFile().getBoolean("store_dropped_items");
    }

    public static Antiglitch getInstance() {
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
