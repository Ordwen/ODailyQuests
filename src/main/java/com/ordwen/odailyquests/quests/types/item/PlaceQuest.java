package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlaceQuest extends ItemQuest {

    public PlaceQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "PLACE";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof BlockPlaceEvent event) {
            final Block block = event.getBlock();
            if (!this.isProtectionBypass()) {
                if (!Protection.canBuild(event.getPlayer(), block, "BLOCK_PLACE")) return false;
            }

            final ItemStack placedItem = event.getItemInHand();

            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent summoned by " + event.getPlayer().getName() + " for " + placedItem.getType() + ".");
            return super.isRequiredItem(new ItemStack(placedItem.getType()));
        }

        return false;
    }
}
