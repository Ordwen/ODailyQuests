package com.ordwen.odailyquests.quests.types.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class LocationQuest extends AbstractQuest {

    private static final String LOCATION_PATH = ".location";

    Location requiredLocation;
    int radius;

    public LocationQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "LOCATION";
    }

    @Override
    public boolean canProgress(Event provided) {
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        final ConfigurationSection locationSection = section.getConfigurationSection(LOCATION_PATH);
        if (locationSection == null) {
            PluginLogger.configurationError(file, index, LOCATION_PATH, "You need to specify a location.");
            return false;
        }

        if (!locationSection.contains(".radius")) {
            PluginLogger.configurationError(file, index, "radius", "You need to specify a radius.");
            return false;
        }

        radius = locationSection.getInt(".radius");

        final String wd = locationSection.getString(".world");
        if (wd == null) {
            PluginLogger.configurationError(file, index, "world", "You need to specify a world.");
            return false;
        }

        final World world = Bukkit.getWorld(wd);
        if (world == null) {
            PluginLogger.configurationError(file, index, "world", "The world specified in the location is invalid.");
            return false;
        }

        final int x = locationSection.getInt(".x");
        final int y = locationSection.getInt(".y");
        final int z = locationSection.getInt(".z");

        requiredLocation = new Location(world, x, y, z);

        /* apply Persistent Data Container to the menu item to differentiate LOCATION quests */
        final ItemStack menuItem = super.getMenuItem();
        final ItemMeta meta = menuItem.getItemMeta();
        if (meta == null) return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_type"), PersistentDataType.STRING, LOCATION_PATH);
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_index"), PersistentDataType.INTEGER, index);
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "file_name"), PersistentDataType.STRING, file);

        menuItem.setItemMeta(meta);

        return true;
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
