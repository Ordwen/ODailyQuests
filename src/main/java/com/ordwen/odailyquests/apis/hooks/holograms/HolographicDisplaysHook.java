package com.ordwen.odailyquests.apis.hooks.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HolographicDisplaysHook {

    /* store all holograms */
    private static final List<Hologram> allHolograms = new ArrayList<>();

    /**
     * Check if HolographicDisplays is enabled.
     * @return true if HolographicDisplays is enabled.
     */
    public static boolean isHolographicDisplaysSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    /**
     * Create a hologram from a quest.
     * @param index of the quest
     * @param list where to find the quest
     * @param player sender
     */
    public static void createHologram(int index, ArrayList<AbstractQuest> list, Player player) {
        AbstractQuest quest = getQuest(index, list);

        if (quest != null) {
            /* init items to save */
            // hologram location
            Location loc = player.getLocation();
            // hologram text lines
            List<String> lines = new ArrayList<>();
            lines.add(quest.getQuestName());
            lines.add("");
            lines.addAll(quest.getQuestDesc());
            lines.add("");
            // hologram item line
            ItemStack item = quest.getMenuItem();

            // save the hologram on the file
            HologramsManager.saveHologram(allHolograms.size(), loc, lines, item);

            // display hologram and add it to the list
            displayHologram(loc, lines, item);

        } else {
            final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
            if (msg != null) player.sendMessage(msg);
        }
    }

    /**
     * Display the hologram.
     * @param location of the hologram
     * @param lines of the hologram
     * @param item of the linked quest
     */
    public static void displayHologram(Location location, List<String> lines, ItemStack item) {
        Hologram hologram = HologramsAPI.createHologram(Bukkit.getPluginManager().getPlugin("ODailyQuests"), location);
        for (String str : lines) {
            hologram.appendTextLine(str);
        }
        hologram.appendItemLine(item);

        allHolograms.add(hologram);
    }

    /**
     * Get quest by index & list.
     * @param index of the quest
     * @param quests list where find the quest
     * @return the quest
     */
    private static AbstractQuest getQuest(int index, ArrayList<AbstractQuest> quests) {
        if (index >= 0 && quests.size()-1 >= index) {
            return quests.get(index);
        } else return null;
    }

    /**
     * Get the holograms list.
     * @return list
     */
    public static List<Hologram> getAllHolograms() {
        return allHolograms;
    }
}
