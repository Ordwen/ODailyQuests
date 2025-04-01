package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.IAdminCommand;
import com.ordwen.odailyquests.commands.admin.AdminMessages;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand extends AdminMessages implements IAdminCommand {

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 4 && args[1] != null && args[2] != null && args[3] != null) {
            if (!args[1].equalsIgnoreCase("total")) {
                help(sender);
            }

            final Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                invalidAmount(sender);
                return;
            }

            addAmount(sender, target, amount);
        } else help(sender);
    }

    /**
     * Adds the amount of achieved quests to the player.
     *
     * @param sender the command sender.
     * @param target the player to add the achieved quests.
     * @param amount the amount of achieved quests to add.
     */
    private void addAmount(CommandSender sender, Player target, int amount) {
        final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(target.getName());
        playerQuests.addTotalAchievedQuests(amount);

        sendAdminMessage(sender, amount);
        sendTargetMessage(target, amount);
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param amount the amount of quests added.
     */
    private void sendAdminMessage(CommandSender sender, int amount) {
        final String msg = QuestsMessages.ADD_TOTAL_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg
                .replace("%target%", sender.getName())
                .replace("%amount%", String.valueOf(amount)));
    }

    /**
     * Sends the confirmation message to the target.
     *
     * @param target the target player.
     * @param amount the amount of quests added.
     */
    private void sendTargetMessage(Player target, int amount) {
        final String msg = QuestsMessages.ADD_TOTAL_TARGET.toString();
        if (msg != null) target.sendMessage(msg
                .replace("%amount%", String.valueOf(amount)));
    }
}
