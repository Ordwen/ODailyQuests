package com.ordwen.odailyquests.commands.interfaces.playerinterface;

import com.ordwen.odailyquests.files.PlayerInterfaceFile;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerHead {

    private static PlayerInterfaceFile playerInterfaceFile;

    public PlayerHead(PlayerInterfaceFile playerInterfaceFile) {
        PlayerHead.playerInterfaceFile = playerInterfaceFile;
    }

    private static ItemStack playerHead;
    private static SkullMeta skullMeta;

    /**
     * Init player head.
     */
    public void initPlayerHead() {
        playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                ColorConvert.convertColorCode(playerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface.player_head").getString(".item_name"))));
        skullMeta.setLore(playerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection("player_interface.player_head").getStringList(".item_description"));
    }

    /**
     * Get player head.
     * @return player head.
     */
    public static ItemStack getPlayerHead(Player player) {

        skullMeta.setOwningPlayer(player);
        List<String> itemDesc = skullMeta.getLore();

        for (String string : itemDesc) {
            itemDesc.set(itemDesc.indexOf(string), ChatColor.translateAlternateColorCodes('&', ColorConvert.convertColorCode(string)
                    .replace("%achieved%", String.valueOf(QuestsManager.getActiveQuests().get(player.getName()).getAchievedQuests()))
                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName()))));
        }

        skullMeta.setLore(itemDesc);
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }

}
