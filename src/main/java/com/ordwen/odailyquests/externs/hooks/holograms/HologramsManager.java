package com.ordwen.odailyquests.externs.hooks.holograms;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.HologramsFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
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
        ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
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

        ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
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
        ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
            try {
                HologramsFile.getHologramsFileConfiguration().save(HologramsFile.getHologramsFile());
                PluginLogger.info("Holograms file successfully saved.");
            } catch (IOException e) {
                PluginLogger.error("An error happened on the save of the progression file.");
                PluginLogger.error("If the problem persists, contact the developer.");
                PluginLogger.error(e.getMessage());
            }
        });
    }

    /**
     * Load all holograms from file.
     */
    public static void loadHolograms() {
        if (HolographicDisplaysHook.isHolographicDisplaysSetup()) {
            ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
                final FileConfiguration file = HologramsFile.getHologramsFileConfiguration();
                for (String holo : file.getConfigurationSection("").getKeys(false)) {
                    Location loc = file.getLocation(holo + ".location");
                    List<String> lines = file.getStringList(holo + ".lines");
                    ItemStack item = file.getItemStack(holo + ".item");

                    HolographicDisplaysHook.displayHologram(loc, lines, item);
                }
            });
            PluginLogger.info("Holograms successfully loaded.");
        }
    }
}
