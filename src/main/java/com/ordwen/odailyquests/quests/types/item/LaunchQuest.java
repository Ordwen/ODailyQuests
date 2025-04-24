package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof ProjectileLaunchEvent event) {
            final EntityType entityType = event.getEntity().getType();
            final ItemStack requiredItem;

            switch (entityType) {
                case ENDER_PEARL -> requiredItem = new ItemStack(Material.ENDER_PEARL);
                case EGG -> requiredItem = new ItemStack(Material.EGG);
                case ARROW -> requiredItem = new ItemStack(Material.ARROW);
                case SNOWBALL -> requiredItem = new ItemStack(Material.SNOWBALL);
                case THROWN_EXP_BOTTLE -> requiredItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
                case FIREWORK -> requiredItem = new ItemStack(Material.FIREWORK_ROCKET);
                default -> {
                    return false;
                }
            }

            return super.isRequiredItem(requiredItem, progression);
        }

        if (provided instanceof PlayerInteractEvent interactEvent) {
            final ItemStack item = interactEvent.getItem();
            if (item != null && item.getType() == Material.FIREWORK_ROCKET && interactEvent.getPlayer().isGliding()) {
                return super.isRequiredItem(item, progression);
            }
        }

        return false;
    }


}
