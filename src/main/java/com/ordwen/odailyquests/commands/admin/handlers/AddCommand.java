package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.tools.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AddCommand extends AdminCommandBase {

    private static final String ADD = "add";

    @Override
    public String getName() {
        return ADD;
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.get();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 4 && args[1].equalsIgnoreCase(TOTAL)) {
            // Command: /dqa add total <player> <amount>
            final String playerName = args[2];
            final String amountStr = args[3];

            final Pair<Player, Integer> playerAmount = getPlayerAndAmount(sender, playerName, amountStr);
            if (playerAmount == null) return;

            final Player target = playerAmount.first();
            final int amount = playerAmount.second();

            addTotalAmount(sender, target, amount);
        } else if (args.length == 5  && args[1].equalsIgnoreCase(TOTAL)) {
            // Command: /dqa add total <category> <player> <amount>
            final String category = args[2];
            final String playerName = args[3];
            final String amountStr = args[4];

            if (CategoriesLoader.getAllCategories().containsKey(category)) {
                final Pair<Player, Integer> playerAmount = getPlayerAndAmount(sender, playerName, amountStr);
                if (playerAmount == null) return;

                final Player target = playerAmount.first();
                final int amount = playerAmount.second();

                addCategoryAmount(sender, target, category, amount);
            } else {
                invalidCategory(sender);
            }
        } else {
            help(sender);
        }
    }

    private Pair<Player, Integer> getPlayerAndAmount(CommandSender sender, String playerName, String amountStr) {
        final Player target = getTargetPlayer(sender, playerName);
        if (target == null) return null;

        final int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            invalidAmount(sender);
            return null;
        }

        return new Pair<>(target, amount);
    }

    /**
     * Adds the amount of achieved quests to the player.
     *
     * @param sender the command sender.
     * @param target the player to add the achieved quests.
     * @param amount the amount of achieved quests to add.
     */
    private void addTotalAmount(CommandSender sender, Player target, int amount) {
        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(target.getName());
        playerQuests.addTotalAchievedQuests(amount);

        sendAdminTotalMessage(sender, target.getName(), amount);
        sendTargetTotalMessage(target, amount);
    }

    /**
     * Adds the amount of achieved quests to the player in a specific category.
     *
     * @param sender   the command sender.
     * @param target   the player to add the achieved quests.
     * @param category the category to add the achieved quests.
     * @param amount   the amount of achieved quests to add.
     */
    private void addCategoryAmount(CommandSender sender, Player target, String category, int amount) {
        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(target.getName());
        playerQuests.addTotalCategoryAchievedQuests(category, amount);

        sendAdminTotalCategoryMessage(sender, target.getName(), amount, category);
        sendTargetTotalCategoryMessage(target, amount, category);
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param amount the amount of quests added.
     */
    private void sendAdminTotalMessage(CommandSender sender, String targetName, int amount) {
        final String msg = QuestsMessages.ADD_TOTAL_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg
                .replace("%target%", targetName)
                .replace(AMOUNT, String.valueOf(amount)));
    }

    /**
     * Sends the confirmation message to the target.
     *
     * @param target the target player.
     * @param amount the amount of quests added.
     */
    private void sendTargetTotalMessage(Player target, int amount) {
        final String msg = QuestsMessages.ADD_TOTAL_TARGET.toString();
        if (msg != null) target.sendMessage(msg
                .replace(AMOUNT, String.valueOf(amount)));
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param amount the amount of quests added.
     */
    private void sendAdminTotalCategoryMessage(CommandSender sender, String targetName, int amount, String context) {
        final String msg = QuestsMessages.ADD_TOTAL_CATEGORY_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg
                .replace(TARGET, targetName)
                .replace(AMOUNT, String.valueOf(amount))
                .replace(CATEGORY, context));
    }

    /**
     * Sends the confirmation message to the target.
     *
     * @param target the target player.
     * @param amount the amount of quests added.
     */
    private void sendTargetTotalCategoryMessage(Player target, int amount, String category) {
        final String msg = QuestsMessages.ADD_TOTAL_CATEGORY_TARGET.toString();
        if (msg != null) target.sendMessage(msg
                .replace(AMOUNT, String.valueOf(amount))
                .replace(CATEGORY, category));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase(ADD)) {
            return Collections.singletonList(TOTAL);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase(ADD) && args[1].equalsIgnoreCase(TOTAL)) {
            final Set<String> categories = CategoriesLoader.getAllCategories().keySet();
            final List<String> completions = new ArrayList<>(categories);

            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            return completions;
        }

        if (args.length == 4 && args[0].equalsIgnoreCase(ADD) && args[1].equalsIgnoreCase(TOTAL) && CategoriesLoader.hasCategory(args[2])) {
            return null;
        }

        return Collections.emptyList();
    }
}
