package com.ordwen.odailyquests.events.listeners.item.custom;

import net.momirealms.customcrops.api.event.CropBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CropBreakListener implements Listener {

    @EventHandler
    public void onCropBreak(CropBreakEvent event) {
        System.out.println("CropBreakEvent");
        System.out.println("CROP ITEM ID: " + event.getCropItemID());
        System.out.println("CROP KEY: " + event.getCropKey());
        System.out.println("BREAKER: " + event.getEntity() + ", is player: " + (event.getEntity() instanceof org.bukkit.entity.Player));
    }
}
