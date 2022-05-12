package com.ordwen.odailyquests.apis.holograms;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.HologramsFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LoadHolograms {

    /**
     * Getting instance of classes.
     */
    private final HologramsFile hologramsFile;

    /**
     * Class instance constructor.
     * @param hologramsFile file class.
     */
    public LoadHolograms(HologramsFile hologramsFile) {
        this.hologramsFile = hologramsFile;
    }

    /**
     * Load all holograms from file.
     */
    public void loadHolograms() {
        FileConfiguration file = hologramsFile.getHologramsFileConfiguration();
        for (String holo : file.getConfigurationSection("").getKeys(false)) {
            Location loc = file.getLocation(holo + ".location");
            List<String> lines = file.getStringList(holo + ".lines");
            ItemStack item = file.getItemStack(holo + ".item");

            HolographicDisplaysHook.displayHologram(loc, lines, item);
        }

        PluginLogger.info(ChatColor.GREEN + "Holograms successfully loaded.");
    }
}
