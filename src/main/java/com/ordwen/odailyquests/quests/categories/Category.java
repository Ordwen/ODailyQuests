package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import java.util.ArrayList;

public class Category extends ArrayList<AbstractQuest> {

    private final String name;
    private String groupName;

    public Category(String name) {
        this.name = name;
    }

    /**
     * Get the name of the category.
     * @return name of the category.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the name of the group this category belongs to.
     * @return group name, or null if not assigned to a group (legacy mode)
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Set the name of the group this category belongs to.
     * @param groupName the group name
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
