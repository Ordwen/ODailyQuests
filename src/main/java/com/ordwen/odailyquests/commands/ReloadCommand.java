package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;
    private final QuestsFiles questsFiles;
    private PlayerQuestsInterface playerQuestsInterface;
    private GlobalQuestsInterface globalQuestsInterface;
    private CategorizedQuestsInterfaces categorizedQuestsInterfaces;

    /**
     * Class instance constructor.
     *
     * @param configurationFiles configuration files class.
     */
    public ReloadCommand(ConfigurationFiles configurationFiles, QuestsFiles questsFiles,
                         PlayerQuestsInterface playerQuestsInterface,
                         GlobalQuestsInterface globalQuestsInterface,
                         CategorizedQuestsInterfaces categorizedQuestsInterfaces) {
        this.configurationFiles = configurationFiles;
        this.questsFiles = questsFiles;
        this.playerQuestsInterface = playerQuestsInterface;
        this.globalQuestsInterface = globalQuestsInterface;
        this.categorizedQuestsInterfaces = categorizedQuestsInterfaces;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("config")) {
                    int currentMode = configurationFiles.getConfigFile().getInt("quests_mode");
                    configurationFiles.loadConfigurationFiles();
                    InterfacesManager.initInventoryNames();
                    playerQuestsInterface.loadPlayerQuestsInterface();
                    globalQuestsInterface.loadGlobalQuestsInterface();
                    categorizedQuestsInterfaces.loadCategorizedInterfaces();
                    if (currentMode != configurationFiles.getConfigFile().getInt("quests_mode")) {
                        questsFiles.loadQuestsFiles();
                    }
                } else if (args[0].equalsIgnoreCase("messages")) {
                    configurationFiles.loadMessagesFiles();
                } else if (args[0].equalsIgnoreCase("quests")) {
                    questsFiles.loadQuestsFiles();
                } else if (args[0].equalsIgnoreCase("all")) {
                    configurationFiles.loadConfigurationFiles();
                    InterfacesManager.initInventoryNames();
                    playerQuestsInterface.loadPlayerQuestsInterface();
                    globalQuestsInterface.loadGlobalQuestsInterface();
                    categorizedQuestsInterfaces.loadCategorizedInterfaces();
                    configurationFiles.loadMessagesFiles();
                    questsFiles.loadQuestsFiles();
                } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
            } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
        } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
        return false;
    }
}
