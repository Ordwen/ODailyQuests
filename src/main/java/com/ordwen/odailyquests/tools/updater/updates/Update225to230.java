package com.ordwen.odailyquests.tools.updater.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.updater.IConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Update225to230 implements IConfigUpdater {

    @Override
    public void apply(ODailyQuests plugin) {
        final File file = new File(plugin.getDataFolder(), "config.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        addDefaultConfigItem("reroll_only_if_not_achieved", false, config, file);
        addDefaultConfigItem("shared_mobs", false, config, file);

        addDefaultConfigItem("progress_bar.symbol", "|", config, file);
        addDefaultConfigItem("progress_bar.completed_color", "&a", config, file);
        addDefaultConfigItem("progress_bar.remaining_color", "&7", config, file);
        addDefaultConfigItem("progress_bar.amount_of_symbols", 20, config, file);
    }
}
