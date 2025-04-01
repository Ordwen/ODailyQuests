package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.IAdminCommand;
import com.ordwen.odailyquests.commands.admin.AdminMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomCompleteCommand extends AdminMessages implements IAdminCommand {

    private final PlayerProgressor playerProgressor;

    public CustomCompleteCommand() {
        this.playerProgressor = new PlayerProgressor();
    }

    @Override
    public String getName() {
        return "customcomplete";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Bukkit.getPlayerExact(args[1]) != null) {
            if (args.length < 4) {
                help(sender);
                return;
            }

            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            String questType = args[2];

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException exception) {
                help(sender);
                return;
            }

            playerProgressor.setPlayerQuestProgression(null, target, amount, questType);
        } else help(sender);
    }
}
