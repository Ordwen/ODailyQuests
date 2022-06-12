package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.SpawnersProgression;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.HashSet;

public class ProgressionManager implements Listener {

    HashSet<Entity> entitiesFromSpawners = new HashSet<>();

    @EventHandler
    public void onEntitySpawnEvent(SpawnerSpawnEvent event) {
        entitiesFromSpawners.add(event.getEntity());
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), new ItemStack(event.getBlock().getType()), null, null, 1, QuestType.BREAK);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), new ItemStack(event.getBlock().getType()), null, null, 1, QuestType.PLACE);
    }

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            setPlayerQuestProgression(event.getEntity().getName(), event.getItem().getItemStack(), null, null, event.getItem().getItemStack().getAmount(), QuestType.PICKUP);
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        ItemStack test = event.getRecipe().getResult().clone();
        ClickType click = event.getClick();

        int recipeAmount = test.getAmount();

        switch (click) {
            case NUMBER_KEY:
                if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null)
                    recipeAmount = 0;
                break;
            case DROP:
            case CONTROL_DROP:
                ItemStack cursor = event.getCursor();
                if (!(cursor == null || cursor.getType() == Material.AIR))
                    recipeAmount = 0;
                break;
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                if (recipeAmount == 0)
                    break;
                int maxCraftable = getMaxCraftAmount(event.getInventory());
                int capacity = fits(test, event.getView().getBottomInventory());

                if (capacity < maxCraftable)
                    maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;

                recipeAmount = maxCraftable;
                break;
        }

        if (recipeAmount == 0)
            return;

        test.setAmount(recipeAmount);
        setPlayerQuestProgression(event.getWhoClicked().getName(), test, null, null, test.getAmount(), QuestType.CRAFT);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            switch (event.getEntity().getType()) {
                case ENDER_PEARL -> setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), new ItemStack(Material.ENDER_PEARL), null, null, 1, QuestType.LAUNCH);
                case EGG -> setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), new ItemStack(Material.EGG), null, null, 1, QuestType.LAUNCH);
                case ARROW -> setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), new ItemStack(Material.ARROW), null, null, 1, QuestType.LAUNCH);
            }
        }
    }

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), event.getItem(), null, null, 1, QuestType.CONSUME);
    }

    @EventHandler
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), new ItemStack(event.getItemType()), null, null, event.getItemAmount(), QuestType.COOK);
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (event.getEntity().getKiller() != null) {
            if (SpawnersProgression.isSpawnersProgressionDisabled()) {
                if (!entitiesFromSpawners.contains(entity)) {
                    setPlayerQuestProgression(event.getEntity().getKiller().getName(), null, event.getEntity().getType(), null, 1, QuestType.KILL);
                }
            } else setPlayerQuestProgression(event.getEntity().getKiller().getName(), null, event.getEntity().getType(), null, 1, QuestType.KILL);
        }

        entitiesFromSpawners.remove(entity);
    }

    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent event) {
        setPlayerQuestProgression(event.getEnchanter().getName(), event.getItem(), null, null, 1, QuestType.ENCHANT);
    }

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            setPlayerQuestProgression(event.getPlayer().getName(), ((Item) event.getCaught()).getItemStack(), null, null, 1, QuestType.FISH);
        }
    }

    /* QUESTS TYPES UPDATE */
    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {
        setPlayerQuestProgression(event.getOwner().getName(), null, event.getEntityType(), null, 1, QuestType.TAME);
    }

    @EventHandler
    public void onEntityBreadEvent(EntityBreedEvent event) {
        assert event.getBreeder() instanceof Player;
        setPlayerQuestProgression(event.getBreeder().getName(), null, event.getEntityType(), null, 1, QuestType.BREED);
    }

    @EventHandler
    public void onBrewEvent(PlayerShearEntityEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), null, event.getEntity().getType(), null, 1, QuestType.SHEAR);
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.getItemStack().getType() == Material.MILK_BUCKET) {
            setPlayerQuestProgression(event.getPlayer().getName(), null, null, null, 1, QuestType.MILKING);
        }
    }

    /**
     * Increase player quest progression.
     *
     * @param playerName player name.
     * @param item       the material of the event-block.
     * @param type       quest type.
     */
    public static void setPlayerQuestProgression(String playerName, ItemStack item, EntityType entity, String entityName, int quantity, QuestType type) {
        if (DisabledWorlds.isWorldDisabled(Bukkit.getPlayer(playerName).getWorld().getName())) {
            return;
        }
        if (QuestsManager.getActiveQuests().containsKey(playerName)) {
            HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(playerName).getPlayerQuests();
            for (Quest quest : playerQuests.keySet()) {
                Progression questProgression = playerQuests.get(quest);
                if (!questProgression.isAchieved() && quest.getType() == type) {
                    boolean isRequiredItem = false;
                    if (type == QuestType.MILKING) {
                        isRequiredItem = true;
                    } else if (type == QuestType.KILL
                            || type == QuestType.BREED
                            || type == QuestType.TAME
                            || type == QuestType.SHEAR) {
                        if (quest.getEntityType() == null) {
                            isRequiredItem = true;
                        }
                        else if (quest.getEntityType().equals(entity)) {
                            isRequiredItem = true;
                        }
                    } else if (type == QuestType.CUSTOM_MOBS) {
                        if (quest.getEntityName().equals(entityName)) {
                            isRequiredItem = true;
                        }
                    } else {
                        if (quest.getItemRequired() == null) {
                            isRequiredItem = true;
                        }
                        else if (quest.getItemRequired().hasItemMeta()) {
                            if (item.hasItemMeta()) {
                                isRequiredItem =
                                        quest.getItemRequired().getType() == item.getType()
                                                && quest.getItemRequired().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())
                                                && quest.getItemRequired().getItemMeta().getLore().equals(item.getItemMeta().getLore());
                            }
                        } else isRequiredItem = (quest.getItemRequired().getType() == item.getType());
                    }

                    if (isRequiredItem) {
                        for (int i = 0; i < quantity; i++) {
                            questProgression.progression++;
                        }
                        if (questProgression.getProgression() >= quest.getAmountRequired()) {
                            questProgression.isAchieved = true;
                            QuestsManager.getActiveQuests().get(playerName).increaseAchievedQuests(playerName);
                            RewardManager.sendAllRewardItems(quest.getQuestName(), playerName, quest.getReward());
                        }

                        if (!Synchronization.isSynchronised()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if player can validate a quest with type GET.
     *
     * @param playerName  player to check.
     * @param clickedItem item clicked in the inventory.
     */
    public static void validateGetQuestType(String playerName, ItemStack clickedItem) {
        if (DisabledWorlds.isWorldDisabled(Bukkit.getPlayer(playerName).getWorld().getName())) {
            return;
        }
        if (QuestsManager.getActiveQuests().containsKey(playerName)) {
            HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(playerName).getPlayerQuests();
            for (Quest quest : playerQuests.keySet()) {
                if (clickedItem.equals(quest.getMenuItem()) && quest.getType() == QuestType.GET) {
                    Progression questProgression = playerQuests.get(quest);
                    if (!questProgression.isAchieved()) {
                        PlayerInventory playerInventory = Bukkit.getPlayer(playerName).getInventory();
                        if (getAmount(playerInventory, quest.getItemRequired()) >= quest.getAmountRequired()) {
                            questProgression.isAchieved = true;
                            QuestsManager.getActiveQuests().get(playerName).increaseAchievedQuests(playerName);
                            Bukkit.getPlayer(playerName).closeInventory();
                            RewardManager.sendAllRewardItems(quest.getQuestName(), playerName, quest.getReward());
                        } else {
                            Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.NOT_ENOUGH_ITEM.toString());
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Count amount of an item in player inventory.
     *
     * @param playerInventory player inventory to check.
     * @param item            material to check.
     * @return amount of material.
     */
    private static int getAmount(PlayerInventory playerInventory, ItemStack item) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && itemStack.getType().equals(item.getType())) {
                if (item.hasItemMeta()) {
                    if (itemStack.hasItemMeta()) {
                        boolean sameItem = itemStack.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())
                                && itemStack.getItemMeta().getLore().equals(item.getItemMeta().getLore());
                        if (sameItem) {
                            amount += itemStack.getAmount();
                        }
                    }
                } else amount += itemStack.getAmount();
            }
        }
        return amount;
    }

    private int fits(ItemStack stack, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int result = 0;

        for (ItemStack is : contents)
            if (is == null)
                result += stack.getMaxStackSize();
            else if (is.isSimilar(stack))
                result += Math.max(stack.getMaxStackSize() - is.getAmount(), 0);

        return result;
    }

    private int getMaxCraftAmount(CraftingInventory inv) {
        if (inv.getResult() == null)
            return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getMatrix())
            if (is != null && is.getAmount() < materialCount)
                materialCount = is.getAmount();

        return resultCount * materialCount;
    }
}
