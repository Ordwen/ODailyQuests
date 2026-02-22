package com.ordwen.odailyquests.events;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.NexoEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.events.listeners.crate.CrateOpenListener;
import com.ordwen.odailyquests.events.listeners.customs.CustomFurnaceExtractListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.stackers.RoseStackerListener;
import com.ordwen.odailyquests.events.listeners.integrations.emf.EMFFishCaughtListener;
import com.ordwen.odailyquests.events.listeners.integrations.npcs.CitizensHook;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.CropBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.customsuite.FishingLootSpawnListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.CustomBlockBreakListener;
import com.ordwen.odailyquests.events.listeners.integrations.itemsadder.ItemsAdderLoadDataListener;
import com.ordwen.odailyquests.events.listeners.integrations.nexo.NexoItemsLoadedListener;
import com.ordwen.odailyquests.events.listeners.integrations.npcs.FancyNpcsHook;
import com.ordwen.odailyquests.events.listeners.integrations.oraxen.OraxenItemsLoadedListener;
import com.ordwen.odailyquests.events.listeners.item.custom.CraftMMOItemListener;
import com.ordwen.odailyquests.events.listeners.item.custom.CustomPlayerFishListener;
import com.ordwen.odailyquests.events.listeners.item.custom.DropQueuePushListener;
import com.ordwen.odailyquests.events.listeners.vote.VotifierListener;
import com.ordwen.odailyquests.events.listeners.entity.*;
import com.ordwen.odailyquests.events.listeners.entity.custom.mobs.EliteMobDeathListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.stackers.WildStackerListener;
import com.ordwen.odailyquests.events.listeners.entity.custom.mobs.MythicMobDeathListener;
import com.ordwen.odailyquests.events.listeners.global.*;
import com.ordwen.odailyquests.events.listeners.inventory.InventoryClickListener;
import com.ordwen.odailyquests.events.listeners.inventory.InventoryCloseListener;
import com.ordwen.odailyquests.events.listeners.item.*;
import com.ordwen.odailyquests.tools.PluginLogger;
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

        registerBukkitNativeListeners(pluginManager);
        registerCustomEvents(pluginManager);
        registerPackIntegrations(pluginManager);
        registerPluginListeners(pluginManager);
    }

    private void registerBukkitNativeListeners(final PluginManager pluginManager) {
        // entity events
        pluginManager.registerEvents(new EntityBreedListener(), oDailyQuests);
        pluginManager.registerEvents(new EntityTameListener(), oDailyQuests);
        pluginManager.registerEvents(new ShearEntityListener(), oDailyQuests);
        pluginManager.registerEvents(new EntityDeathListener(), oDailyQuests);
        pluginManager.registerEvents(new SpawnerSpawnListener(), oDailyQuests);

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
    }

    private void registerCustomEvents(final PluginManager pluginManager) {
        if (ItemsAdderEnabled.isEnabled()
                || OraxenEnabled.isEnabled()
                || NexoEnabled.isEnabled()
                || CustomFurnaceResults.isEnabled()) {
            pluginManager.registerEvents(new CustomFurnaceExtractListener(), oDailyQuests);
        }
    }

    private void registerPackIntegrations(final PluginManager pluginManager) {
        if (ItemsAdderEnabled.isEnabled()) {
            registerSafely(() -> {
                pluginManager.registerEvents(new ItemsAdderLoadDataListener(oDailyQuests), oDailyQuests);
                pluginManager.registerEvents(new CustomBlockBreakListener(), oDailyQuests);
            }, "ItemsAdder");
        }

        if (OraxenEnabled.isEnabled()) {
            registerSafely(() -> pluginManager.registerEvents(new OraxenItemsLoadedListener(oDailyQuests), oDailyQuests), "Oraxen");
        }

        if (NexoEnabled.isEnabled()) {
            registerSafely(() -> pluginManager.registerEvents(new NexoItemsLoadedListener(oDailyQuests), oDailyQuests), "Nexo");
        }
    }

    private void registerPluginListeners(final PluginManager pluginManager) {
        // Third-party plugin hooks with safety wrapper
        registerIfPluginEnabled("EliteMobs", () -> pluginManager.registerEvents(new EliteMobDeathListener(), oDailyQuests));
        registerIfPluginEnabled("MythicMobs", () -> pluginManager.registerEvents(new MythicMobDeathListener(), oDailyQuests));
        registerIfPluginEnabled("WildStacker", () -> pluginManager.registerEvents(new WildStackerListener(), oDailyQuests));
        registerIfPluginEnabled("RoseStacker", () -> pluginManager.registerEvents(new RoseStackerListener(), oDailyQuests));
        registerIfPluginEnabled("CustomCrops", () -> pluginManager.registerEvents(new CropBreakListener(), oDailyQuests));
        registerIfPluginEnabled("CustomFishing", () -> pluginManager.registerEvents(new FishingLootSpawnListener(), oDailyQuests));
        registerIfPluginEnabled("ExcellentCrates", () -> pluginManager.registerEvents(new CrateOpenListener(), oDailyQuests));
        registerIfPluginEnabled("Citizens", () -> pluginManager.registerEvents(new CitizensHook(oDailyQuests.getInterfacesManager()), oDailyQuests));
        registerIfPluginEnabled("FancyNpcs", () -> pluginManager.registerEvents(new FancyNpcsHook(oDailyQuests.getInterfacesManager()), oDailyQuests));
        registerIfPluginEnabled("eco", () -> pluginManager.registerEvents(new DropQueuePushListener(), oDailyQuests));
        registerIfPluginEnabled("MMOCore", () -> pluginManager.registerEvents(new CustomPlayerFishListener(), oDailyQuests));
        registerIfPluginEnabled("MMOItems", () -> pluginManager.registerEvents(new CraftMMOItemListener(), oDailyQuests));
        registerIfPluginEnabled("EvenMoreFish", () -> pluginManager.registerEvents(new EMFFishCaughtListener(), oDailyQuests));

        registerIfAnyPluginEnabled(
                () -> pluginManager.registerEvents(new VotifierListener(), oDailyQuests),
                "Votifier", "VotifierPlus", "NuVotifier"
        );
    }

    private void registerIfPluginEnabled(final String pluginName, final Runnable registerAction) {
        if (!PluginUtils.isPluginEnabled(pluginName)) return;
        registerSafely(registerAction, pluginName);
    }

    private void registerIfAnyPluginEnabled(final Runnable registerAction, final String... pluginNames) {
        for (String pluginName : pluginNames) {
            if (PluginUtils.isPluginEnabled(pluginName)) {
                registerSafely(registerAction, pluginName);
                return; // register only once
            }
        }
    }

    private void registerSafely(final Runnable registerAction, final String prettyName) {
        try {
            registerAction.run();
        } catch (NoClassDefFoundError e) {
            PluginLogger.warn("Cannot hook into " + prettyName + " events. This is usually caused by an outdated " + prettyName + " version.");
            PluginLogger.warn("If the problem persists, please contact the plugin developer.");
        }
    }
}