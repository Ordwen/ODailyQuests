package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerHarvestBlockListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event) {

        // debug > System.out.println("PLAYER HARVEST BLOCK");

        final BlockData data = event.getHarvestedBlock().getBlockData();
        if (data instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {

                Material material = data.getMaterial();
                switch (material) {
                    case SWEET_BERRY_BUSH -> material = Material.SWEET_BERRIES;
                }

                final List<ItemStack> drops = event.getItemsHarvested();
                int amount = 0;
                for (ItemStack item : drops) {
                    if (item.getType() == material) {
                        amount += item.getAmount();
                    }
                }

                setPlayerQuestProgression(event.getPlayer(), new ItemStack(material), amount, QuestType.FARMING);
            }
        }
    }
}
