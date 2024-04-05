package com.ordwen.odailyquests.quests.types.tmp.item;

import com.ordwen.odailyquests.quests.types.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakQuest extends ItemQuest {

    public BreakQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "BREAK";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof BlockBreakEvent event) {
            final Block block = event.getBlock();

            Material material = switch (block.getType()) {
                case POTATOES -> Material.POTATO;
                case CARROTS -> Material.CARROT;
                case BEETROOTS -> Material.BEETROOT;
                case COCOA -> Material.COCOA_BEANS;
                case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
                default -> block.getType();
            };

            return super.isRequiredItem(new ItemStack(material));
        }

        return false;
    }
}
