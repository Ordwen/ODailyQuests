package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.apis.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.apis.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.commands.convert.ConverterManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.LoadQuests;
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
    private final ReloadService reloadService;

    public AdminCommands(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
        this.reloadService = oDailyQuests.getReloadService();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length == 1) {
                if ("reload".equals(args[0])) {
                    reloadService.reload();
                    sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
                } else {
                    final String msg = QuestsMessages.ADMIN_HELP.toString();
                    if (msg != null) sender.sendMessage(msg);
                }
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "convert":
                        if (args.length == 3) {
                            if (!new ConverterManager(oDailyQuests).convert(args[1], args[2])) {
                                sender.sendMessage(ChatColor.RED + "Conversion failed! Please check the console for more information.");
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "Conversion successful!");
                                sender.sendMessage(ChatColor.GREEN + "Please select the new storage mode in config file and restart the server to apply changes.");
                            }
                        } else sender.sendMessage(ChatColor.RED + "Usage: /quests convert <old format> <new format>");
                        break;
                    case "reset":
                        if (args.length >= 3 && args[1] != null && args[2] != null) {
                            final Player target = Bukkit.getPlayerExact(args[2]);
                            switch (args[1]) {
                                case "quests":
                                    if (target != null) {
                                        int totalAchievedQuests = QuestsManager.getActiveQuests().get(args[2]).getTotalAchievedQuests();

                                        QuestsManager.getActiveQuests().remove(args[2]);
                                        LinkedHashMap<AbstractQuest, Progression> quests = QuestsManager.selectRandomQuests();

                                        PlayerQuests playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
                                        playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                                        QuestsManager.getActiveQuests().put(args[2], playerQuests);
                                        QuestsManager.getActiveQuests().get(args[2]).setAchievedQuests(0);

                                        PluginLogger.info(ChatColor.GREEN + args[2] + ChatColor.YELLOW + " inserted into the array.");

                                        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
                                        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

                                        msg = QuestsMessages.QUESTS_RENEWED.toString();
                                        if (msg != null) target.sendMessage(msg);
                                    } else {
                                        final String msg = QuestsMessages.INVALID_PLAYER.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                    }
                                    break;
                                case "total":
                                    if (target != null) {
                                        QuestsManager.getActiveQuests().get(args[2]).setTotalAchievedQuests(0);

                                        String msg = QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString();
                                        if (msg != null) sender.sendMessage(msg.replace("%target%", target.getName()));

                                        msg = QuestsMessages.TOTAL_AMOUNT_RESET.toString();
                                        if (msg != null) target.sendMessage(msg);
                                    } else {
                                        final String msg = QuestsMessages.INVALID_PLAYER.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                    }
                                    break;
                                default:
                                    final String msg = QuestsMessages.ADMIN_HELP.toString();
                                    if (msg != null) sender.sendMessage(msg);
                                    break;
                            }
                        } else {
                            final String msg = QuestsMessages.ADMIN_HELP.toString();
                            if (msg != null) sender.sendMessage(msg);
                        }
                        break;
                    case "show":
                        if (sender instanceof Player) {
                            if (Bukkit.getPlayerExact(args[1]) != null) {
                                ((Player) sender).openInventory(PlayerQuestsInterface.getPlayerQuestsInterface(args[1]));
                            } else {
                                final String msg = QuestsMessages.INVALID_PLAYER.toString();
                                if (msg != null) sender.sendMessage(msg);
                            }
                        } else {
                            final String msg = QuestsMessages.PLAYER_ONLY.toString();
                            if (msg != null) sender.sendMessage(msg);
                        }
                        break;
                    case "complete":
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        if (target != null) {
                            if (args[2] != null && Integer.parseInt(args[2]) >= 1 && Integer.parseInt(args[2]) <= QuestsAmount.getQuestsAmount()) {
                                HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(args[1]).getPlayerQuests();
                                int index = 0;
                                for (AbstractQuest quest : playerQuests.keySet()) {
                                    Progression progression = playerQuests.get(quest);
                                    if (index == Integer.parseInt(args[2]) - 1) {
                                        if (!playerQuests.get(quest).isAchieved()) {
                                            progression.setAchieved();
                                            RewardManager.sendAllRewardItems(quest.getQuestName(), target, quest.getReward());
                                            playerQuests.replace(quest, progression);
                                            QuestsManager.getActiveQuests().get(args[1]).increaseAchievedQuests(args[1]);
                                            break;
                                        } else {
                                            final String msg = QuestsMessages.QUEST_ALREADY_ACHIEVED.toString();
                                            if (msg != null) sender.sendMessage(msg);
                                        }
                                    }
                                    index++;
                                }
                            } else {
                                final String msg = QuestsMessages.INVALID_QUEST_ID.toString();
                                if (msg != null) sender.sendMessage(msg);
                            }
                        } else {
                            final String msg = QuestsMessages.INVALID_PLAYER.toString();
                            if (msg != null) sender.sendMessage(msg);
                        }
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
                                            final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
                                            if (msg != null) sender.sendMessage(msg);
                                            return true;
                                        }
                                        switch (args[2]) {
                                            case "global":
                                                if (LoadQuests.getGlobalQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getGlobalQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else {
                                                    final String msg = QuestsMessages.HOLO_CATEGORIZED_ENABLED.toString();
                                                    if (msg != null) sender.sendMessage(msg);
                                                }
                                                break;
                                            case "easy":
                                                if (LoadQuests.getEasyQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getEasyQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else {
                                                    final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                    if (msg != null) sender.sendMessage(msg);
                                                }
                                                break;
                                            case "medium":
                                                if (LoadQuests.getMediumQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getMediumQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else {
                                                    final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                    if (msg != null) sender.sendMessage(msg);
                                                }
                                                break;
                                            case "hard":
                                                if (LoadQuests.getHardQuests().size() != 0) {
                                                    HolographicDisplaysHook.createHologram(index,
                                                            LoadQuests.getHardQuests(),
                                                            ((Player) sender).getPlayer());
                                                } else {
                                                    final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                    if (msg != null) sender.sendMessage(msg);
                                                }
                                                break;
                                            default:
                                                final String msg = QuestsMessages.INVALID_CATEGORY.toString();
                                                if (msg != null) sender.sendMessage(msg);
                                                break;
                                        }
                                        break;
                                    } else {
                                        final String msg = QuestsMessages.ADMIN_HELP.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                    }
                                } else if (args.length == 3 && args[1].equalsIgnoreCase("delete")) {
                                    int index;
                                    try {
                                        index = Integer.parseInt(args[2]);
                                    } catch (Exception e) {
                                        final String msg = QuestsMessages.INVALID_QUEST_INDEX.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                        return true;
                                    }
                                    if (HologramsManager.deleteHologram(index)) {
                                        final String msg = QuestsMessages.HOLO_DELETED.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                    } else {
                                        final String msg = QuestsMessages.HOLO_INVALID_INDEX.toString();
                                        if (msg != null) sender.sendMessage(msg);
                                    }
                                } else {
                                    final String msg = QuestsMessages.ADMIN_HELP.toString();
                                    if (msg != null) sender.sendMessage(msg);
                                }
                            } else {
                                final String msg = QuestsMessages.ADMIN_HELP.toString();
                                if (msg != null) sender.sendMessage(msg);
                            }
                        } else {
                            final String msg = QuestsMessages.PLAYER_ONLY.toString();
                            if (msg != null) sender.sendMessage(msg);
                        }
                        break;
                    default:
                        final String msg = QuestsMessages.ADMIN_HELP.toString();
                        if (msg != null) sender.sendMessage(msg);
                        break;
                }
            } else {
                final String msg = QuestsMessages.ADMIN_HELP.toString();
                if (msg != null) sender.sendMessage(msg);
            }
        } else {
            final String msg = QuestsMessages.NO_PERMISSION.toString();
            if (msg != null) sender.sendMessage(msg);
        }
        return false;
    }
}
