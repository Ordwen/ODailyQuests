package com.ordwen.odailyquests.apis.hooks.holograms;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.HologramsFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HologramsManager {

    /**
     * Save the hologram into the file.
     *
     * @param index    of the hologram
     * @param location location of the hologram
     * @param lines    of the hologram
     * @param item     of the linked quest
     */
    public static void saveHologram(int index, Location location, List<String> lines, ItemStack item) {
        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            HologramsFile.getHologramsFileConfiguration().set(index + ".location", location);
            HologramsFile.getHologramsFileConfiguration().set(index + ".lines", lines);
            HologramsFile.getHologramsFileConfiguration().set(index + ".item", item);

            saveFile();
        });
    }

    /**
     * Delete a hologram from file.
     *
     * @param index of the hologram
     */
    public static boolean deleteHologram(int index) {
        AtomicBoolean isValid = new AtomicBoolean(false);

        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            if (HologramsFile.getHologramsFileConfiguration().getConfigurationSection(String.valueOf(index)) != null) {
                HologramsFile.getHologramsFileConfiguration().set(String.valueOf(index), null);
                HolographicDisplaysHook.getAllHolograms().get(index).delete();
                HolographicDisplaysHook.getAllHolograms().remove(index);

                saveFile();
                isValid.set(true);
            }
        });

        return isValid.get();
    }

    /**
     * Save holograms file.
     */
    private static void saveFile() {
        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            try {
                HologramsFile.getHologramsFileConfiguration().save(HologramsFile.getHologramsFile());
                PluginLogger.info(ChatColor.GREEN + "Holograms file successfully saved.");
            } catch (IOException e) {
                PluginLogger.info(ChatColor.RED + "An error happened on the save of the progression file.");
                PluginLogger.info(ChatColor.RED + "If the problem persists, contact the developer.");
                e.printStackTrace();
            }
        });
    }

    /**
     * Load all holograms from file.
     */
    public static void loadHolograms() {
        if (HolographicDisplaysHook.isHolographicDisplaysSetup()) {
            Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
                final FileConfiguration file = HologramsFile.getHologramsFileConfiguration();
                for (String holo : file.getConfigurationSection("").getKeys(false)) {
                    Location loc = file.getLocation(holo + ".location");
                    List<String> lines = file.getStringList(holo + ".lines");
                    ItemStack item = file.getItemStack(holo + ".item");

                    HolographicDisplaysHook.displayHologram(loc, lines, item);
                }
            });
            PluginLogger.info(ChatColor.GREEN + "Holograms successfully loaded.");
        }
    }
}
