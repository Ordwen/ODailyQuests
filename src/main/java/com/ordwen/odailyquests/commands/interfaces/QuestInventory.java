package com.ordwen.odailyquests.commands.interfaces;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class QuestInventory implements InventoryHolder {

    @Getter
    private Inventory inventory;

    public QuestInventory(int size, String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }
}
