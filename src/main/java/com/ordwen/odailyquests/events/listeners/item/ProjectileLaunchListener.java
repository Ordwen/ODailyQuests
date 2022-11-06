package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileLaunchListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        final String id = String.valueOf(event.getEntity().getUniqueId());
        System.out.println(id);

        if (event.getEntity().getShooter() instanceof Player player) {
            switch (event.getEntity().getType()) {
                case ENDER_PEARL -> setPlayerQuestProgression(player, new ItemStack(Material.ENDER_PEARL), 1, QuestType.LAUNCH, id);
                case EGG -> setPlayerQuestProgression(player, new ItemStack(Material.EGG), 1, QuestType.LAUNCH, id);
                case ARROW -> setPlayerQuestProgression(player, new ItemStack(Material.ARROW), 1, QuestType.LAUNCH, id);
            }
        }
    }
}
