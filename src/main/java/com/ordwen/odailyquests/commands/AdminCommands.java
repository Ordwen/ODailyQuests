package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.commands.interfaces.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.logging.Logger;

public class AdminCommands implements CommandExecutor {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("questsadmin")) {
            if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
                if (args.length >= 2) {
                    switch (args[0]) {
                        case "reset":
                            if (Bukkit.getPlayer(args[1]) != null) {
                                QuestsManager.getActiveQuests().remove(args[1]);
                                HashMap<Quest, Progression> quests = new HashMap<>();
                                QuestsManager.selectRandomQuests(quests);
                                PlayerQuests playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
                                QuestsManager.getActiveQuests().put(args[1], playerQuests);

                                logger.info(ChatColor.GREEN + args[1] + ChatColor.YELLOW + " inserted into the array.");
                                Bukkit.getPlayer(args[1]).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());
                            } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                            break;
                        case "show":
                            if (sender instanceof Player) {
                                if (Bukkit.getPlayer(args[1]) != null) {
                                    ((Player) sender).openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(args[1]));
                                } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                            } else sender.sendMessage(QuestsMessages.PLAYER_ONLY.toString());
                            break;
                        case "complete":
                            if (Bukkit.getPlayer(args[1]) != null) {
                                if (args[2] != null) {
                                    HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(args[1]).getPlayerQuests();
                                    // quest id = numéro de quête de la HashMap du joueur concerné
                                    // donc récup la quête au numéro de la HashMap, set isAchieved sur true, et send reward
                                    int index = 0;
                                    for (Quest quest : playerQuests.keySet()) {
                                        if (index == Integer.parseInt(args[2])) {
                                            Progression progression = playerQuests.get(quest);
                                            progression.isAchieved = true;
                                            Bukkit.getPlayer(args[1]).sendMessage(QuestsMessages.QUEST_ACHIEVED.toString().replace("%questName%", quest.getQuestName()));
                                            RewardManager.sendQuestReward(args[1], quest.getReward());
                                            break;
                                        }
                                        index++;
                                        // TESTER
                                    }
                                } else sender.sendMessage();
                            } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                            break;
                        default:
                            sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
                            break;
                    }
                } else sender.sendMessage(QuestsMessages.INVALID_SYNTAX.toString());
            } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
        }
        return false;
    }
}
