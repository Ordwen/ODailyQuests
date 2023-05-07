package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.NamespacedKey;

public class Antiglitch {

    public static boolean storeItems;
    public static NamespacedKey BROKEN_KEY = new NamespacedKey(ODailyQuests.INSTANCE, "broken");
    public static NamespacedKey DROPPED_BY = new NamespacedKey(ODailyQuests.INSTANCE, "dropped");

}
