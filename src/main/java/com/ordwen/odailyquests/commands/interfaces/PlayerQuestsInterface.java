package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginLogger;

import java.util.*;
import java.util.logging.Logger;

public class PlayerQuestsInterface {

    /**
     * Getting instance of classes.
     */
    private static ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public PlayerQuestsInterface(ConfigurationFiles configurationFiles) {
        PlayerQuestsInterface.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    private static Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* init variables */
    private static Inventory playerQuestsInventory;

    private static String inProgress;
    private static String achieved;
    private static ItemStack emptyCaseItem;

    private static ItemStack itemStack;
    private static ItemMeta itemMeta;

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        emptyCaseItem = new ItemStack(Material.valueOf(Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".empty_item")));
        playerQuestsInventory = Bukkit.createInventory(null, 27, InterfacesManager.getPlayerQuestsInventoryName());

        /* fill empty slots */
        for (int i = 0; i < playerQuestsInventory.getSize(); i++) {
            if (playerQuestsInventory.getItem(i) == null) playerQuestsInventory.setItem(i, emptyCaseItem);
        }

        logger.info(ChatColor.GREEN + "Player quests interface successfully loaded.");
    }

    /**
     * Get player quests inventory.
     * @return player quests inventory.
     */
    public static Inventory getPlayerQuestsInterface(String playerName) {

        inProgress = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".quest_in_progress");
        achieved = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".quest_achieved");

        HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        HashMap<Quest, Progression> playerQuests = activeQuests.get(playerName).getPlayerQuests();

        int i = 2;
        for (Quest quest : playerQuests.keySet()) {

            itemStack = quest.getItemRequired();
            itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());

            List<String> lore = new ArrayList<>(quest.getQuestDesc());
            lore.add(ChatColor.GRAY + "Status :");

            if (playerQuests.get(quest).isAchieved()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', achieved));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', inProgress));
            }

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            playerQuestsInventory.setItem(i+9, itemStack);

            i = i + 2;
        }

        return playerQuestsInventory;
    }
}
