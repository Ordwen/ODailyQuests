package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ConsumeQuest extends ItemQuest {

    public ConsumeQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "CONSUME";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerItemConsumeEvent event) {
            return super.isRequiredItem(event.getItem());
        }

        if(provided instanceof EntityResurrectEvent event) {

            final EntityEquipment equipment = event.getEntity().getEquipment();

            if(equipment != null) {

                final ItemStack mainHand = equipment.getItemInMainHand();
                final ItemStack offHand = equipment.getItemInOffHand();

                if(mainHand.getType() == Material.TOTEM_OF_UNDYING) {
                    return super.isRequiredItem(mainHand);
                }

                if(offHand.getType() == Material.TOTEM_OF_UNDYING) {
                    return super.isRequiredItem(offHand);
                }
            }
        }

        return false;
    }
}
