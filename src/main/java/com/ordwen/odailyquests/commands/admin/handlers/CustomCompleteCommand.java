package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.configuration.essentials.CustomTypes;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomCompleteCommand extends AdminCommandBase {

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
        return QuestsPermissions.QUESTS_ADMIN.get();
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

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return new ArrayList<>(CustomTypes.getCustomTypes());
        }

        if (args.length >= 4) {
            return Collections.emptyList();
        }

        return null;
    }
}
