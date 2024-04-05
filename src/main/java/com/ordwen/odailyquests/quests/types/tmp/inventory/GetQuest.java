package com.ordwen.odailyquests.quests.types.tmp.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GetQuest extends ItemQuest {

    public GetQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "GET";
    }

    @Override
    public boolean canProgress(Event event) {
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (!super.loadParameters(section, file, index)) return false;

        /* apply Persistent Data Container to the menu item to differentiate GET quests */
        final ItemStack menuItem = super.getMenuItem();
        final ItemMeta meta = menuItem.getItemMeta();
        if (meta == null) return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_type"), PersistentDataType.STRING, "get");
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "quest_index"), PersistentDataType.INTEGER, index);
        container.set(new NamespacedKey(ODailyQuests.INSTANCE, "file_name"), PersistentDataType.STRING, file);

        menuItem.setItemMeta(meta);

        return true;
    }
}
