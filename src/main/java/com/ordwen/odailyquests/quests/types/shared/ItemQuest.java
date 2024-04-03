package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemQuest extends AbstractQuest {

    private final List<ItemStack> requiredItems;
    private final boolean ignoreNbt = false;

    /**
     * Quest constructor.
     *
     * @param base parent quest.
     */
    public ItemQuest(BasicQuest base) {
        super(base);
        this.requiredItems = new ArrayList<>();
    }

    public boolean isRequiredItem(ItemStack provided) {
        if (requiredItems == null) return true;

        for (ItemStack item : requiredItems) {

            if (ignoreNbt && item.getType() == provided.getType()) {
                return true;
            }

            if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                    if (item.getType() == provided.getType() && item.getItemMeta().getCustomModelData() == provided.getItemMeta().getCustomModelData()) {
                        return true;
                    }
                }
            }

            if (item.isSimilar(provided)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        if (!section.contains(".required_item")) return true;

        // TO DO loadItemQuest QuestsLoader

        return true;
    }
}
