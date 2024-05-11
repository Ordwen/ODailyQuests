package com.ordwen.odailyquests.commands.interfaces;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InterfaceInventory implements InventoryHolder {

    @Getter
    private Inventory inventory;
    @Getter
    private String category;
    @Getter
    private int page;

    public InterfaceInventory(int size, String title, String category, int page) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.category = category;
        this.page = page;
    }
}
