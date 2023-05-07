package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockDropItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled()) return;

        final BlockData data = event.getBlockState().getBlockData();

        // check if the dropped item is a crop
        if (data instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {

                Material material = switch (data.getMaterial()) {
                    case POTATOES -> Material.POTATO;
                    case CARROTS -> Material.CARROT;
                    case BEETROOTS -> Material.BEETROOT;
                    case COCOA -> Material.COCOA_BEANS;
                    case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
                    default -> data.getMaterial();
                };

                final List<Item> drops = event.getItems();
                int amount = 0;
                for (Item item : drops) {

                    if (item.getItemStack().getType() == material) {
                        amount += item.getItemStack().getAmount();
                    }
                }

                setPlayerQuestProgression(event.getPlayer(), new ItemStack(material), amount, QuestType.FARMING, null);
            }
        }

        // check if the dropped item is a block that can be posed
        else if (data.getMaterial().isBlock()) {
            if (Antiglitch.storeItems) {

                for (Item item : event.getItems()) {
                    final ItemStack drop = item.getItemStack();
                    final ItemMeta dropMeta = drop.getItemMeta();
                    final PersistentDataContainer pdc = dropMeta.getPersistentDataContainer();

                    pdc.set(Antiglitch.BROKEN_KEY, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                    drop.setItemMeta(dropMeta);
                }
            }
        }
    }
}
