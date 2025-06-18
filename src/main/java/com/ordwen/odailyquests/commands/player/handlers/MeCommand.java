package com.ordwen.odailyquests.commands.player.handlers;

import com.ordwen.odailyquests.api.commands.player.PlayerCommandBase;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MeCommand extends PlayerCommandBase {

    private final PlayerQuestsInterface playerQuestsInterface;

    public MeCommand(PlayerQuestsInterface playerQuestsInterface) {
        this.playerQuestsInterface = playerQuestsInterface;
    }

    @Override
    public String getName() {
        return "me";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_PLAYER_USE.get();
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length > 1) {
            help(player);
            return;
        }

        openInventory(player);
    }

    /**
     * Opens the quests interface for the player.
     *
     * @param player the player.
     */
    private void openInventory(Player player) {
        final Inventory inventory = playerQuestsInterface.getPlayerQuestsInterface(player);
        if (inventory == null) {
            player.sendMessage(QuestsMessages.IMPOSSIBLE_TO_OPEN_INVENTORY.toString());
            player.sendMessage(QuestsMessages.CONTACT_ADMIN.toString());
            return;
        }

        player.openInventory(inventory);
    }
}