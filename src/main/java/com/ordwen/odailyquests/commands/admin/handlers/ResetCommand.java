package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ResetCommand extends AdminCommandBase {

    private static final String TOTAL = "total";
    private static final String QUESTS = "quests";
    private static final String TARGET = "%target%";

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            help(sender);
            return;
        }

        final String action = args[1].toLowerCase();
        if (action.equalsIgnoreCase(TOTAL)) {
            handleResetTotal(sender, args);
        } else if (action.equalsIgnoreCase(QUESTS)) {
            handleResetQuests(sender, args);
        } else {
            help(sender);
        }
    }

    private void handleResetTotal(CommandSender sender, String[] args) {
        if (args.length == 3) {
            final Player target = getTargetPlayer(sender, args[2]);
            if (target != null) {
                resetTotal(sender, target);
            }
        } else if (args.length == 4) {
            final String category = args[2];
            if (!CategoriesLoader.getAllCategories().containsKey(category)) {
                invalidCategory(sender);
                return;
            }
            final Player target = getTargetPlayer(sender, args[3]);
            if (target != null) {
                resetCategory(sender, target, category);
            }
        } else {
            help(sender);
        }
    }

    private void handleResetQuests(CommandSender sender, String[] args) {
        final Player target = getTargetPlayer(sender, args[2]);
        if (target != null) {
            quests(sender, target);
        }
    }


    /**
     * Resets the current active quests of the player.
     *
     * @param sender the command sender
     * @param target the player to reset
     */
    public void quests(CommandSender sender, Player target) {
        final String playerName = target.getName();
        final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(playerName);
        final Map<String, Integer> totalAchievedQuestsByCategory = playerQuests.getTotalAchievedQuestsByCategory();
        final int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        QuestLoaderUtils.loadNewPlayerQuests(playerName, QuestsManager.getActiveQuests(), totalAchievedQuestsByCategory, totalAchievedQuests);

        String msg = QuestsMessages.QUESTS_RENEWED_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace(TARGET, target.getName()));
    }

    private void resetTotal(CommandSender sender, Player target) {
        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(target.getName());
        playerQuests.setTotalAchievedQuests(0);

        String msg = QuestsMessages.TOTAL_AMOUNT_RESET_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg.replace(TARGET, target.getName()));

        msg = QuestsMessages.TOTAL_AMOUNT_RESET.getMessage(target);
        if (msg != null) target.sendMessage(msg);
    }

    private void resetCategory(CommandSender sender, Player target, String category) {
        System.out.println("1");
        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(target.getName());
        playerQuests.setTotalCategoryAchievedQuests(category, 0);

        String msg = QuestsMessages.TOTAL_CATEGORY_RESET_ADMIN.toString();
        if (msg != null) sender.sendMessage(
                msg.replace(TARGET, target.getName())
                        .replace("%category%", category)
        );

        msg = QuestsMessages.TOTAL_CATEGORY_RESET_TARGET.getMessage(target);
        if (msg != null) target.sendMessage(msg.replace("%category%", category));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            return List.of(QUESTS, TOTAL);
        }

        if (args.length == 3 && args[1].equalsIgnoreCase(TOTAL)) {
            final Set<String> categories = CategoriesLoader.getAllCategories().keySet();
            final List<String> suggestions = new ArrayList<>(categories);

            Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            return suggestions;
        }

        if (args.length == 4 && args[1].equalsIgnoreCase(TOTAL) && CategoriesLoader.getAllCategories().containsKey(args[2])) {
            return null;
        }

        if (args.length >= 4) {
            return Collections.emptyList();
        }

        if (args.length == 3 && args[1].equalsIgnoreCase(QUESTS)) {
            return null;
        }

        return Collections.emptyList();
    }
}
