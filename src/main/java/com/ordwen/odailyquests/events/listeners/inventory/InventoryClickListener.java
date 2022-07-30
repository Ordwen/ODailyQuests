package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractSpecifiedChecker;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.types.VillagerQuest;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

public class InventoryClickListener extends AbstractSpecifiedChecker implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) return;

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) event.getWhoClicked();

        // check if player is trading
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == InventoryType.SlotType.RESULT) {

            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {

                if (merchantInventory.getSelectedRecipe() != null) {
                    validateTradeQuestType(
                            player,
                            villager,
                            merchantInventory.getSelectedRecipe(),
                            event.getCurrentItem().getAmount());
                }
            }
            return;
        }

        // complete quest for types that requires a click ( GET - REACH )
        final String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(InterfacesManager.getPlayerQuestsInventoryName())) {
            event.setCancelled(true);

            setPlayerQuestProgression(player, clickedItem);
        }
    }
}
