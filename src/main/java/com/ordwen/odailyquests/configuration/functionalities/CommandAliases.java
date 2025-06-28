package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.util.List;

public class CommandAliases implements IConfigurable {

    private final ConfigurationFile configurationFile;

    private boolean isKeepingOnlyAliases = false;

    public CommandAliases(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final List<String> aliases = configurationFile.getConfig().getStringList("command_aliases");
        for (String alias : aliases) {
            registerAlias(alias);
        }

        isKeepingOnlyAliases = configurationFile.getConfig().getBoolean("keep_only_aliases_in_completion", false);
    }

    private void registerAlias(String alias) {
        final ODailyQuests plugin = ODailyQuests.INSTANCE;
        final PluginCommand command = plugin.getCommand("dquests");

        try {
            final Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            final CommandMap commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());
            commandMap.register(alias, "odailyquests", command);

        } catch (Exception e) {
            PluginLogger.error("Failed to register command alias '" + alias + "' for command '" + "dquests" + "' : " + e.getMessage());
        }
    }

    private List<String> getSubcommandAliasesInternal(String subcommandName) {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("subcommand_aliases");
        if (section == null) return List.of();

        return section.getStringList(subcommandName.toLowerCase());
    }

    private boolean isKeepingOnlyAliasesInternal() {
        return isKeepingOnlyAliases;
    }

    private static CommandAliases getInstance() {
        return ConfigFactory.getConfig(CommandAliases.class);
    }

    public static List<String> getSubcommandAliases(String subcommandName) {
        return getInstance().getSubcommandAliasesInternal(subcommandName);
    }

    public static boolean isKeepingOnlyAliases() {
        return getInstance().isKeepingOnlyAliasesInternal();
    }
}
