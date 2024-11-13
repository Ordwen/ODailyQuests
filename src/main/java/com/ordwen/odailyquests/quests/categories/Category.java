package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.quests.types.AbstractQuest;

import java.util.ArrayList;

public class Category extends ArrayList<AbstractQuest> {

    private final String name;

    public Category(String name) {
        this.name = name;
    }

    /**
     * Get the name of the category.
     *
     * @return name of the category.
     */
    public String getName() {
        return this.name;
    }
}
