package com.ordwen.odailyquests.commands.interfaces.playerinterface.items;

import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerHead {

    private static final String SLOT_PARAMETER = "slot";
    
    private final PlayerInterfaceFile playerInterfaceFile;

    private boolean enabled;
    private final Set<Integer> slots = new HashSet<>();

    private ItemStack head;
    private SkullMeta meta;
    private boolean usePlaceholders = false;

    public PlayerHead(PlayerInterfaceFile playerInterfaceFile) {
        this.playerInterfaceFile = playerInterfaceFile;
    }

    /**
     * Init player head.
     */
    public void load() {
        final ConfigurationSection section = playerInterfaceFile.getConfig().getConfigurationSection("player_interface.player_head");
        if (section == null) {
            PluginLogger.error("Player head section not found in the player interface file.");
            enabled = false;
            return;
        }

        enabled = section.getBoolean(".enabled");
        if (!enabled) return;

        head = new ItemStack(Material.PLAYER_HEAD, 1);
        meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(ColorConvert.convertColorCode(section.getString(".item_name")));
        meta.setLore(section.getStringList(".item_description"));

        if (section.isInt(".custom_model_data"))
            meta.setCustomModelData(section.getInt(".custom_model_data"));

        if (section.isBoolean(".use_placeholders"))
            usePlaceholders = section.getBoolean(".use_placeholders");

        slots.clear();
        if (section.isList(SLOT_PARAMETER)) slots.addAll(section.getIntegerList(SLOT_PARAMETER));
        else slots.add(section.getInt(SLOT_PARAMETER) - 1);
    }

    public Inventory setPlayerHead(Inventory inventory, Player player, int size) {
        if (!enabled) return inventory;

        for (int slot : slots) {
            if (slot >= 0 && slot <= size) {
                inventory.setItem(slot, getPlayerHead(player));
            } else {
                PluginLogger.error("An error occurred when loading the player interface.");
                PluginLogger.error("The slot defined for the player head is out of bounds.");
            }
        }

        return inventory;
    }

    public ItemStack getPlayerHead(Player player) {
        final SkullMeta clone = this.meta.clone();
        if (usePlaceholders) clone.setDisplayName(PAPIHook.getPlaceholders(player, clone.getDisplayName()));

        clone.setOwningPlayer(player);
        final List<String> lore = clone.getLore();
        if (lore == null) return head;

        for (String string : lore) {
            int index = lore.indexOf(string);
            if (usePlaceholders) {
                string = PAPIHook.getPlaceholders(player, string);
            }

            final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(player.getName());
            lore.set(index, ColorConvert.convertColorCode(string)
                    .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName())));
        }

        clone.setLore(lore);
        head.setItemMeta(clone);
        return head;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
