package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.GetPlaceholders;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerHead {

    private static ItemStack playerHead;
    private static SkullMeta skullMeta;

    /**
     * Init player head.
     */
    public void initPlayerHead() {
        final ConfigurationSection playerHeadSection = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface.player_head");

        playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        skullMeta = (SkullMeta) playerHead.getItemMeta();

        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(playerHeadSection.getString(".item_name"))));
        skullMeta.setLore(playerHeadSection.getStringList(".item_description"));

        if (playerHeadSection.isInt(".custom_model_data")) {
            skullMeta.setCustomModelData(playerHeadSection.getInt(".custom_model_data"));
        }
    }

    /**
     * Get player head.
     * @return player head.
     */
    public static ItemStack getPlayerHead(Player player) {

        SkullMeta meta = PlayerHead.skullMeta.clone();

        meta.setOwningPlayer(player);
        List<String> itemDesc = meta.getLore();

        for (String string : itemDesc) {
            itemDesc.set(itemDesc.indexOf(string), GetPlaceholders.getPlaceholders(player, string));
        }

        for (String string : itemDesc) {
            itemDesc.set(itemDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(string)
                    .replace("%achieved%", String.valueOf(QuestsManager.getActiveQuests().get(player.getName()).getAchievedQuests()))
                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName()))));
        }

        meta.setLore(itemDesc);
        playerHead.setItemMeta(meta);
        return playerHead;
    }

}
