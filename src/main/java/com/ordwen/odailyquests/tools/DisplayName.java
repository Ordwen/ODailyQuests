package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.ChatColor;

public class DisplayName {

    private DisplayName() {}

    public static String getDisplayName(AbstractQuest quest, int index) {
        if (!quest.isRandomRequired()) {
            return ChatColor.RED + "Invalid usage.";
        }

        String displayName = null;
        if (quest instanceof EntityQuest entityQuest) {
            displayName = entityQuest.getSelectedDisplayName(index);
        } else if (quest instanceof ItemQuest itemQuest) {
            displayName = itemQuest.getSelectedDisplayName(index);
        }

        if (displayName == null) return ChatColor.RED + "Invalid usage.";
        return displayName;
    }
}
