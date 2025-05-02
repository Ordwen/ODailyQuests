package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
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
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof PlayerHarvestBlockEvent event) {
            if (!this.isProtectionBypass() && !Protection.canBuild(event.getPlayer(), event.getHarvestedBlock(), "BLOCK_BREAK"))
                return false;

            return super.isRequiredItem(current, progression);
        }

        if (provided instanceof BlockDropItemEvent event) {
            // check if the broken block is a container
            if (event.getBlockState() instanceof InventoryHolder) {
                Debugger.write("FarmingQuest:canProgress: Block is a container.");
                return false;
            }

            if (!this.isProtectionBypass() && !Protection.canBuild(event.getPlayer(), event.getBlock(), "BLOCK_BREAK"))
                return false;

            return super.isRequiredItem(current, progression);
        }

        if (provided instanceof BlockBreakEvent event) {
            if (!this.isProtectionBypass() && !Protection.canBuild(event.getPlayer(), event.getBlock(), "BLOCK_BREAK"))
                return false;

            String blockType = event.getBlock().getType().name();
            if (blockType.endsWith("_PLANT")) {
                blockType = blockType.substring(0, blockType.length() - 6);
            }

            Debugger.write("FarmingQuest:canProgress: Potential vertical plant. Checking for type " + event.getBlock().getType() + ".");
            return super.isRequiredItem(new ItemStack(Material.valueOf(blockType)), progression);
        }

        return false;
    }

    public static void setCurrent(ItemStack current) {
        FarmingQuest.current = current;
    }
}
