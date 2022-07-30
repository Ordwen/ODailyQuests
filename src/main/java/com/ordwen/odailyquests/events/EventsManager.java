package com.ordwen.odailyquests.events;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.apis.hooks.mobs.EliteMobsHook;
import com.ordwen.odailyquests.apis.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.apis.hooks.stackers.WildStackerHook;
import com.ordwen.odailyquests.events.listeners.entity.*;
import com.ordwen.odailyquests.events.listeners.entity.custom.EliteMobDeathListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.EntityUnstackListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.MythicMobDeathListener;
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
        Bukkit.getPluginManager().registerEvents(new EntityBreadListener(), oDailyQuests);
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

        // item events
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new CraftItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new EnchantItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new FurnaceExtractListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PickupItemListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerFishListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new PlayerItemConsumeListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new ProjectileLaunchListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), oDailyQuests);
        Bukkit.getPluginManager().registerEvents(new BlockDropItemListener(), oDailyQuests);

        // inventory events
        Bukkit.getPluginManager().registerEvents(new InventoryCloseListener(), oDailyQuests);
    }
}
