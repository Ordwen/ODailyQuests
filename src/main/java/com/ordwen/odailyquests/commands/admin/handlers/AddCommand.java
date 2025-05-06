package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.commands.admin.AdminCommandBase;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AddCommand extends AdminCommandBase {

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPermission() {
        return QuestsPermissions.QUESTS_ADMIN.getPermission();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 4) {
            final String typeOrCategory = args[1];
            final String playerName = args[2];
            final String amountStr = args[3];

            final Player target = Bukkit.getPlayer(playerName);
            if (target == null) {
                invalidPlayer(sender);
                return;
            }

            final int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                invalidAmount(sender);
                return;
            }

            if (typeOrCategory.equalsIgnoreCase("total")) {
                addTotalAmount(sender, target, amount);
            } else if (CategoriesLoader.getAllCategories().containsKey(typeOrCategory)) {
                addCategoryAmount(sender, target, typeOrCategory, amount);
            } else {
                help(sender);
            }
        } else {
            help(sender);
        }
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

        sendAdminMessage(sender, target.getName(), amount, "total");
        sendTargetMessage(target, amount, "total");
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
        playerQuests.addCategoryAchievedQuests(category, amount);

        sendAdminMessage(sender, target.getName(), amount, category);
        sendTargetMessage(target, amount, category);
    }

    /**
     * Sends the confirmation message to the sender.
     *
     * @param amount the amount of quests added.
     */
    private void sendAdminMessage(CommandSender sender, String targetName, int amount, String context) {
        final String msg = QuestsMessages.ADD_TOTAL_ADMIN.toString();
        if (msg != null) sender.sendMessage(msg
                .replace("%target%", targetName)
                .replace("%amount%", String.valueOf(amount))
                .replace("%category%", context));
    }

    /**
     * Sends the confirmation message to the target.
     *
     * @param target the target player.
     * @param amount the amount of quests added.
     */
    private void sendTargetMessage(Player target, int amount, String context) {
        final String msg = QuestsMessages.ADD_TOTAL_TARGET.toString();
        if (msg != null) target.sendMessage(msg
                .replace("%amount%", String.valueOf(amount))
                .replace("%category%", context));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            final Set<String> categories = CategoriesLoader.getAllCategories().keySet();
            final List<String> completions = new ArrayList<>(categories.stream().toList());
            completions.add("total");
            return completions;
        }

        if (args.length == 3) {
            return null;
        }

        return Collections.emptyList();
    }
}
