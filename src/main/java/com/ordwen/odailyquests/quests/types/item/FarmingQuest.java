package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class FarmingQuest extends ItemQuest {

    private static ItemStack current;

    public FarmingQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "FARMING";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerHarvestBlockEvent event) {
            if (!Protection.canBuild(event.getPlayer(), event.getHarvestedBlock(), "BLOCK_BREAK")) return false;
            return super.isRequiredItem(current);
        }

        if (provided instanceof BlockDropItemEvent event) {
            // check if the broken block is a container
            if (event.getBlockState() instanceof InventoryHolder) {
                Debugger.addDebug("FarmingQuest:canProgress: Block is a container.");
                return false;
            }

            if (!Protection.canBuild(event.getPlayer(), event.getBlock(),"BLOCK_BREAK")) return false;
            return super.isRequiredItem(current);
        }

        return false;
    }

    public static void setCurrent(ItemStack current) {
        FarmingQuest.current = current;
    }
}
