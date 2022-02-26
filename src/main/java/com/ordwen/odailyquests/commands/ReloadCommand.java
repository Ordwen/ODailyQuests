package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.commands.interfaces.CategorizedQuestsInterfaces;
import com.ordwen.odailyquests.commands.interfaces.GlobalQuestsInterface;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.quests.LoadQuests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;
    private final QuestsFiles questsFiles;
    private final LoadQuests loadQuests;
    private final InterfacesManager interfacesManager;
    private final PlayerQuestsInterface playerQuestsInterface;
    private final GlobalQuestsInterface globalQuestsInterface;
    private final CategorizedQuestsInterfaces categorizedQuestsInterfaces;

    /**
     * Class instance constructor.
     * @param configurationFiles
     * @param questsFiles
     * @param loadQuests
     * @param interfacesManager
     * @param playerQuestsInterface
     * @param globalQuestsInterface
     * @param categorizedQuestsInterfaces
     */
    public ReloadCommand(ConfigurationFiles configurationFiles, QuestsFiles questsFiles,
                         LoadQuests loadQuests, InterfacesManager interfacesManager, PlayerQuestsInterface playerQuestsInterface,
                         GlobalQuestsInterface globalQuestsInterface,
                         CategorizedQuestsInterfaces categorizedQuestsInterfaces) {
        this.configurationFiles = configurationFiles;
        this.questsFiles = questsFiles;
        this.loadQuests = loadQuests;
        this.interfacesManager = interfacesManager;
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
                    interfacesManager.initInventoryNames();
                    if (currentMode != configurationFiles.getConfigFile().getInt("quests_mode")) {
                        questsFiles.loadQuestsFiles();
                        loadQuests.clearQuestsLists();
                        loadQuests.loadQuests();
                    }
                    playerQuestsInterface.loadPlayerQuestsInterface();
                    globalQuestsInterface.loadGlobalQuestsInterface();
                    categorizedQuestsInterfaces.loadCategorizedInterfaces();
                } else if (args[0].equalsIgnoreCase("messages")) {
                    configurationFiles.loadMessagesFiles();
                } else if (args[0].equalsIgnoreCase("quests")) {
                    questsFiles.loadQuestsFiles();
                    loadQuests.clearQuestsLists();
                    loadQuests.loadQuests();
                    playerQuestsInterface.loadPlayerQuestsInterface();
                    globalQuestsInterface.loadGlobalQuestsInterface();
                    categorizedQuestsInterfaces.loadCategorizedInterfaces();
                } else if (args[0].equalsIgnoreCase("all")) {
                    configurationFiles.loadConfigurationFiles();
                    interfacesManager.initInventoryNames();
                    questsFiles.loadQuestsFiles();
                    loadQuests.clearQuestsLists();
                    loadQuests.loadQuests();
                    playerQuestsInterface.loadPlayerQuestsInterface();
                    globalQuestsInterface.loadGlobalQuestsInterface();
                    categorizedQuestsInterfaces.loadCategorizedInterfaces();
                    configurationFiles.loadMessagesFiles();
                } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
            } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
        } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
        return false;
    }
}
