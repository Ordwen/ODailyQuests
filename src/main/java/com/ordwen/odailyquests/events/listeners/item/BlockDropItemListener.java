package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.enums.QuestType;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockDropItemListener extends AbstractItemChecker implements Listener {

    /* store items that can be farmed but are not counted as crops or ageables */
    private final Set<Material> farmableItems = new HashSet<>(
            Arrays.asList(
                    Material.SUGAR_CANE,
                    Material.CACTUS,
                    Material.PUMPKIN,
                    Material.MELON,
                    Material.MELON_SLICE
            ));

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled()) return;

        final BlockData data = event.getBlockState().getBlockData();
        final Material dataMaterial = data.getMaterial();

        // check if the dropped item figure in the non-crops items list
        if (farmableItems.contains(data.getMaterial())) {

            final AtomicBoolean valid = new AtomicBoolean(true);

            // check if the block have been placed by the player
            if (dataMaterial.isBlock()) {
                if (Antiglitch.isStorePlacedBlocks()) {
                    if (!event.getBlock().getMetadata("odailyquests:placed").isEmpty()) {
                        valid.set(false);
                    }
                }
            }

            if (!valid.get()) {
                return;
            }

            final List<Item> drops = event.getItems();
            if (drops.isEmpty()) return;

            boolean isSlicedMelon = false;
            int amount = 0;
            for (Item item : drops) {
                final Material itemMaterial = item.getItemStack().getType();
                if (itemMaterial == Material.MELON_SLICE) isSlicedMelon = true;

                if (isSlicedMelon) {
                    if (itemMaterial == Material.MELON_SLICE) {
                        amount += item.getItemStack().getAmount();
                    }
                }

                else if (itemMaterial == dataMaterial) {
                    amount += item.getItemStack().getAmount();
                }
            }

            if (isSlicedMelon) {
                setPlayerQuestProgression(event.getPlayer(), new ItemStack(Material.MELON_SLICE), amount, QuestType.FARMING, null);
            }
            else {
                setPlayerQuestProgression(event.getPlayer(), new ItemStack(dataMaterial), amount, QuestType.FARMING, null);
            }
        }

        // check if the dropped item is a crop
        else if (data instanceof Ageable ageable) {

            if (ageable.getAge() == ageable.getMaximumAge()) {

                Material material = switch (data.getMaterial()) {
                    case POTATOES -> Material.POTATO;
                    case CARROTS -> Material.CARROT;
                    case BEETROOTS -> Material.BEETROOT;
                    case COCOA -> Material.COCOA_BEANS;
                    case SWEET_BERRY_BUSH -> Material.SWEET_BERRIES;
                    default -> dataMaterial;
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
        if (dataMaterial.isBlock()) {
            if (Antiglitch.isStoreBrokenBlocks()) {

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
