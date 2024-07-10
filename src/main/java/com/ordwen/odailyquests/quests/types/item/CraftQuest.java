package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.*;

public class CraftQuest extends ItemQuest {

    public CraftQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "CRAFT";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof CraftItemEvent event) {
            final ItemStack item;
            if (event.getRecipe() instanceof ComplexRecipe complexRecipe) {
                item = new ItemStack(Material.valueOf(complexRecipe.getKey().getKey().toUpperCase()));
            } else {
                final ItemStack result = event.getCurrentItem();
                if (result == null) return false;
                item = result.clone();
            }
            return super.isRequiredItem(item);
        }

        if (provided instanceof SmithItemEvent event) {
            final ItemStack item = event.getCurrentItem();
            return super.isRequiredItem(item);
        }

        return false;
    }
}
