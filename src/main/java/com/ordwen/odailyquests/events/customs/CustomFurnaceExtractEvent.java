package com.ordwen.odailyquests.events.customs;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomFurnaceExtractEvent extends Event {

    private final Player player;
    private final ItemStack result;
    private final int amount;

    public CustomFurnaceExtractEvent(Player player, ItemStack result, int amount) {
        this.player = player;
        this.result = result;
        this.amount = amount;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getAmount() {
        return amount;
    }
}
