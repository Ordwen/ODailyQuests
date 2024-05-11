package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class QuestsInterfaces {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public QuestsInterfaces(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private final NamespacedKey usePlaceholdersKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_use_placeholders");
    private final NamespacedKey requiredKey = new NamespacedKey(ODailyQuests.INSTANCE, "odq_interface_required");

    /**
     * Load the global quests interface.
     */
    public void loadGlobalInterface(QuestSystem questSystem) {
        questSystem.getCategorizedInterfaces().clear();

        /* Init empty case items */
        final ItemStack globalEmptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.global_quests").getString(".empty_item")));
        questSystem.getEmptyCaseItems().add(globalEmptyCaseItem);

        /* Global quests inventory */
        int neededInventories = (int) Math.ceil(questSystem.getGlobalCategory().size() / questSystem.getQuestsInvSize());
        loadSelectedInterface(questSystem, "global", InterfacesManager.getGlobalQuestsInventoryName(), globalEmptyCaseItem, neededInventories, questSystem.getGlobalCategory());
    }

    /**
     * Load all categorized interfaces.
     */
    public void loadCategorizedInterfaces(QuestSystem questSystem) {
        questSystem.getCategorizedInterfaces().clear();

        /* Init empty case items */
        final ItemStack easyEmptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.easy_quests").getString(".empty_item")));
        final ItemStack mediumEmptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.medium_quests").getString(".empty_item")));
        final ItemStack hardEmptyCaseItem = new ItemStack(Material.valueOf(configurationFiles.getConfigFile().getConfigurationSection("interfaces.hard_quests").getString(".empty_item")));
        questSystem.getEmptyCaseItems().addAll(Arrays.asList(easyEmptyCaseItem, mediumEmptyCaseItem, hardEmptyCaseItem));

        /* Easy quests inventory */
        int neededInventories = (int) Math.ceil(questSystem.getEasyCategory().size() / questSystem.getQuestsInvSize());
        loadSelectedInterface(questSystem, "easy", InterfacesManager.getEasyQuestsInventoryName(), easyEmptyCaseItem, neededInventories, questSystem.getEasyCategory());

        /* Medium quests inventory */
        neededInventories = (int) Math.ceil(questSystem.getMediumCategory().size() / questSystem.getQuestsInvSize());
        loadSelectedInterface(questSystem, "medium", InterfacesManager.getMediumQuestsInventoryName(), mediumEmptyCaseItem, neededInventories, questSystem.getMediumCategory());

        /* Hard quests inventory */
        neededInventories = (int) Math.ceil(questSystem.getHardCategory().size() / questSystem.getQuestsInvSize());
        loadSelectedInterface(questSystem, "hard", InterfacesManager.getHardQuestsInventoryName(), hardEmptyCaseItem, neededInventories, questSystem.getHardCategory());
    }

    /**
     * Load specified interface.
     *
     * @param inventoryName name of interface.
     * @param emptyCaseItem item for empty-cases.
     * @param quests        list of quests.
     */
    public void loadSelectedInterface(QuestSystem questSystem, String category, String inventoryName, ItemStack emptyCaseItem, int neededInventories, ArrayList<AbstractQuest> quests) {

        boolean allQuestsLoaded = false;
        int currentQuestIndex = 0;

        final List<InterfaceInventory> questsInventories = new ArrayList<>();

        for (int i = 0; i < neededInventories; i++) {
            InterfaceInventory inv = new InterfaceInventory(54, inventoryName + " - " + (i + 1), category, i);
            if (i > 0) {
                inv.getInventory().setItem(45, Buttons.getPreviousButton());
            }
            if (i < neededInventories - 1) {
                inv.getInventory().setItem(53, Buttons.getNextButton());
            }
            questsInventories.add(inv);
        }

        Debugger.addDebug("quest inventories size: " + questsInventories.size());

        for (InterfaceInventory inv : questsInventories) {
            int i = 0;

            /* add quests items on slots */
            while (i < questSystem.getQuestsInvSize() && !allQuestsLoaded) {
                if (currentQuestIndex < quests.size()) {
                    final AbstractQuest quest = quests.get(currentQuestIndex);

                    final ItemStack itemStack = quest.getMenuItem();
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta != null) {
                        itemMeta.setDisplayName(quest.getQuestName());
                        itemMeta.setLore(quest.getQuestDesc());

                        if (quest.isUsingPlaceholders()) {
                            itemMeta.getPersistentDataContainer().set(usePlaceholdersKey, PersistentDataType.BYTE, (byte) 1);
                            itemMeta.getPersistentDataContainer().set(requiredKey, PersistentDataType.INTEGER, quest.getAmountRequired());
                        }

                        itemStack.setItemMeta(itemMeta);
                    }

                    inv.getInventory().setItem(i, itemStack);
                    i++;
                    currentQuestIndex++;
                } else {
                    allQuestsLoaded = true;
                }
            }

            /* fill empty slots */
            for (int j = 0; j < inv.getInventory().getSize(); j++) {
                if (inv.getInventory().getItem(j) == null) inv.getInventory().setItem(j, emptyCaseItem);
            }

            questSystem.getCategorizedInterfaces().add(inv);
            Debugger.addDebug("Adding one to categorized interfaces");
        }

        PluginLogger.fine(questSystem.getSystemName() + " Categorized quests interface named " + inventoryName + " successfully loaded.");
    }

    public InterfaceInventory getInterfacePage(QuestSystem questSystem, String category, int page, Player player) {
        for (InterfaceInventory inventory : questSystem.getCategorizedInterfaces()) {
            Debugger.addDebug("Category: " + category + " Page: " + page);
            Debugger.addDebug("Inventory Category: " + inventory.getCategory() + " Page: " + inventory.getPage());
            if (inventory.getCategory().equals(category) && inventory.getPage() == page) {
                Debugger.addDebug("inventory is equal to inventory");
                for (int i = 0; i < inventory.getInventory().getSize(); i++) {
                    final ItemStack item = inventory.getInventory().getItem(i);
                    if (item != null && item.getItemMeta() != null) {
                        final ItemMeta itemMeta = item.getItemMeta();
                        if (itemMeta.getPersistentDataContainer().has(usePlaceholdersKey, PersistentDataType.BYTE)) {
                            final List<String> lore = itemMeta.getLore();
                            if (lore == null) continue;

                            final int required = itemMeta.getPersistentDataContainer().get(requiredKey, PersistentDataType.INTEGER);

                            lore.replaceAll(s -> s.replace("%progress%", String.valueOf(0)));
                            lore.replaceAll(s -> s.replace("%progressBar%", ProgressBar.getProgressBar(0, required)));
                            lore.replaceAll(s -> s.replace("%required%", String.valueOf(required)));
                            lore.replaceAll(s -> s.replace("%drawIn%", "~"));
                            lore.replaceAll(s -> PAPIHook.getPlaceholders(player, s));

                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                        }
                    }
                }
                return inventory;
            }
        }
        return null;
    }
}
