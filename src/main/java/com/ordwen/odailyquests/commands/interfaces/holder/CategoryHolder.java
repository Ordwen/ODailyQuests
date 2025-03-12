package com.ordwen.odailyquests.commands.interfaces.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CategoryHolder implements InventoryHolder {

    private final int page;
    private final String category;

    public CategoryHolder(int page, String category) {
        this.page = page;
        this.category = category;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getPage() {
        return page;
    }

    public String getCategory() {
        return category;
    }
}
