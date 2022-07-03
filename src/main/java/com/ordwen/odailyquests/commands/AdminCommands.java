package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.apis.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.apis.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AdminCommands implements CommandExecutor {

    private final ODailyQuests oDailyQuests;

    public AdminCommands(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length == 1) {
                if ("reload".equals(args[0])) {
                    oDailyQuests.filesManager.loadAllFiles();
                    oDailyQuests.configurationManager.loadConfiguration();

                    oDailyQuests.interfacesManager.initAllObjects();
                    LoadQuests.loadCategories();

                    sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
                } else {
                    sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                }
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "reset":
                        if (args.length >= 3 && args[1] != null && args[2] != null) {
                            switch (args[1]) {
                                case "quests":
                                    if (Bukkit.getPlayerExact(args[2]) != null) {
                                        int totalAchievedQuests = QuestsManager.getActiveQuests().get(args[2]).getTotalAchievedQuests();

                                        QuestsManager.getActiveQuests().remove(args[2]);
                                        LinkedHashMap<Quest, Progression> quests = new LinkedHashMap<>();
                                        QuestsManager.selectRandomQuests(quests);

                                        PlayerQuests playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
                                        playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                                        QuestsManager.getActiveQuests().put(args[2], playerQuests);
                                        QuestsManager.getActiveQuests().get(args[2]).setAchievedQuests(0);

                                        PluginLogger.info(ChatColor.GREEN + args[2] + ChatColor.YELLOW + " inserted into the array.");

                                        sender.sendMessage(QuestsMessages.QUESTS_RENEWED_ADMIN.toString().replace("%target%", Bukkit.getPlayerExact(args[2]).getName()));
                                        Bukkit.getPlayerExact(args[2]).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());
                                    } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                                    break;
                                case "total":
                                    if (Bukkit.getPlayerExact(args[2]) != null) {
                                        QuestsManager.getActiveQuests().get(args[2]).setTotalAchievedQuests(0);

                                        sender.sendMessage(QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString().replace("%target%", Bukkit.getPlayerExact(args[2]).getName()));
                                        Bukkit.getPlayerExact(args[2]).sendMessage(QuestsMessages.TOTAL_AMOUNT_RESET.toString());
                                    } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                                    break;
                                default:
                                    sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                                    break;
                            }
                        } else sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                        break;
                    case "show":
                        if (sender instanceof Player) {
                            if (Bukkit.getPlayerExact(args[1]) != null) {
                                ((Player) sender).openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(args[1]));
                            } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                        } else sender.sendMessage(QuestsMessages.PLAYER_ONLY.toString());
                        break;
                    case "complete":
                        if (Bukkit.getPlayerExact(args[1]) != null) {
                            if (args[2] != null && Integer.parseInt(args[2]) >= 1 && Integer.parseInt(args[2]) <= 3) {
                                HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(args[1]).getPlayerQuests();
                                int index = 0;
                                for (Quest quest : playerQuests.keySet()) {
                                    Progression progression = playerQuests.get(quest);
                                    if (index == Integer.parseInt(args[2]) - 1) {
                                        if (!playerQuests.get(quest).isAchieved()) {
                                            progression.isAchieved = true;
                                            RewardManager.sendAllRewardItems(quest.getQuestName(), args[1], quest.getReward());
                                            playerQuests.replace(quest, progression);
                                            QuestsManager.getActiveQuests().get(args[1]).increaseAchievedQuests(args[1]);
                                            break;
                                        } else sender.sendMessage(QuestsMessages.QUEST_ALREADY_ACHIEVED.toString());
                                    }
                                    index++;
                                }
                            } else sender.sendMessage(QuestsMessages.INVALID_QUEST_ID.toString());
                        } else sender.sendMessage(QuestsMessages.INVALID_PLAYER.toString());
                        break;
                    case "holo":
                        if (sender instanceof Player) {
                            if (args.length >= 3 && args[1] != null && args[2] != null) {
                                if (args[1].equalsIgnoreCase("create")) {
                                    if (args.length == 4 && args[3] != null) {
                                        int index;
                                        try {
                                            index = Integer.parseInt(args[3]) - 1;
                                        } catch (Exception e) {
                                            sender.sendMessage(QuestsMessages.INVALID_QUEST_INDEX.toString());
                                            return true;
                                        }
                                        switch (args[2]) {
                                            case "global":
                                                if (LoadQuests.getGlobalQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getGlobalQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else
                                                    sender.sendMessage(QuestsMessages.HOLO_CATEGORIZED_ENABLED.toString());
                                                break;
                                            case "easy":
                                                if (LoadQuests.getEasyQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getEasyQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else
                                                    sender.sendMessage(QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString());
                                                break;
                                            case "medium":
                                                if (LoadQuests.getMediumQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getMediumQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else
                                                    sender.sendMessage(QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString());
                                                break;
                                            case "hard":
                                                if (LoadQuests.getHardQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getHardQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else
                                                    sender.sendMessage(QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString());
                                                break;
                                            default:
                                                sender.sendMessage(QuestsMessages.INVALID_CATEGORY.toString());
                                                break;
                                        }
                                        break;
                                    } else sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                                } else if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
                                    int index;
                                    try {
                                        index = Integer.parseInt(args[2]);
                                    } catch (Exception e) {
                                        sender.sendMessage(QuestsMessages.INVALID_QUEST_INDEX.toString());
                                        return true;
                                    }
                                    if (HologramsManager.deleteHologram(index)) {
                                        sender.sendMessage(QuestsMessages.HOLO_DELETED.toString());
                                    } else sender.sendMessage(QuestsMessages.HOLO_INVALID_INDEX.toString());
                                } else sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                            } else sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                        } else sender.sendMessage(QuestsMessages.PLAYER_ONLY.toString());
                        break;
                    default:
                        sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
                        break;
                }
            } else sender.sendMessage(QuestsMessages.ADMIN_HELP.toString());
        } else sender.sendMessage(QuestsMessages.NO_PERMISSION.toString());
        return false;
    }
}
