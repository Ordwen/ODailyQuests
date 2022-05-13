package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerQuestsInterface {

    /**
     * Getting instance of classes.
     */
    private static FileConfiguration interfaceConfig;
    private static FileConfiguration config;
    /**
     * Class instance constructor.
     *
     * @param playerInterfaceFile interfaceConfiguration files class.
     */
    public PlayerQuestsInterface(PlayerInterfaceFile playerInterfaceFile, ConfigurationFiles configurationFiles) {
        interfaceConfig = playerInterfaceFile.getPlayerInterfaceFileConfiguration();
        config = configurationFiles.getConfigFile();
    }

    /* init variables */
    private static Inventory playerQuestsInventoryBase;
    private static int size;
    private static String achieved;
    private static String status;
    private static String progression;
    private static String completeGetType;

    /* item slots */
    private static int slotPlayerHead = -1;
    private static int slotFirstQuest;
    private static int slotSecondQuest;
    private static int slotThirdQuest;
    private static List<ItemStack> fillItems;
    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        /* load item slots */
        if (interfaceConfig.getConfigurationSection("player_interface.player_head").getBoolean(".enabled")) {
            slotPlayerHead = interfaceConfig.getConfigurationSection("player_interface.player_head").getInt(".slot");
        }
        slotFirstQuest = interfaceConfig.getConfigurationSection("player_interface").getInt(".first_quest_slot");
        slotSecondQuest = interfaceConfig.getConfigurationSection("player_interface").getInt(".second_quest_slot");
        slotThirdQuest = interfaceConfig.getConfigurationSection("player_interface").getInt(".third_quest_slot");

        /* create base of inventory */
        size = interfaceConfig.getConfigurationSection("player_interface").getInt(".size");
        playerQuestsInventoryBase = Bukkit.createInventory(null, size, "BASIC");

        /* load all texts */
        achieved = interfaceConfig.getConfigurationSection("player_interface").getString(".achieved");
        status = interfaceConfig.getConfigurationSection("player_interface").getString(".status");
        progression = interfaceConfig.getConfigurationSection("player_interface").getString(".progress");
        completeGetType = interfaceConfig.getConfigurationSection("player_interface").getString(".complete_get_type");

        // CHARGER TOUS LES ITEMS QUI NE CHANGENT PAS SELON LE JOUEURS (section "items")
        /* load all fill items */
        fillItems = new ArrayList<>();

        PluginLogger.info(ChatColor.GREEN + "Player quests interface successfully loaded.");
    }

    /**
     * Get player quests inventory.
     *
     * @return player quests inventory.
     */
    public static Inventory getPlayerQuestsInterface(String playerName) {

        Inventory playerQuestsInventoryIndividual = Bukkit.createInventory(null, size, InterfacesManager.getPlayerQuestsInventoryName());
        playerQuestsInventoryIndividual.setContents(playerQuestsInventoryBase.getContents());

        HashMap<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        HashMap<Quest, Progression> playerQuests = activeQuests.get(playerName).getPlayerQuests();

        /* load player head */
        playerQuestsInventoryIndividual.setItem(slotPlayerHead, Items.getPlayerHead(Bukkit.getPlayer(playerName)));

        /* load quests */
        int i = 0;
        for (Quest quest : playerQuests.keySet()) {

            ItemStack itemStack = quest.getMenuItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

            assert itemMeta != null;
            itemMeta.setDisplayName(quest.getQuestName());

            List<String> lore = new ArrayList<>(quest.getQuestDesc());
            lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(status)));

            if (playerQuests.get(quest).isAchieved()) {
                itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(achieved)));
            } else {
                if (quest.getType() == QuestType.GET) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(completeGetType)));
                } else {
                    lore.add(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(progression))
                            .replace("%progress%", String.valueOf(playerQuests.get(quest).getProgression()))
                            .replace("%required%", String.valueOf(quest.getAmountRequired())));
                }
            }

            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            switch (i) {
                case 0 -> playerQuestsInventoryIndividual.setItem(slotFirstQuest, itemStack);
                case 1 -> playerQuestsInventoryIndividual.setItem(slotSecondQuest, itemStack);
                case 2 -> playerQuestsInventoryIndividual.setItem(slotThirdQuest, itemStack);
                default -> {
                    PluginLogger.error("Unexpected value on the load of the player quests interface.");
                    PluginLogger.error("Invalid index for quest place : " + i);
                    PluginLogger.error("Please inform the developer.");
                }
            }
            i++;
        }
        return playerQuestsInventoryIndividual;
    }

    /**
     * Get the time remain before the next quests draw.
     *
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {

        long timestamp = QuestsManager.getActiveQuests().get(playerName).getTimestamp();
        long diff;

        if (config.getInt("timestamp_mode") == 1) {
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

        long rest;
        int minutes;
        int hours;
        int days;

        String d = config.getConfigurationSection("temporality_initials").getString("days");
        String h = config.getConfigurationSection("temporality_initials").getString("hours");
        String m = config.getConfigurationSection("temporality_initials").getString("minutes");

        switch (config.getInt("temporality_mode")) {
            case 1:
                rest = 86400000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);

                if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
            case 2:
                rest = 604800000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                days = (int) (rest / (1000 * 60 * 60 * 24));

                if (days != 0) {
                    timeRemain = String.format("%d" + d + "%d" + h + "%d" + m, days, hours, minutes);
                }
                else if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
            case 3:
                rest = 2678400000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                days = (int) (rest / (1000 * 60 * 60 * 24));

                if (days != 0) {
                    timeRemain = String.format("%d" + d + "%d" + h + "%d" + m, days, hours, minutes);
                }
                else if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
        }

        return timeRemain;
    }

    public static List<ItemStack> getFillItems() {
        return fillItems;
    }
}
