package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LaunchQuest extends ItemQuest {

    private static final Map<String, String> ENTITY_TO_MATERIAL = Map.of(
            "THROWN_EXP_BOTTLE", "EXPERIENCE_BOTTLE",
            "FIREWORK", "FIREWORK_ROCKET"
    );

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
            final String entityName = event.getEntity().getType().name();
            final String materialName = ENTITY_TO_MATERIAL.getOrDefault(entityName, entityName);

            try {
                final Material material = Material.valueOf(materialName);
                return super.isRequiredItem(new ItemStack(material), progression);
            } catch (IllegalArgumentException e) {
                // Unknown material in this context, do not progress
                return false;
            }
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
