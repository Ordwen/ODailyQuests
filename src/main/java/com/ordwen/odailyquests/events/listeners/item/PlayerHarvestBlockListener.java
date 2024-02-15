package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerHarvestBlockListener extends AbstractItemChecker implements Listener {

    /* store items that can be farmed but are not counted as crops or ageables */
    private final Set<String> farmableItems = new HashSet<>(
            List.of(
                    "CAVE_VINES_PLANT"
            ));

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        if (event.isCancelled()) return;

        final BlockData data = event.getHarvestedBlock().getBlockData();
        final Material dataMaterial = data.getMaterial();
        final List<ItemStack> drops = event.getItemsHarvested();

        System.out.println("material: " + dataMaterial);
        System.out.println("ageable: " + (data instanceof Ageable));
        System.out.println("drops: " + drops);

        // check if the dropped item figure in the non-crops items list
        if (farmableItems.contains(data.getMaterial().toString())) {

            System.out.println("FARMABLE ITEM");

            if (drops.isEmpty()) return;

            Material material = switch (dataMaterial.toString()) {
                case "CAVE_VINES_PLANT" -> Material.valueOf("GLOW_BERRIES");
                default -> dataMaterial;
            };


            int amount = 0;
            for (ItemStack item : drops) {
                if (item.getType() == material) {
                    amount += item.getAmount();
                }
            }

            System.out.println("amount: " + amount);
            if (amount > 0) setPlayerQuestProgression(event.getPlayer(), new ItemStack(material), amount, QuestType.FARMING, null);

        } else if (data instanceof Ageable ageable) {

            if (ageable.getAge() == ageable.getMaximumAge()) {

                if (drops.isEmpty()) return;

                Material material = switch (dataMaterial.toString()) {
                    case "SWEET_BERRY_BUSH" -> Material.valueOf("SWEET_BERRIES");
                    default -> dataMaterial;
                };

                int amount = 0;
                for (ItemStack item : drops) {
                    System.out.println(item.getType() + ", " + dataMaterial);
                    if (item.getType() == dataMaterial) {
                        amount += item.getAmount();
                    }
                }

                if (amount > 0) setPlayerQuestProgression(event.getPlayer(), new ItemStack(material), amount, QuestType.FARMING, null);
            }
        }
    }
}
