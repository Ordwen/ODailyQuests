package com.ordwen.odailyquests.commands.admin.handlers;

import com.ordwen.odailyquests.commands.admin.ACommandHandler;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.externs.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.externs.hooks.holograms.HolographicDisplaysHook;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoloCommand extends ACommandHandler {

    public HoloCommand(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public void handle() {
        if (!(sender instanceof Player)) {
            playerOnly();
            return;
        }

        if (args.length >= 3 && args[1] != null && args[2] != null) {
            switch (args[1]) {
                case "create" -> create();
                case "delete" -> delete();
                default -> help();
            }
        } else help();
    }

    /**
     * Create a hologram.
     */
    private void create() {
        if (args.length != 4 || args[3] == null) {
            help();
            return;
        }

        int index;
        try {
            index = Integer.parseInt(args[3]) - 1;
        } catch (Exception e) {
            invalidQuest();
            return;
        }

        switch (args[2]) {
            case "global" -> {
                if (CategoriesLoader.getGlobalQuests().isEmpty()) {
                    enabled();
                    return;
                }
                HolographicDisplaysHook.createHologram(index, CategoriesLoader.getGlobalQuests(), ((Player) sender).getPlayer());
            }
            case "easy" -> {
                if (!CategoriesLoader.getEasyQuests().isEmpty()) {
                    disabled();
                    return;
                }
                HolographicDisplaysHook.createHologram(index, CategoriesLoader.getEasyQuests(), ((Player) sender).getPlayer());
            }
            case "medium" -> {
                if (!CategoriesLoader.getMediumQuests().isEmpty()) {
                    disabled();
                    return;
                }
                HolographicDisplaysHook.createHologram(index, CategoriesLoader.getMediumQuests(), ((Player) sender).getPlayer());
            }
            case "hard" -> {
                if (CategoriesLoader.getHardQuests().isEmpty()) {
                    disabled();
                    return;
                }
                HolographicDisplaysHook.createHologram(index, CategoriesLoader.getHardQuests(), ((Player) sender).getPlayer());
            }
            default -> invalidCategory();
        }
    }

    /**
     * Send a message to the sender if categorized quests are disabled.
     */
    private void disabled() {
        final String msg = QuestsMessages.HOLO_CATEGORIZED_DISABLED.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Send a message to the sender if categorized quests are enabled.
     */
    private void enabled() {
        final String msg = QuestsMessages.HOLO_CATEGORIZED_ENABLED.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Send a message to the sender if the category is invalid.
     */
    private void invalidCategory() {
        final String msg = QuestsMessages.INVALID_CATEGORY.toString();
        if (msg != null) sender.sendMessage(msg);
    }

    /**
     * Delete a hologram.
     */
    private void delete() {
        int index;
        try {
            index = Integer.parseInt(args[2]);
        } catch (Exception e) {
            invalidQuest();
            return;
        }
        if (HologramsManager.deleteHologram(index)) {
            final String msg = QuestsMessages.HOLO_DELETED.toString();
            if (msg != null) sender.sendMessage(msg);
        } else {
            final String msg = QuestsMessages.HOLO_INVALID_INDEX.toString();
            if (msg != null) sender.sendMessage(msg);
        }
    }
}
