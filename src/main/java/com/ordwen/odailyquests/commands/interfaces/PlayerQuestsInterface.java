package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* init variables */
    private static Inventory playerQuestsInventory;

    private static String achieved;
    private static String status;
    private static String progression;
    private static String completeGetType;
    private static ItemStack emptyCaseItem;

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        emptyCaseItem = new ItemStack(Material.valueOf(Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".empty_item")));
        playerQuestsInventory = Bukkit.createInventory(null, 27, InterfacesManager.getPlayerQuestsInventoryName());

        achieved = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".achieved");
        status = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".status");
        progression = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".progress");
        completeGetType = Objects.requireNonNull(configurationFiles.getConfigFile().getConfigurationSection("interfaces.player_quests")).getString(".complete_get_type");

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

        HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        HashMap<Quest, Progression> playerQuests = activeQuests.get(playerName).getPlayerQuests();

        /* load player head */
        playerQuestsInventory.setItem(4, Items.getPlayerHead(Bukkit.getPlayer(playerName)));

        /* load quests */
        int i = 2;
        for (Quest quest : playerQuests.keySet()) {

            ItemStack itemStack = quest.getMenuItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());

            List<String> lore = new ArrayList<>(quest.getQuestDesc());
            lore.add(ChatColor.translateAlternateColorCodes('&', status));

            if (playerQuests.get(quest).isAchieved()) {
                itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(ChatColor.translateAlternateColorCodes('&', achieved));
            } else {
                if (quest.getType() == QuestType.GET) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', completeGetType));
                } else {
                    lore.add(ChatColor.translateAlternateColorCodes('&', progression)
                            .replace("%progress%", String.valueOf(playerQuests.get(quest).getProgression()))
                            .replace("%required%", String.valueOf(quest.getAmountRequired())));
                }
            }

            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            playerQuestsInventory.setItem(i+9, itemStack);

            i = i + 2;
        }

        return playerQuestsInventory;
    }

    /**
     * Get empty case item material.
     * @return material.
     */
    public static Material getEmptyCaseItem() {
        return emptyCaseItem.getType();
    }

    /**
     * Get the time remain before the next quests draw.
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {

        long timestamp = QuestsManager.getActiveQuests().get(playerName).getTimestamp();
        long diff;

        if (configurationFiles.getConfigFile().getInt("timestamp_mode") == 1) {
            Calendar oldCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);
            oldCal.set(Calendar.HOUR_OF_DAY, oldCal.getActualMinimum(Calendar.HOUR_OF_DAY));
            oldCal.set(Calendar.MINUTE, oldCal.getActualMinimum(Calendar.MINUTE));
            oldCal.set(Calendar.SECOND, oldCal.getActualMinimum(Calendar.SECOND));
            oldCal.set(Calendar.MILLISECOND, oldCal.getActualMinimum(Calendar.MILLISECOND));

            Calendar currentCal = Calendar.getInstance();
            currentCal.setTimeInMillis(System.currentTimeMillis());

            diff = currentCal.getTimeInMillis() - oldCal.getTimeInMillis();
        } else {
            diff = System.currentTimeMillis() - timestamp;
        }

        String timeRemain = "";

        if (diff <= 86400000) {
            long rest = (long) 86400000 - diff;
            int minutes = (int) ((rest / (1000 * 60)) % 60);
            int hours = (int) ((rest / (1000 * 60 * 60)) % 24);

            if (hours != 0) {
                timeRemain = String.format("%dh%dm", hours, minutes);
            } else if (minutes != 0) {
                timeRemain = String.format("%dm", minutes);
            } else {
                timeRemain = "Few seconds.";
            }
        }

        return timeRemain;
    }
}
