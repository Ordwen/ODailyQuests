package com.ordwen.odailyquests.commands.interfaces.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public record CategoryHolder(int page, String category) implements InventoryHolder {

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
