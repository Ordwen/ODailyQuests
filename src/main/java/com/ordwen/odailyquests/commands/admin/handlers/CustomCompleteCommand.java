package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomCompleteCommand extends ACommandHandler {

    private final PlayerProgressor playerProgressor;

    public CustomCompleteCommand(CommandSender sender, String[] args) {
        super(sender, args);
        this.playerProgressor = new PlayerProgressor();
    }

    @Override
    public void handle() {
        if (Bukkit.getPlayerExact(args[1]) != null) {

            if (args.length < 4) {
                help();
                return;
            }

            final Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                invalidPlayer();
                return;
            }

            String questType = args[2];

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException exception) {
                help();
                return;
            }

            playerProgressor.setPlayerQuestProgression(null, target, amount, questType);
        } else help();
    }
}
