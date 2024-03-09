package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
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

        final Player player = event.getPlayer();

        final BlockData data = event.getHarvestedBlock().getBlockData();
        final Material dataMaterial = data.getMaterial();
        final List<ItemStack> drops = event.getItemsHarvested();

        // check if the dropped item figure in the non-crops items list
        if (farmableItems.contains(data.getMaterial().toString())) {

            if (drops.isEmpty()) return;

            Material material = switch (dataMaterial.toString()) {
                case "CAVE_VINES_PLANT" -> Material.valueOf("GLOW_BERRIES");
                default -> dataMaterial;
            };

            makeProgress(player, material, drops, material);
        }

        else if (data instanceof Ageable ageable) {

            if (ageable.getAge() == ageable.getMaximumAge()) {

                if (drops.isEmpty()) return;

                Material material = switch (dataMaterial.toString()) {
                    case "SWEET_BERRY_BUSH" -> Material.valueOf("SWEET_BERRIES");
                    default -> dataMaterial;
                };

                makeProgress(player, dataMaterial, drops, material);
            }
        }
    }

    /**
     * Calculate the amount of items harvested and update the player's quest progression
     * @param player the player who harvested the block
     * @param dataMaterial the material of the block
     * @param drops the list of items harvested
     * @param material the material of the item
     */
    private void makeProgress(Player player, Material dataMaterial, List<ItemStack> drops, Material material) {
        int amount = 0;
        for (ItemStack item : drops) {
            if (item.getType() == dataMaterial) {
                amount += item.getAmount();
            }
        }

        if (amount > 0) {

            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerHarvestBlockListener: makeProgress: " + player.getName() + " harvested " + amount + " " + material + ".");

            setPlayerQuestProgression(player, new ItemStack(material), amount, QuestType.FARMING);
        }
    }
}
