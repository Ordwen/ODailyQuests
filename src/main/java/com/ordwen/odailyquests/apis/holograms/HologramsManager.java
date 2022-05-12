package com.ordwen.odailyquests.apis.holograms;

import com.ordwen.odailyquests.files.HologramsFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class HologramsManager {

    /**
     * Getting instance of classes.
     */
    private static HologramsFile hologramsFile;

    /**
     * Class instance constructor.
     * @param hologramsFile file class.
     */
    public HologramsManager(HologramsFile hologramsFile) {
        HologramsManager.hologramsFile = hologramsFile;
    }

    /**
     * Save the hologram into the file.
     * @param index of the hologram
     * @param location location of the hologram
     * @param lines of the hologram
     * @param item of the linked quest
     */
    public static void saveHologram(int index, Location location, List<String> lines, ItemStack item) {
        hologramsFile.getHologramsFileConfiguration().set(index + ".location", location);
        hologramsFile.getHologramsFileConfiguration().set(index + ".lines", lines);
        hologramsFile.getHologramsFileConfiguration().set(index + ".item", item);

        saveFile();
    }

    /**
     * Delete a hologram from file.
     * @param index of the hologram
     */
    public static boolean deleteHologram(int index) {
        if (hologramsFile.getHologramsFileConfiguration().getConfigurationSection(String.valueOf(index)) != null) {
            hologramsFile.getHologramsFileConfiguration().set(String.valueOf(index), null);
            HolographicDisplaysHook.getAllHolograms().get(index).delete();
            HolographicDisplaysHook.getAllHolograms().remove(index);

            saveFile();
            return true;
        }
        return false;
    }

    private static void saveFile() {
        try {
            hologramsFile.getHologramsFileConfiguration().save(hologramsFile.getHologramsFile());
            PluginLogger.info(ChatColor.GREEN + "Holograms file successfully saved.");
        } catch (IOException e) {
            PluginLogger.info(ChatColor.RED + "An error happened on the save of the progression file.");
            PluginLogger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            e.printStackTrace();
        }
    }
}
