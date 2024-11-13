package com.ordwen.odailyquests.commands.interfaces.playerinterface.items;

import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerHead {

    private static ItemStack playerHead;
    private static SkullMeta skullMeta;
    private static boolean usePlaceholders = false;

    /**
     * Get player head.
     *
     * @return player head.
     */
    public static ItemStack getPlayerHead(final Player player) {

        final SkullMeta meta = PlayerHead.skullMeta.clone();
        if (usePlaceholders) meta.setDisplayName(PAPIHook.getPlaceholders(player, meta.getDisplayName()));

        meta.setOwningPlayer(player);
        final List<String> itemDesc = meta.getLore();

        for (String string : itemDesc) {

            final int index = itemDesc.indexOf(string);

            if (usePlaceholders) {
                string = PAPIHook.getPlaceholders(player, string);
            }

            final PlayerQuests playerQuests = QuestsManager.getActiveQuests().get(player.getName());
            itemDesc.set(index, ColorConvert.convertColorCode(string)
                    .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName())));
        }

        meta.setLore(itemDesc);
        playerHead.setItemMeta(meta);
        return playerHead;
    }

    /**
     * Init player head.
     */
    public void initPlayerHead() {
        final ConfigurationSection playerHeadSection = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface.player_head");

        playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        skullMeta = (SkullMeta) playerHead.getItemMeta();

        skullMeta.setDisplayName(ColorConvert.convertColorCode(playerHeadSection.getString(".item_name")));
        skullMeta.setLore(playerHeadSection.getStringList(".item_description"));

        if (playerHeadSection.isInt(".custom_model_data"))
            skullMeta.setCustomModelData(playerHeadSection.getInt(".custom_model_data"));

        if (playerHeadSection.isBoolean(".use_placeholders"))
            usePlaceholders = playerHeadSection.getBoolean(".use_placeholders");
    }

}
