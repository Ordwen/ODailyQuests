package com.ordwen.odailyquests.quests.player.progression.clickable;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestContext {
    private final @NotNull Player player;
    private final @Nullable ItemStack clickedItem;
    private final @Nullable Villager villager;
    private final @Nullable MerchantRecipe selectedRecipe;
    private final int quantity;

    private QuestContext(Builder builder) {
        this.player = builder.player;
        this.clickedItem = builder.clickedItem;
        this.villager = builder.villager;
        this.selectedRecipe = builder.selectedRecipe;
        this.quantity = builder.quantity;
    }

    public static class Builder {
        private final @NotNull Player player;
        private @Nullable ItemStack clickedItem;
        private @Nullable Villager villager;
        private @Nullable MerchantRecipe selectedRecipe;
        private int quantity = 1;

        public Builder(@NotNull Player player) {
            this.player = player;
        }

        public Builder clickedItem(@Nullable ItemStack clickedItem) {
            this.clickedItem = clickedItem;
            return this;
        }

        public Builder villagerTrade(@Nullable Villager villager, @Nullable MerchantRecipe selectedRecipe, int quantity) {
            this.villager = villager;
            this.selectedRecipe = selectedRecipe;
            this.quantity = quantity;
            return this;
        }

        public QuestContext build() {
            return new QuestContext(this);
        }
    }

    public @NotNull Player getPlayer() { return player; }
    public @Nullable ItemStack getClickedItem() { return clickedItem; }
    public @Nullable Villager getVillager() { return villager; }
    public @Nullable MerchantRecipe getSelectedRecipe() { return selectedRecipe; }
    public int getQuantity() { return quantity; }

}
