package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockDropItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {

        final BlockData data = event.getBlockState().getBlockData();
        if (data instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {

                Material material = data.getMaterial();
                switch (material) {
                    case POTATOES -> material = Material.POTATO;
                    case CARROTS -> material = Material.CARROT;
                    case BEETROOTS -> material = Material.BEETROOT;
                    case COCOA -> material = Material.COCOA_BEANS;
                    case SWEET_BERRY_BUSH -> material = Material.SWEET_BERRIES;
                }

                final List<Item> drops = event.getItems();
                int amount = 0;
                for (Item item : drops) {

                    if (item.getItemStack().getType() == material) {
                        amount += item.getItemStack().getAmount();
                    }
                }

                setPlayerQuestProgression(event.getPlayer(), new ItemStack(material), amount, QuestType.FARMING);
            }
        }
    }
}
