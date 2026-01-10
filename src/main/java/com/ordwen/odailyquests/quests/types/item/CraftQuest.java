package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import com.ordwen.odailyquests.tools.PluginUtils;
import net.Indyuce.mmoitems.api.event.CraftMMOItemEvent;
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
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof CraftItemEvent event) {
            final ItemStack item;
            if (event.getRecipe() instanceof ComplexRecipe complexRecipe) {
                item = new ItemStack(Material.valueOf(complexRecipe.getKey().getKey().toUpperCase()));
            } else {
                final ItemStack result = event.getCurrentItem();
                if (result == null) return false;
                item = result.clone();
            }
            return super.isRequiredItem(item, progression);
        }

        if (provided instanceof SmithItemEvent event) {
            final ItemStack item = event.getCurrentItem();
            return super.isRequiredItem(item, progression);
        }

        if (PluginUtils.isPluginEnabled("MMOItems") && provided instanceof CraftMMOItemEvent event) {
            final ItemStack item = event.getResult();
            return  super.isRequiredItem(item, progression);
        }

        return false;
    }
}
