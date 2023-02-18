package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.files.QuestsFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoUpdater {

    private final ODailyQuests plugin;

    public AutoUpdater(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdate() {
        final String configVersion = plugin.getConfig().getString("version");
        final String currentVersion = plugin.getDescription().getVersion();

        if (configVersion == null | !configVersion.equals(currentVersion)) {
            PluginLogger.warn("It looks like you were using an older version of the plugin. Let's update your files!");

            // --------------
            // 1.3.6 -> 2.0.0
            // --------------

            // PLAYER INTERFACE FILE

            PluginLogger.info("Updating playerInterface.yml...");
            final FileConfiguration pif = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration();
            final int slot1 = pif.getInt("first_quest_slot");
            final int slot2 = pif.getInt("second_quest_slot");
            final int slot3 = pif.getInt("third_quest_slot");

            pif.set("quests.1", slot1);
            pif.set("quests.2", slot2);
            pif.set("quests.3", slot3);

            try {
                pif.save(PlayerInterfaceFile.getPlayerInterfaceFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            PluginLogger.fine("Done!");

            // QUESTS FILES

            PluginLogger.info("Updating globalQuests.yml...");
            updateQuestsFile(QuestsFiles.getGlobalQuestsConfiguration(), QuestsFiles.getGlobalQuestsFile());
            PluginLogger.fine("Done!");

            PluginLogger.info("Updating easyQuests.yml...");
            updateQuestsFile(QuestsFiles.getEasyQuestsConfiguration(), QuestsFiles.getEasyQuestsFile());
            PluginLogger.fine("Done!");

            PluginLogger.info("Updating mediumQuests.yml...");
            updateQuestsFile(QuestsFiles.getMediumQuestsConfiguration(), QuestsFiles.getMediumQuestsFile());
            PluginLogger.fine("Done!");

            PluginLogger.info("Updating hardQuests.yml...");
            updateQuestsFile(QuestsFiles.getHardQuestsConfiguration(), QuestsFiles.getHardQuestsFile());
            PluginLogger.fine("Done!");

            // CONFIGURATION FILE

            PluginLogger.info("Updating config.yml...");

            final FileConfiguration config = plugin.getConfigurationFiles().getConfigFile();
            final File file = plugin.getConfigurationFiles().getFile();

            AddDefault.addDefaultConfigItem("global_quests_amount", 3, config, file);
            AddDefault.addDefaultConfigItem("easy_quests_amount", 1, config, file);
            AddDefault.addDefaultConfigItem("medium_quests_amount", 1, config, file);
            AddDefault.addDefaultConfigItem("hard_quests_amount", 1, config, file);

            AddDefault.addDefaultConfigItem("version", currentVersion, config, file);

            PluginLogger.fine("Done!");

            // --------------

            PluginLogger.fine("All files have been updated!");
        }
    }

    private void updateQuestsFile(FileConfiguration fileConfiguration, File file) {
        for (String fileQuest : fileConfiguration.getConfigurationSection("quests").getKeys(false)) {
            ConfigurationSection questSection = fileConfiguration.getConfigurationSection("quests." + fileQuest);
            if (questSection.contains(".entity_type")) {
                final String entityType = questSection.getString(".entity_type");

                final List<String> entityTypes = new ArrayList<>();
                entityTypes.add(entityType);

                questSection.set("required_entity", entityTypes);
            }
        }

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
