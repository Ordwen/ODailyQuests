package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.interfaces.InterfaceInventory;
import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.player.PCommandHandler;
import com.ordwen.odailyquests.commands.player.PlayerMessages;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ShowCommand extends PCommandHandler {

    private static final String GLOBAL = "global";
    private static final String EASY = "easy";
    private static final String MEDIUM = "medium";
    private static final String HARD = "hard";

    public ShowCommand(Player player, String[] args, QuestSystem questSystem, PlayerMessages playerMessages) {
        super(player, args, questSystem, playerMessages);
    }

    @Override
    public void handle() {
        if (!player.hasPermission(QuestsPermissions.QUEST_SHOW.getPermission())) {
            playerMessages.noPermission(player);
            return;
        }

        if (args.length != 2) {
            playerMessages.help(player);
            return;
        }

        if (args[1].equalsIgnoreCase(GLOBAL)) {
            openGlobal(questSystem);
        } else openCategory(questSystem, args[1]);
    }

    /**
     * Opens the category interface.
     * @param category the category.
     */
    private void openCategory(QuestSystem questSystem, String category) {
        if (questSystem.getQuestsMode() == 1) {
            playerMessages.categorizedDisabled(player);
            return;
        }

        switch (category) {
            case GLOBAL -> openGlobal(questSystem);
            case EASY -> openEasy(questSystem);
            case MEDIUM -> openMedium(questSystem);
            case HARD -> openHard(questSystem);
            default -> playerMessages.invalidCategory(player);
        }
    }

    /**
     * Opens the global interface.
     */
    private void openGlobal(QuestSystem questSystem) {
        if (questSystem.getQuestsMode() == 2) {
            playerMessages.categorizedEnabled(player);
            return;
        }

        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_GLOBAL.getPermission())) {
            playerMessages.noPermissionCategory(player);
            return;
        }

        final InterfaceInventory inventory = InterfacesManager.getInterfaceFirstPage(questSystem, GLOBAL, player);
        player.openInventory(inventory.getInventory());
    }

    /**
     * Opens the easy interface.
     */
    private void openEasy(QuestSystem questSystem) {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_EASY.getPermission())) {
            playerMessages.noPermissionCategory(player);
            return;
        }
        final InterfaceInventory inventory = InterfacesManager.getInterfaceFirstPage(questSystem, EASY, player);
        player.openInventory(inventory.getInventory());
    }

    /**
     * Opens the medium interface.
     */
    private void openMedium(QuestSystem questSystem) {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_MEDIUM.getPermission())) {
            playerMessages.noPermissionCategory(player);
            return;
        }
        final InterfaceInventory inventory = InterfacesManager.getInterfaceFirstPage(questSystem, MEDIUM, player);
        player.openInventory(inventory.getInventory());
    }

    /**
     * Opens the hard interface.
     */
    private void openHard(QuestSystem questSystem) {
        if (!player.hasPermission(QuestsPermissions.QUESTS_SHOW_HARD.getPermission())) {
            playerMessages.noPermissionCategory(player);
            return;
        }
        final InterfaceInventory inventory = InterfacesManager.getInterfaceFirstPage(questSystem, HARD, player);
        player.openInventory(inventory.getInventory());
    }
}
