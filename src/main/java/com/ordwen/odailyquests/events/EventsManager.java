package com.ordwen.odailyquests.events;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.events.listeners.crate.CrateOpenListener;
import com.ordwen.odailyquests.events.listeners.customs.CustomFurnaceExtractListener;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.CropBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.FishingLootSpawnListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.CustomBlockBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.ItemsAdderLoadDataListener;
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
import org.bukkit.Bukkit;

public class EventsManager {

    private final ODailyQuests oDailyQuests;

    public EventsManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Registers all events.
     */
    public void registerListeners() {

        // entity events
        Bukkit.getPluginManager().registerEvents(new EntityBreedListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new EntityTameListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new ShearEntityListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new SpawnerSpawnListener(), oDailyQuests);

        if (EliteMobsHook.isEliteMobsSetup()) {
            Bukkit.getPluginManager().registerEvents(new EliteMobDeathListener(), oDailyQuests);
        }

        if (MythicMobsHook.isMythicMobsSetup()) {
            Bukkit.getPluginManager().registerEvents(new MythicMobDeathListener(), oDailyQuests);
        }

        if (WildStackerHook.isWildStackerSetup()) {
            Bukkit.getPluginManager().registerEvents(new EntityUnstackListener(), oDailyQuests);
        }

        // global events
        Bukkit.getPluginManager().registerEvents(new BucketFillListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerExpChangeListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerLevelChangeListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEntityListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), oDailyQuests);

        // item events
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new CraftItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new SmithItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new EnchantItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new FurnaceExtractListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PickupItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerFishListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerItemConsumeListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunchListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(oDailyQuests.getInterfacesManager().getPlayerQuestsInterface()), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new BlockDropItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerHarvestBlockListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new StructureGrowListener(), oDailyQuests);

        // inventory events
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), oDailyQuests);

        // custom events
        if (ItemsAdderEnabled.isEnabled()
                || OraxenEnabled.isEnabled()
                || CustomFurnaceResults.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(new CustomFurnaceExtractListener(), oDailyQuests);
        }

        // other plugins events
        if (ItemsAdderEnabled.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(new ItemsAdderLoadDataListener(oDailyQuests), oDailyQuests);
            Bukkit.getPluginManager().registerEvents(new CustomBlockBreakListener(), oDailyQuests);
        }

        if (OraxenEnabled.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(new OraxenItemsLoadedListener(oDailyQuests), oDailyQuests);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CustomCrops")) {
            Bukkit.getPluginManager().registerEvents(new CropBreakListener(), oDailyQuests);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CustomFishing")) {
            Bukkit.getPluginManager().registerEvents(new FishingLootSpawnListener(), oDailyQuests);
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Votifier")) {
            Bukkit.getPluginManager().registerEvents(new VotifierListener(), oDailyQuests);
        }

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("ExcellentCrates")) {
            Bukkit.getPluginManager().registerEvents(new CrateOpenListener(), oDailyQuests);
        }
    }
}
