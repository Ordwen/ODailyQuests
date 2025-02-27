package com.ordwen.odailyquests.commands.interfaces.playerinterface.items;

import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.ItemUtils;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Buttons {

    /* instance */
    private final ConfigurationFile configurationFile;

    /* init items */
    private ItemStack previous;
    private ItemStack next;

    /**
     * Constructor.
     *
     * @param configurationFile configuration files class.
     */
    public Buttons(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * Load all items.
     */
    public void initItems() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("interfaces");

        if (section == null) {
            PluginLogger.warn("Impossible to load buttons because the interfaces section is missing in the configuration file.");
            return;
        }

        initPreviousButton(section);
        initNextButton(section);
    }

    /**
     * Init previous button.
     */
    private void initPreviousButton(ConfigurationSection section) {
        previous = ItemUtils.getCustomHead("a2f0425d64fdc8992928d608109810c1251fe243d60d175bed427c651cbe");
        final ItemMeta previousMeta = previous.getItemMeta();
        if (previousMeta == null) return;

        previousMeta.setDisplayName(ColorConvert.convertColorCode(section.getString(".previous_item_name")));
        previous.setItemMeta(previousMeta);
    }

    /**
     * Init next button.
     */
    private void initNextButton(ConfigurationSection section) {
        next = ItemUtils.getCustomHead("6d865aae2746a9b8e9a4fe629fb08d18d0a9251e5ccbe5fa7051f53eab9b94");
        final ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta == null) return;

        nextMeta.setDisplayName(ColorConvert.convertColorCode(section.getString(".next_item_name")));
        next.setItemMeta(nextMeta);
    }

    /**
     * Get previous button.
     *
     * @return previous button.
     */
    public ItemStack getPreviousButton() {
        return previous;
    }

    /**
     * Get next button.
     *
     * @return next button.
     */
    public ItemStack getNextButton() {
        return next;
    }
}
