package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class LaunchQuest extends ItemQuest {

    public LaunchQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "LAUNCH";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof ProjectileLaunchEvent event) {
            switch (event.getEntity().getType()) {
                case ENDER_PEARL -> { return super.isRequiredItem(new ItemStack(Material.ENDER_PEARL)); }
                case EGG -> { return super.isRequiredItem(new ItemStack(Material.EGG)); }
                case ARROW -> { return super.isRequiredItem(new ItemStack(Material.ARROW)); }
                case SNOWBALL -> { return super.isRequiredItem(new ItemStack(Material.SNOWBALL)); }
            }
        }
        return false;
    }
}
