package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

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
                    }

                }
            }
        }
    }
}
