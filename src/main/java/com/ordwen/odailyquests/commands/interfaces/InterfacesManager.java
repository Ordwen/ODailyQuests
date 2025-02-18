package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.files.ConfigurationFile;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class InterfacesManager implements Listener {

    private final ConfigurationFile configurationFile;

    private final PlayerQuestsInterface playerQuestsInterface;
    private final QuestsInterfaces questsInterfaces;
    private final Buttons buttons;

    public InterfacesManager(ODailyQuests oDailyQuests) {
        this.configurationFile = oDailyQuests.getFilesManager().getConfigurationFile();
        this.playerQuestsInterface = new PlayerQuestsInterface(oDailyQuests.getFilesManager().getPlayerInterfaceFile());

        this.buttons = new Buttons(configurationFile);
        this.questsInterfaces = new QuestsInterfaces(configurationFile, buttons);
    }

    /**
     * Load all interfaces objects.
     */
    public void initAllObjects() {
        buttons.initItems();
        loadInterfaces();
    }

    /**
     * Load all interfaces.
     */
    public void loadInterfaces() {
        loadPlayerQuestsInterface();
        loadQuestsInterfaces();
    }

    /**
     * Load player quests interface.
     */
    public void loadPlayerQuestsInterface() {
        playerQuestsInterface.load();
    }

    /**
     * Load categorized quests interface.
     */
    public void loadQuestsInterfaces() {
        questsInterfaces.loadAll();
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public QuestsInterfaces getQuestsInterfaces() {
        return questsInterfaces;
    }

    public PlayerQuestsInterface getPlayerQuestsInterface() {
        return playerQuestsInterface;
    }
}


