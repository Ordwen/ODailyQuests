package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Objects;

public class ProgressionManager implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), event.getBlock().getType(), QuestType.BREAK);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), event.getBlock().getType(), QuestType.PLACE);
    }

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            setPlayerQuestProgression(event.getEntity().getName(), event.getItem().getItemStack().getType(), QuestType.PICKUP);
        }
    }

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        setPlayerQuestProgression(event.getWhoClicked().getName(), event.getRecipe().getResult().getType(), QuestType.CRAFT);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            switch (event.getEntity().getType()) {
                case ENDER_PEARL:
                    setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), Material.ENDER_PEARL, QuestType.LAUNCH);
                    break;
                case EGG:
                    setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), Material.EGG, QuestType.LAUNCH);
                    break;
                case ARROW:
                    setPlayerQuestProgression(((Player) event.getEntity().getShooter()).getName(), Material.ARROW, QuestType.LAUNCH);
            }
        }
    }

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        setPlayerQuestProgression(event.getPlayer().getName(), event.getItem().getType(), QuestType.CONSUME);
    }

    /**
     * Increase player quest progression.
     * @param playerName player name.
     * @param material the material of the event-block.
     * @param TYPE quest type.
     */
    public void setPlayerQuestProgression(String playerName, Material material, QuestType TYPE) {
        if (QuestsManager.getActiveQuests().containsKey(playerName)) {
            HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(playerName).getPlayerQuests();
            for (Quest quest : playerQuests.keySet()) {
                Progression questProgression = playerQuests.get(quest);
                if (!questProgression.isAchieved() && quest.getType() == TYPE && quest.getItemRequired().getType().equals(material)) {
                    questProgression.progression++;
                    if (questProgression.getProgression() == quest.getAmountRequired()) {
                        questProgression.isAchieved = true;
                        Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.QUEST_ACHIEVED.toString().replace("%questName%", quest.getQuestName()));
                        RewardManager.sendQuestReward(playerName, quest.getReward());
                    }
                }
            }
        }
    }

    /**
     * Check if player can validate a quest with type GET.
     * @param playerName player to check.
     * @param material material to check.
     */
    public static void validateGetQuestType(String playerName, Material material) {
        if (QuestsManager.getActiveQuests().containsKey(playerName)) {
            HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(playerName).getPlayerQuests();
            for (Quest quest : playerQuests.keySet()) {
                if (quest.getItemRequired().getType().equals(material)) {
                    Progression questProgression = playerQuests.get(quest);
                    if (!questProgression.isAchieved() && quest.getType() == QuestType.GET) {
                        PlayerInventory playerInventory = Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory();
                        if (getAmount(playerInventory, quest.getItemRequired().getType()) >= quest.getAmountRequired()) {
                            questProgression.isAchieved = true;
                            Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.QUEST_ACHIEVED.toString().replace("%questName%", quest.getQuestName()));
                            RewardManager.sendQuestReward(playerName, quest.getReward());
                        } else {
                            Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(QuestsMessages.NOT_ENOUGH_ITEM.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * Count amount of an item in player inventory.
     * @param playerInventory player inventory to check.
     * @param material material to check.
     * @return amount of material.
     */
    private static int getAmount(PlayerInventory playerInventory, Material material) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && itemStack.getType().equals(material)) {
                amount += itemStack.getAmount();
            }
        }
        return amount;
    }
}
