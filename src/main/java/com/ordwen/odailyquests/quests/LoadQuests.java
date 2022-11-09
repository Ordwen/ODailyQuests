package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.apis.hooks.mobs.EliteMobsHook;
import com.ordwen.odailyquests.apis.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import com.ordwen.odailyquests.tools.ColorConvert;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.util.ArrayList;
import java.util.List;

public class LoadQuests {

    /* Init quests lists */
    private static final ArrayList<AbstractQuest> globalQuests = new ArrayList<>();
    private static final ArrayList<AbstractQuest> easyQuests = new ArrayList<>();
    private static final ArrayList<AbstractQuest> mediumQuests = new ArrayList<>();
    private static final ArrayList<AbstractQuest> hardQuests = new ArrayList<>();

    /**
     * Load all quests from files.
     */
    public static void loadCategories() {

        globalQuests.clear();
        easyQuests.clear();
        mediumQuests.clear();
        hardQuests.clear();

        /* init files */
        FileConfiguration globalQuestsFile = QuestsFiles.getGlobalQuestsFile();
        FileConfiguration easyQuestsFile = QuestsFiles.getEasyQuestsFile();
        FileConfiguration mediumQuestsFile = QuestsFiles.getMediumQuestsFile();
        FileConfiguration hardQuestsFile = QuestsFiles.getHardQuestsFile();

        if (Modes.getQuestsMode() == 1) {
            /* load global quests */
            loadQuests(globalQuestsFile, globalQuests, "Global Quests");
            if (globalQuests.size() < QuestsAmount.getQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getQuestsAmount() + " quests in your globalQuest.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
        } else if (Modes.getQuestsMode() == 2) {
            /* load easy quests */
            loadQuests(easyQuestsFile, easyQuests, "Easy Quests");
            if (easyQuests.size() < QuestsAmount.getEasyQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getEasyQuestsAmount() + " quest in your easyQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
            /* load medium quests */
            loadQuests(mediumQuestsFile, mediumQuests, "Medium Quests");
            if (mediumQuests.size() < QuestsAmount.getMediumQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getMediumQuestsAmount() + " quest in your mediumQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
            /* load hard quests */
            loadQuests(hardQuestsFile, hardQuests, "Hard Quests");
            if (hardQuests.size() < QuestsAmount.getHardQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getHardQuestsAmount() + " quest in your hardQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
        } else {
            PluginLogger.error(ChatColor.RED + "Impossible to load the quests. The selected mode is incorrect.");
        }
    }

    /**
     * Load quests from a file.
     *
     * @param file     file to check.
     * @param quests   list for quests.
     * @param fileName file name for PluginLogger.
     */
    public static void loadQuests(FileConfiguration file, ArrayList<AbstractQuest> quests, String fileName) {

        /* load quests */
        if (file.getConfigurationSection("quests") != null) {
            int questIndex = 0;
            for (String fileQuest : file.getConfigurationSection("quests").getKeys(false)) {

                ConfigurationSection questSection = file.getConfigurationSection("quests." + fileQuest);

                /* init variables (quest constructor) */
                AbstractQuest quest = null;
                String questName;
                List<String> questDesc;
                QuestType questType = null;
                List<ItemStack> requiredItems = new ArrayList<>();
                ItemStack customItem;
                List<EntityType> entityTypes = null;
                DyeColor dyeColor = null;
                String entityName = null;
                Villager.Profession profession = null;
                int villagerLevel = 0;
                ItemStack menuItem;
                int requiredAmount;
                int cmd = -1;

                /* reach type */
                Location location = null;
                int radius = 1;

                /* init variables (reward constructor) */
                Reward reward;
                RewardType rewardType;

                /* init quest items */
                questName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(questSection.getString(".name")));

                String presumedItem = questSection.getString(".menu_item");
                cmd = questSection.getInt(".custom_model_data");

                menuItem = getItemStackFromMaterial(presumedItem, fileName, questIndex, "menu_item", cmd);

                questDesc = questSection.getStringList(".description");
                for (String string : questDesc) {
                    questDesc.set(questDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(string)));
                }

                try {
                    questType = QuestType.valueOf(questSection.getString(".quest_type"));
                } catch (Exception e) {
                    PluginLogger.error("-----------------------------------");
                    PluginLogger.error("Invalid quest type detected.");
                    PluginLogger.error("File : " + fileName);
                    PluginLogger.error("Quest number : " + (questIndex + 1));
                    PluginLogger.error("Parameter : quest_type");
                    PluginLogger.error("Value : " + questSection.getString(".quest_type"));
                    PluginLogger.error("-----------------------------------");
                }

                if (questType != null) {
                    boolean isGlobalType = false;
                    boolean isEntityType = false;

                    switch (questType) {
                        /* type that does not require a specific entity/item */
                        case MILKING, EXP_POINTS, EXP_LEVELS, CARVE, PLAYER_DEATH, FIREBALL_REFLECT -> {
                            isGlobalType = true;
                        }
                        /* type that require a custom mob */
                        case CUSTOM_MOBS -> {
                            isEntityType = true;
                            entityName = ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(questSection.getString(".entity_name")));
                        }
                        /* types that requires an entity */
                        case KILL, BREED, TAME, SHEAR -> {
                            isEntityType = true;
                            if (questSection.contains(".required_entity")) {
                                entityTypes = new ArrayList<>();

                                if (questSection.isString(".required_entity")) entityTypes.add(EntityType.valueOf(questSection.getString(".required_entity")));
                                else {
                                    for (String presumedEntity : questSection.getStringList(".required_entity")) {
                                        EntityType entityType = getEntityType(presumedEntity, fileName, questIndex, presumedEntity);
                                        entityTypes.add(entityType);

                                        if (entityType == EntityType.SHEEP) {
                                            if (questSection.contains(".sheep_color")) {
                                                String presumedDyeColor = questSection.getString(".sheep_color");
                                                dyeColor = getDyeColor(presumedDyeColor, fileName, questIndex, presumedDyeColor);
                                            }
                                        }
                                    }
                                }
                            } else isGlobalType = true;
                        }
                        /* types that requires an item */
                        case BREAK, PLACE, CRAFT, PICKUP, LAUNCH, CONSUME, GET, COOK, ENCHANT, VILLAGER_TRADE, FISH, FARMING -> {
                            if (questSection.contains(".required_item")) {

                                if (questSection.isString(".required_item")) {
                                    String itemType = questSection.getString(".required_item");

                                    /* check if the required item is a custom item */
                                    if (itemType.equals("CUSTOM_ITEM")) {
                                        ConfigurationSection section = file.getConfigurationSection("quests." + fileQuest + ".custom_item");
                                        customItem = getItemStackFromMaterial(section.getString(".type"), fileName, questIndex, "type (CUSTOM_ITEM)", -1);
                                        ItemMeta meta = customItem.getItemMeta();
                                        meta.setDisplayName(ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString(".name"))));
                                        List<String> lore = section.getStringList(".lore");
                                        for (String str : lore) {
                                            lore.set(lore.indexOf(str), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(str)));
                                        }
                                        meta.setLore(lore);
                                        customItem.setItemMeta(meta);

                                        requiredItems.add(customItem);
                                    } else {
                                        requiredItems.add(getItemStackFromMaterial(itemType, fileName, questIndex, "required_item", -1));
                                    }
                                } else {
                                    for (String itemType : questSection.getStringList(".required_item")) {
                                        ItemStack itemStack = getItemStackFromMaterial(itemType, fileName, questIndex, "required_item", cmd);
                                        requiredItems.add(itemStack);
                                    }
                                }

                                for (String itemType : questSection.getStringList(".required_item")) {
                                        requiredItems.add(getItemStackFromMaterial(itemType, fileName, questIndex, "required_item", -1));
                                }
                            } else isGlobalType = true;

                            /* check if the item have to be obtained by a villager */
                            if (questType == QuestType.VILLAGER_TRADE) {
                                if (questSection.contains(".villager_profession")) {
                                    profession = Villager.Profession.valueOf(questSection.getString(".villager_profession"));
                                }
                                if (questSection.contains(".villager_level")) {
                                    villagerLevel = questSection.getInt(".villager_level");
                                }
                            }
                        }
                        /* type that requires a location */
                        case LOCATION -> {
                            final ConfigurationSection section = file.getConfigurationSection("quests." + fileQuest + ".location");

                            if (section == null) {
                                PluginLogger.error("-----------------------------------");
                                PluginLogger.error("Invalid quest configuration detected.");
                                PluginLogger.error("File : " + fileName);
                                PluginLogger.error("Quest number : " + (questIndex + 1));
                                PluginLogger.error("You need to specify a location.");
                                PluginLogger.error("-----------------------------------");

                                location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
                            } else {
                                final String wd = section.getString(".world");
                                final int x = section.getInt(".x");
                                final int y = section.getInt(".y");
                                final int z = section.getInt(".z");

                                radius = section.getInt(".radius");
                                final World world = Bukkit.getWorld(wd);
                                if (world == null) {
                                    PluginLogger.error("-----------------------------------");
                                    PluginLogger.error("Invalid quest configuration detected.");
                                    PluginLogger.error("File : " + fileName);
                                    PluginLogger.error("Quest number : " + (questIndex + 1));
                                    PluginLogger.error("The world specified in the location is not loaded.");
                                    PluginLogger.error("-----------------------------------");   
                                    
                                    location = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
                                } else {
                                    location = new Location(world, x, y, z);
                                }
                            }
                        }
                    }

                    if (questType == QuestType.LOCATION) requiredAmount = 1;
                    else requiredAmount = questSection.getInt(".required_amount");

                    /* init reward */
                    rewardType = RewardType.valueOf(file.getConfigurationSection("quests." + fileQuest + ".reward").getString(".reward_type"));

                    if (rewardType == RewardType.COMMAND) {
                        reward = new Reward(rewardType, file.getConfigurationSection("quests." + fileQuest + ".reward").getStringList(".commands"));
                    } else {
                        reward = new Reward(rewardType, file.getConfigurationSection("quests." + fileQuest + ".reward").getInt(".amount"));
                    }

                    /* init quest */
                    final GlobalQuest base = new GlobalQuest(questIndex, questName, questDesc, questType, menuItem, requiredAmount, reward);
                    if (isGlobalType) {
                        if (questType == QuestType.VILLAGER_TRADE) {
                            quest = new VillagerQuest(base, profession, villagerLevel);
                        } else quest = base;
                    } else if (isEntityType) {
                        if (questType == QuestType.CUSTOM_MOBS) {
                            if (EliteMobsHook.isEliteMobsSetup() || MythicMobsHook.isMythicMobsSetup()) {
                                quest = new EntityQuest(base, entityTypes, null, entityName);
                            } else {
                                PluginLogger.error("File : " + fileName);
                                PluginLogger.error("Quest at index " + (questIndex + 1) + " cannot be loaded !");
                                PluginLogger.error("There is no compatible plugin found for quest type CUSTOM_MOBS.");
                            }
                        } else {
                            quest = new EntityQuest(base, entityTypes, dyeColor, entityName);
                        }
                    } else {
                        if (questType == QuestType.VILLAGER_TRADE) {
                            quest = new VillagerQuest(base, requiredItems, profession, villagerLevel);
                        } else if (questType == QuestType.LOCATION) {
                            quest = new LocationQuest(base, location, radius);
                        }
                        else {
                            quest = new ItemQuest(base, requiredItems);
                        }
                    }

                    /* add quest to the list */
                    if (quest != null && menuItem != null) {
                        quests.add(quest);
                        questIndex++;
                    } else {
                        PluginLogger.error("File : " + fileName);
                        PluginLogger.error("Quest at index " + (questIndex + 1) + " cannot be loaded!");
                        PluginLogger.error("Check previous logs for more details.");
                    }
                }
            }
            PluginLogger.info(ChatColor.GREEN + fileName + " array successfully loaded (" + ChatColor.YELLOW + quests.size() + ChatColor.GREEN + ").");
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to load " + fileName + " : there is no quests in " + fileName + " file !");
    }

    /**
     * @param material
     * @param fileName
     * @param questIndex
     * @return
     */
    private static ItemStack getItemStackFromMaterial(String material, String fileName, int questIndex, String parameter, int modelData) {
        ItemStack requiredItem = null;
        try {
            requiredItem = new ItemStack(Material.valueOf(material));

            if (modelData != -1) {
                final ItemMeta meta = requiredItem.getItemMeta();
                meta.setCustomModelData(modelData);
                requiredItem.setItemMeta(meta);
            }
        } catch (Exception e) {
            PluginLogger.error("-----------------------------------");
            PluginLogger.error("Invalid material type detected.");
            PluginLogger.error("File : " + fileName);
            PluginLogger.error("Quest number : " + (questIndex + 1));
            PluginLogger.error("Parameter : " + parameter);
            PluginLogger.error("Value : " + material);
            PluginLogger.error("-----------------------------------");
        }
        return requiredItem;
    }

    /**
     * @param entity
     * @param fileName
     * @param questIndex
     * @param value
     * @return
     */
    private static EntityType getEntityType(String entity, String fileName, int questIndex, String value) {
        EntityType entityType = null;
        try {
            entityType = EntityType.valueOf(entity);
        } catch (Exception e) {
            PluginLogger.error("-----------------------------------");
            PluginLogger.error("Invalid entity type detected.");
            PluginLogger.error("File : " + fileName);
            PluginLogger.error("Quest number : " + (questIndex + 1));
            PluginLogger.error("Parameter : required_entity");
            PluginLogger.error("Value : " + value);
            PluginLogger.error("-----------------------------------");
        }
        return entityType;
    }

    /**
     * @param dye
     * @param fileName
     * @param questIndex
     * @param value
     * @return
     */
    private static DyeColor getDyeColor(String dye, String fileName, int questIndex, String value) {
        DyeColor dyeColor = null;
        try {
            dyeColor = DyeColor.valueOf(dye.toUpperCase());
        } catch (Exception e) {
            PluginLogger.error("-----------------------------------");
            PluginLogger.error("Invalid dye type detected.");
            PluginLogger.error("File : " + fileName);
            PluginLogger.error("Quest number : " + (questIndex + 1));
            PluginLogger.error("Parameter : sheep_color");
            PluginLogger.error("Value : " + value);
            PluginLogger.error("-----------------------------------");
        }
        return dyeColor;
    }

    /**
     * Get global quests.
     */
    public static ArrayList<AbstractQuest> getGlobalQuests() {
        return globalQuests;
    }

    /**
     * Get easy quests.
     */
    public static ArrayList<AbstractQuest> getEasyQuests() {
        return easyQuests;
    }

    /**
     * Get medium quests.
     */
    public static ArrayList<AbstractQuest> getMediumQuests() {
        return mediumQuests;
    }

    /**
     * Get hard quests.
     */
    public static ArrayList<AbstractQuest> getHardQuests() {
        return hardQuests;
    }
}
