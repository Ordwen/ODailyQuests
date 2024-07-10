package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
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
            if (!Protection.canBuild(event.getPlayer(), block, "BLOCK_BREAK")) return false;

            Debugger.addDebug("BlockBreakListener: onBlockBreakEvent summoned by " + event.getPlayer().getName() + " for " + block.getType() + ".");

            Material material = switch (block.getType()) {
                case POTATOES -> Material.POTATO;
                case CARROTS -> Material.CARROT;
                case BEETROOTS -> Material.BEETROOT;
                case COCOA -> Material.COCOA_BEANS;
                case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
                default -> block.getType();
            };

            if (!material.isItem()) {
                Debugger.addDebug("BreakQuest: canProgress material is not an item: " + material);
                Debugger.addDebug("BreakQuest: cancelling event.");
                return false;
            }

            Debugger.addDebug("BreakQuest: canProgress material: " + material);
            return super.isRequiredItem(new ItemStack(material));
        }

        if (provided instanceof CustomBlockBreakEvent event) {
            return super.isRequiredItem(event.getCustomBlockItem());
        }

        return false;
    }
}
