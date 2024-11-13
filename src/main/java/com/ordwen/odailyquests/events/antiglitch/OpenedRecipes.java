package com.ordwen.odailyquests.events.antiglitch;

import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;

public class OpenedRecipes {

    /* Stock all recipes and their current uses from open villagers menus */
    private static final HashMap<MerchantRecipe, Integer> openRecipes = new HashMap<>();

    /**
     * Add recipes to the map when a villager is clicked
     *
     * @param recipe recipe to add to the map
     * @param i      quantity of uses of the recipe
     */
    public static void put(MerchantRecipe recipe, int i) {
        openRecipes.put(recipe, i);
    }

    /**
     * Remove recipes from the map when villager menu is closed
     *
     * @param recipe recipe to remove from the map
     */
    public static void remove(MerchantRecipe recipe) {
        openRecipes.remove(recipe);
    }

    /**
     * Get the current uses of a recipe
     *
     * @param recipe recipe to get the uses of
     * @return current uses of the recipe
     */
    public static int get(MerchantRecipe recipe) {
        return openRecipes.get(recipe);
    }
}
