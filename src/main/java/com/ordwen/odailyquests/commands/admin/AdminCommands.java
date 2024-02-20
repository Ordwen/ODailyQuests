package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.externs.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.externs.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.commands.admin.convert.ConverterManager;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminCommands implements CommandExecutor {

    private final ReloadService reloadService;

    public AdminCommands(ODailyQuests oDailyQuests) {
        this.reloadService = oDailyQuests.getReloadService();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender.hasPermission(QuestsPermissions.QUESTS_ADMIN.getPermission())) {
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    reloadService.reload();
                    sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded!");
                } else {
                    final String msg = QuestsMessages.ADMIN_HELP.toString();
                    if (msg != null) sender.sendMessage(msg);
                }
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "convert" -> new ConverterManager(sender, args).handle();
                    case "reset" -> new ResetCommand(sender, args).handle();
                    case "show" -> new ShowCommand(sender, args).handle();
                    case "open" -> new OpenCommand(sender, args).handle();
                    case "complete" -> {
                        final Player target = Bukkit.getPlayerExact(args[1]);
                        if (target == null) {
                            final String msg = QuestsMessages.INVALID_PLAYER.toString();
                            if (msg != null) sender.sendMessage(msg);
                            return true;
                        }

                        if (args.length < 3) {
                            final String msg = QuestsMessages.ADMIN_HELP.toString();
                            if (msg != null) sender.sendMessage(msg);
                            return true;
                        }

                        int questIndex;
                        try {
                            questIndex = Integer.parseInt(args[2]);
                        } catch (NumberFormatException exception) {
                            final String msg = QuestsMessages.ADMIN_HELP.toString();
                            if (msg != null) sender.sendMessage(msg);
                            return true;
                        }

                        if (questIndex >= 1 && questIndex <= QuestsAmount.getQuestsAmount()) {
                            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(args[1]).getPlayerQuests();

                            int index = 0;
                            for (AbstractQuest quest : playerQuests.keySet()) {
                                Progression progression = playerQuests.get(quest);
                                if (index == questIndex - 1) {
                                    if (!playerQuests.get(quest).isAchieved()) {
                                        final QuestCompletedEvent event = new QuestCompletedEvent(target, progression, quest);
                                        ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
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
                    }

                    case "holo" -> {
                        if (!(sender instanceof Player)) {
                            final String msg = QuestsMessages.PLAYER_ONLY.toString();
                            if (msg != null) sender.sendMessage(msg);
                            return true;
                        }

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
                                        case "global" -> {
                                            if (!CategoriesLoader.getGlobalQuests().isEmpty()) {
                                                HolographicDisplaysHook.createHologram(index,
                                                        CategoriesLoader.getGlobalQuests(),
                                                        ((Player) sender).getPlayer());
                                            } else {
                                                final String msg = QuestsMessages.HOLO_CATEGORIZED_ENABLED.toString();
                                                if (msg != null) sender.sendMessage(msg);
                                            }
                                        }
                                        case "easy" -> {
                                            if (!CategoriesLoader.getEasyQuests().isEmpty()) {
                                                HolographicDisplaysHook.createHologram(index,
                                                        CategoriesLoader.getEasyQuests(),
                                                        ((Player) sender).getPlayer());
                                            } else {
                                                final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                if (msg != null) sender.sendMessage(msg);
                                            }
                                        }
                                        case "medium" -> {
                                            if (!CategoriesLoader.getMediumQuests().isEmpty()) {
                                                HolographicDisplaysHook.createHologram(index,
                                                        CategoriesLoader.getMediumQuests(),
                                                        ((Player) sender).getPlayer());
                                            } else {
                                                final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                if (msg != null) sender.sendMessage(msg);
                                            }
                                        }
                                        case "hard" -> {
                                            if (!CategoriesLoader.getHardQuests().isEmpty()) {
                                                HolographicDisplaysHook.createHologram(index,
                                                        CategoriesLoader.getHardQuests(),
                                                        ((Player) sender).getPlayer());
                                            } else {
                                                final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
                                                if (msg != null) sender.sendMessage(msg);
                                            }
                                        }
                                        default -> {
                                            final String msg = QuestsMessages.INVALID_CATEGORY.toString();
                                            if (msg != null) sender.sendMessage(msg);
                                        }
                                    }
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
                    }
                    default -> {
                        final String msg = QuestsMessages.ADMIN_HELP.toString();
                        if (msg != null) sender.sendMessage(msg);
                    }
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
