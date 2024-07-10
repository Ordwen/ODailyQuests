package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCommand extends ACommandHandler {

    public AddCommand(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void handle() {
        if (args.length == 4 && args[1] != null && args[2] != null && args[3] != null) {
            if (!args[1].equalsIgnoreCase("total")) {
                help();
            }

            final Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                invalidPlayer();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                invalidAmount();
                return;
            }

            addAmount(target, amount);
        } else help();
    }

    /**
     * Adds the amount of achieved quests to the player.
     *
     * @param target the player to add the achieved quests.
     * @param amount the amount of achieved quests to add.
     */
    private void addAmount(Player target, int amount) {
        final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(target.getName());
        playerQuests.addTotalAchievedQuests(amount);

        sendAdminMessage(amount);
        sendTargetMessage(target, amount);
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param amount the amount of quests added.
     */
    private void sendAdminMessage(int amount) {
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
