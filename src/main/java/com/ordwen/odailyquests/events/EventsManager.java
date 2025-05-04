package com.ordwen.odailyquests.events;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.NexoEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.events.listeners.crate.CrateOpenListener;
import com.ordwen.odailyquests.events.listeners.customs.CustomFurnaceExtractListener;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.CropBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.FishingLootSpawnListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.CustomBlockBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.ItemsAdderLoadDataListener;
import com.ordwen.odailyquests.events.listeners.integrations.nexo.NexoItemsLoadedListener;
import com.ordwen.odailyquests.events.listeners.integrations.oraxen.OraxenItemsLoadedListener;
import com.ordwen.odailyquests.events.listeners.vote.VotifierListener;
import com.ordwen.odailyquests.externs.hooks.mobs.EliteMobsHook;
import com.ordwen.odailyquests.externs.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.externs.hooks.stackers.WildStackerHook;
import com.ordwen.odailyquests.events.listeners.entity.*;
import com.ordwen.odailyquests.events.listeners.entity.custom.mobs.EliteMobDeathListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.stackers.EntityUnstackListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.mobs.MythicMobDeathListener;
import com.ordwen.odailyquests.events.listeners.global.*;
import com.ordwen.odailyquests.events.listeners.inventory.InventoryClickListener;
import com.ordwen.odailyquests.events.listeners.inventory.InventoryCloseListener;
import com.ordwen.odailyquests.events.listeners.item.*;
import com.ordwen.odailyquests.tools.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventsManager {

    private final ODailyQuests oDailyQuests;

    public EventsManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Registers all events.
     */
    public void registerListeners() {

        final PluginManager pluginManager = Bukkit.getPluginManager();
        // entity events
        pluginManager.registerEvents(new EntityBreedListener(), oDailyQuests);
        pluginManager.registerEvents(new EntityTameListener(), oDailyQuests);
        pluginManager.registerEvents(new ShearEntityListener(), oDailyQuests);
        pluginManager.registerEvents(new EntityDeathListener(), oDailyQuests);
        pluginManager.registerEvents(new SpawnerSpawnListener(), oDailyQuests);

        if (EliteMobsHook.isEnabled()) {
            pluginManager.registerEvents(new EliteMobDeathListener(), oDailyQuests);
        }

        if (MythicMobsHook.isEnabled()) {
            pluginManager.registerEvents(new MythicMobDeathListener(), oDailyQuests);
        }

        if (WildStackerHook.isEnabled()) {
            pluginManager.registerEvents(new EntityUnstackListener(), oDailyQuests);
        }

        // global events
        pluginManager.registerEvents(new BucketFillListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerExpChangeListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerLevelChangeListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerInteractListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerInteractEntityListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerDeathListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerRespawnListener(), oDailyQuests);

        // item events
        pluginManager.registerEvents(new BlockBreakListener(), oDailyQuests);
        pluginManager.registerEvents(new BlockPlaceListener(), oDailyQuests);
        pluginManager.registerEvents(new CraftItemListener(), oDailyQuests);
        pluginManager.registerEvents(new SmithItemListener(), oDailyQuests);
        pluginManager.registerEvents(new EnchantItemListener(), oDailyQuests);
        pluginManager.registerEvents(new FurnaceExtractListener(), oDailyQuests);
        pluginManager.registerEvents(new PickupItemListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerFishListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerItemConsumeListener(), oDailyQuests);
        pluginManager.registerEvents(new ProjectileLaunchListener(), oDailyQuests);
        pluginManager.registerEvents(new InventoryClickListener(oDailyQuests.getInterfacesManager().getPlayerQuestsInterface()), oDailyQuests);
        pluginManager.registerEvents(new BlockDropItemListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerHarvestBlockListener(), oDailyQuests);
        pluginManager.registerEvents(new PlayerDropItemListener(), oDailyQuests);
        pluginManager.registerEvents(new StructureGrowListener(), oDailyQuests);

        // inventory events
        pluginManager.registerEvents(new InventoryCloseListener(), oDailyQuests);

        // custom events
        if (ItemsAdderEnabled.isEnabled()
                || OraxenEnabled.isEnabled()
                || NexoEnabled.isEnabled()
                || CustomFurnaceResults.isEnabled()) {
            pluginManager.registerEvents(new CustomFurnaceExtractListener(), oDailyQuests);
        }

        // other plugins events
        if (ItemsAdderEnabled.isEnabled()) {
            pluginManager.registerEvents(new ItemsAdderLoadDataListener(oDailyQuests), oDailyQuests);
            pluginManager.registerEvents(new CustomBlockBreakListener(), oDailyQuests);
        }

        if (OraxenEnabled.isEnabled()) {
            pluginManager.registerEvents(new OraxenItemsLoadedListener(oDailyQuests), oDailyQuests);
        }

        if (NexoEnabled.isEnabled()) {
            pluginManager.registerEvents(new NexoItemsLoadedListener(oDailyQuests), oDailyQuests);
        }

        if (PluginUtils.isPluginEnabled("CustomCrops")) {
            pluginManager.registerEvents(new CropBreakListener(), oDailyQuests);
        }

        if (PluginUtils.isPluginEnabled("CustomFishing")) {
            pluginManager.registerEvents(new FishingLootSpawnListener(), oDailyQuests);
        }

        if (PluginUtils.isPluginEnabled("Votifier")) {
            pluginManager.registerEvents(new VotifierListener(), oDailyQuests);
        }

        if (PluginUtils.isPluginEnabled("ExcellentCrates")) {
            pluginManager.registerEvents(new CrateOpenListener(), oDailyQuests);
        }
    }
}
