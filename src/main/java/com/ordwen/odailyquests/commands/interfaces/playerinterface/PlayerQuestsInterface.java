package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.interfaces.QuestInventory;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.ItemType;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.getters.InterfaceItemGetter;
import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerQuestsInterface extends InterfaceItemGetter {

    /**
     * Get player quests inventory.
     *
     * @return player quests inventory.
     */
    public static QuestInventory getPlayerQuestsInterface(QuestSystem questSystem, Player player) {

        final Map<String, PlayerQuests> activeQuests = questSystem.getActiveQuests();

        if (!activeQuests.containsKey(player.getName())) {
            PluginLogger.error("Impossible to find the player " + player.getName() + " in the " + questSystem.getSystemName() + " active quests.");
            PluginLogger.error("It can happen if the player try to open the interface while the server/plugin is reloading.");
            PluginLogger.error("If the problem persist, please contact the developer.");
            return null;
        }

        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (QuestLoaderUtils.isTimeToRenew(player, activeQuests, questSystem)) return getPlayerQuestsInterface(questSystem, player);

        final Map<AbstractQuest, Progression> questsMap = playerQuests.getPlayerQuests();

        final QuestInventory playerQuestsInventoryIndividual = new QuestInventory(questSystem.getSize(), PAPIHook.getPlaceholders(player, questSystem.getInterfaceName()));
        playerQuestsInventoryIndividual.getInventory().setContents(questSystem.getPlayerQuestsInventoryBase().getContents());

        if (!questSystem.getPapiItems().isEmpty()) {
            for (Integer slot : questSystem.getPapiItems().keySet()) {

                final ItemStack itemCopy = questSystem.getPapiItems().get(slot).clone();
                final ItemMeta meta = itemCopy.getItemMeta();
                final List<String> lore = meta.getLore();

                meta.setDisplayName(PAPIHook.getPlaceholders(player, meta.getDisplayName()));

                for (String str : lore) {
                    lore.set(lore.indexOf(str), PAPIHook.getPlaceholders(player, str)
                            .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                            .replace("%drawIn%", TimeRemain.timeRemain(player.getName(), questSystem)));
                }

                meta.setLore(lore);
                itemCopy.setItemMeta(meta);
                playerQuestsInventoryIndividual.getInventory().setItem(slot, itemCopy);
            }
        }

        /* load player head */
        if (questSystem.isPlayerHeadEnabled()) {
            final ItemStack playerHead = PlayerHead.getPlayerHead(player, questSystem);
            for (int slot : questSystem.getSlotsPlayerHead()) {
                playerQuestsInventoryIndividual.getInventory().setItem(slot, playerHead);
            }
        }

        /* load quests */
        int i = 0;
        for (AbstractQuest quest : questsMap.keySet()) {

            ItemStack itemStack;
            if (questsMap.get(quest).isAchieved()) {
                itemStack = quest.getAchievedItem().clone();
            } else {
                itemStack = quest.getMenuItem().clone();
            }

            final ItemMeta itemMeta = itemStack.getItemMeta().clone();
            itemMeta.setDisplayName(quest.getQuestName());

            final List<String> lore = new ArrayList<>(quest.getQuestDesc());

            if (quest.isUsingPlaceholders()) {
                final Progression progression = questsMap.get(quest);

                for (String str : lore) {
                    lore.set(
                            lore.indexOf(str),
                            PAPIHook.getPlaceholders(player, str)
                                    .replace("%progress%", String.valueOf(progression.getProgression()))
                                    .replace("%progressBar%", ProgressBar.getProgressBar(progression.getProgression(), quest.getAmountRequired()))
                                    .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                    .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName(), questSystem))
                                    .replace("%status%", getQuestStatus(questSystem, progression, quest, player))
                    );
                }

                itemMeta.setDisplayName(PAPIHook.getPlaceholders(player, itemMeta.getDisplayName()));
            }

            if (!questSystem.getStatus().isEmpty() && !questSystem.isStatusDisabled())
                lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, questSystem.getStatus())));

            if (questsMap.get(quest).isAchieved()) {

                if (questSystem.isGlowingEnabled()) {
                    itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
                }

                if (!questSystem.getAchieved().isEmpty() && !questSystem.isStatusDisabled()) {
                    lore.add(ColorConvert.convertColorCode(questSystem.getAchieved()));
                }
            } else {

                if (quest.getQuestType().equals("GET")) {
                    if (!questSystem.getCompleteGetType().isEmpty())
                        lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, questSystem.getCompleteGetType())
                                .replace("%progress%", String.valueOf(questsMap.get(quest).getProgression()))
                                .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                .replace("%progressBar%", ProgressBar.getProgressBar(questsMap.get(quest).getProgression(), quest.getAmountRequired()))
                        ));
                } else {
                    if (!questSystem.getProgression().isEmpty() && !questSystem.isStatusDisabled()) {
                        lore.add(ColorConvert.convertColorCode(PAPIHook.getPlaceholders(player, questSystem.getProgression())
                                .replace("%progress%", String.valueOf(questsMap.get(quest).getProgression()))
                                .replace("%required%", String.valueOf(quest.getAmountRequired()))
                                .replace("%progressBar%", ProgressBar.getProgressBar(questsMap.get(quest).getProgression(), quest.getAmountRequired()))
                        ));
                    }
                }
            }

            itemMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_DYE
            );

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            if (questSystem.getSlotQuests().get(i) != null) {
                for (int slot : questSystem.getSlotQuests().get(i)) {
                    playerQuestsInventoryIndividual.getInventory().setItem(slot - 1, itemStack);
                }
            } else {
                PluginLogger.error("An error occurred when loading the player interface.");
                PluginLogger.error("The slot for the quest number " + (i + 1) + " is not defined in the playerInterface file.");
            }

            i++;
        }
        return playerQuestsInventoryIndividual;
    }

    /**
     * Get the corresponding text for the quest status.
     *
     * @param progression the current progression of the quest.
     * @param quest       the quest.
     * @param player      the player.
     * @return the achieved message or the progress message.
     */
    private static String getQuestStatus(QuestSystem questSystem, Progression progression, AbstractQuest quest, Player player) {
        if (progression.isAchieved()) {
            return PAPIHook.getPlaceholders(player, questSystem.getAchieved());
        } else {
            return PAPIHook.getPlaceholders(player, questSystem.getProgression()
                    .replace("%progress%", String.valueOf(progression.getProgression()))
                    .replace("%required%", String.valueOf(quest.getAmountRequired()))
                    .replace("%progressBar%", ProgressBar.getProgressBar(progression.getProgression(), quest.getAmountRequired()))
            );
        }
    }

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setInterfaceConfig(PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection(questSystem.getConfigPath() + "player_interface"));
            if (questSystem.getInterfaceConfig() == null) {
                PluginLogger.error("An error occurred when loading the " + questSystem.getSystemName() + " player interface.");
                PluginLogger.error("The playerInterface file is not correctly configured.");
                return;
            }

            initVariables(questSystem.getInterfaceConfig(), questSystem);

            final ConfigurationSection questsSection = questSystem.getInterfaceConfig().getConfigurationSection("quests");
            if (questsSection == null) {
                PluginLogger.error("An error occurred when loading the " + questSystem.getSystemName() + " player interface.");
                PluginLogger.error("The quests section is not defined in the playerInterface file.");
                return;
            }

            loadQuestsSlots(questSystem, questsSection);

            final ConfigurationSection itemsSection = questSystem.getInterfaceConfig().getConfigurationSection("items");
            if (itemsSection == null) {
                PluginLogger.error("An error occurred when loading the " + questSystem.getSystemName() + " player interface.");
                PluginLogger.error("The items section is not defined in the playerInterface file.");
                return;
            }

            loadItems(questSystem, itemsSection);

            PluginLogger.fine(questSystem.getSystemName() + " Player quests interface successfully loaded.");
        });
    }

    /**
     * Reload player quests interface.
     *
     * @param interfaceConfig configuration section of the interface.
     */
    private void initVariables(ConfigurationSection interfaceConfig, QuestSystem questSystem) {
            /* clear all lists, in case of reload */
            questSystem.getSlotsPlayerHead().clear();
            questSystem.getSlotQuests().clear();
            questSystem.getFillItems().clear();
            questSystem.getCloseItems().clear();
            questSystem.getPlayerCommandsItems().clear();
            questSystem.getConsoleCommandsItems().clear();
            questSystem.getPapiItems().clear();

            /* get inventory name */
            questSystem.setInterfaceName(ColorConvert.convertColorCode(interfaceConfig.getString(".inventory_name")));

            /* get booleans */
            questSystem.setPlayerHeadEnabled(interfaceConfig.getConfigurationSection("player_head").getBoolean(".enabled"));
            questSystem.setGlowingEnabled(interfaceConfig.getBoolean("glowing_if_achieved"));
            questSystem.setStatusDisabled(interfaceConfig.getBoolean("disable_status"));

            /* create base of inventory */
            questSystem.setSize(interfaceConfig.getInt(".size"));
            questSystem.setPlayerQuestsInventoryBase(Bukkit.createInventory(null, questSystem.getSize(), "BASIC"));

            /* load all texts */
            questSystem.setAchieved(interfaceConfig.getString(".achieved"));
            questSystem.setStatus(interfaceConfig.getString(".status"));
            questSystem.setProgression(interfaceConfig.getString(".progress"));
            questSystem.setCompleteGetType(interfaceConfig.getString(".complete_get_type"));

            /* load player head slots */
            if (questSystem.isPlayerHeadEnabled()) {
                final ConfigurationSection section = interfaceConfig.getConfigurationSection("player_head");

                if (section.isList(".slot")) questSystem.getSlotsPlayerHead().addAll(section.getIntegerList(".slot"));
                else questSystem.getSlotsPlayerHead().add(section.getInt(".slot") - 1);
            }
    }

    /**
     * Load quests slots.
     *
     * @param questsSection configuration section of the quests.
     */
    private void loadQuestsSlots(QuestSystem questSystem, ConfigurationSection questsSection) {
        for (String index : questsSection.getKeys(false)) {
            int slot = Integer.parseInt(index) - 1;
            if (questsSection.isList(index)) {
                final List<Integer> values = questsSection.getIntegerList(index);
                questSystem.getSlotQuests().put(slot, values);
            } else {
                int value = questsSection.getInt(index);
                questSystem.getSlotQuests().put(slot, Collections.singletonList(value));
            }
        }
    }

    /**
     * Load items.
     *
     * @param itemsSection configuration section of the items.
     */
    private void loadItems(QuestSystem questSystem, ConfigurationSection itemsSection) {
        for (String element : itemsSection.getKeys(false)) {

            final ConfigurationSection itemData = itemsSection.getConfigurationSection(element + ".item");
            if (itemData == null) {
                configurationError(element, "item", "The item is not defined.");
                continue;
            }

            /* load item */
            final String material = itemData.getString("material");
            if (material == null) {
                configurationError(element, "material", "The material of the item is not defined.");
                continue;
            }

            ItemStack item;
            if (material.equals("CUSTOM_HEAD")) {
                final String texture = itemData.getString("texture");
                item = Buttons.getCustomHead(texture);

            } else if (material.contains(":")) {
                item = getItem(material, element, "material");

            } else item = new ItemStack(Material.valueOf(material));

            if (item == null) item = new ItemStack(Material.BARRIER);

            /* get slot(s) */
            final List<Integer> slots;
            if (itemData.isList("slot")) {
                slots = itemData.getIntegerList("slot");
            } else {
                slots = List.of(itemData.getInt("slot"));
            }

            /* affect item to slot(s) depending on the type */
            final String itemType = itemsSection.getString(element + ".type");
            switch (ItemType.valueOf(itemType)) {

                case FILL -> {
                    ItemMeta fillItemMeta = item.getItemMeta();

                    fillItemMeta.setDisplayName(ChatColor.RESET + "");
                    item.setItemMeta(fillItemMeta);
                    questSystem.getFillItems().add(item);
                }

                case CLOSE -> {
                    item.setItemMeta(getItemMeta(item, itemData));
                    questSystem.getCloseItems().add(item);
                }

                case PLAYER_COMMAND -> {
                    List<String> commands = itemsSection.getStringList(element + ".commands");
                    item.setItemMeta(getItemMeta(item, itemData));

                    for (int slot : slots) {
                        questSystem.getPlayerCommandsItems().put(slot - 1, commands);
                    }
                }

                case CONSOLE_COMMAND -> {
                    List<String> commands = itemsSection.getStringList(element + ".commands");
                    item.setItemMeta(getItemMeta(item, itemData));

                    for (int slot : slots) {
                        questSystem.getConsoleCommandsItems().put(slot - 1, commands);
                    }
                }
            }

            if (itemsSection.contains(element + ".use_placeholders") && itemsSection.getBoolean(element + ".use_placeholders")) {
                for (int slot : slots) {
                    questSystem.getPapiItems().put(slot - 1, item);
                }
            }

            for (int slot : slots) {
                questSystem.getPlayerQuestsInventoryBase().setItem(slot - 1, item);
            }
        }
    }

    /**
     * Load the ItemMeta of an item.
     *
     * @param itemStack item to load.
     * @param section   section of the item.
     * @return ItemMeta of the item.
     */
    private ItemMeta getItemMeta(ItemStack itemStack, ConfigurationSection section) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        if (section.contains("custom_model_data")) meta.setCustomModelData(section.getInt("custom_model_data"));

        final String name = section.getString("name");
        if (name != null) {
            meta.setDisplayName(ColorConvert.convertColorCode(name));
        }

        final List<String> lore = section.getStringList("lore");
        for (String str : lore) {
            lore.set(lore.indexOf(str), ColorConvert.convertColorCode(str));
        }
        meta.setLore(lore);

        return meta;
    }

}
