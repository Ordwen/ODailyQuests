package com.ordwen.odailyquests.commands.interfaces.playerinterface.items;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
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

    /**
     * Init player head.
     */
    public void initPlayerHead() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            final ConfigurationSection playerHeadSection = PlayerInterfaceFile.getPlayerInterfaceFileConfiguration().getConfigurationSection(questSystem.getConfigPath() + "player_interface.player_head");

            questSystem.setPlayerHead(new ItemStack(Material.PLAYER_HEAD, 1));
            questSystem.setSkullMeta((SkullMeta) questSystem.getPlayerHead().getItemMeta());

            questSystem.getSkullMeta().setDisplayName(ColorConvert.convertColorCode(playerHeadSection.getString(".item_name")));
            questSystem.getSkullMeta().setLore(playerHeadSection.getStringList(".item_description"));

            if (playerHeadSection.isInt(".custom_model_data"))
                questSystem.getSkullMeta().setCustomModelData(playerHeadSection.getInt(".custom_model_data"));

            if (playerHeadSection.isBoolean(".use_placeholders"))
                questSystem.setUsePlaceholders(playerHeadSection.getBoolean(".use_placeholders"));
        });
    }

    /**
     * Get player head.
     * @return player head.
     */
    public static ItemStack getPlayerHead(Player player, QuestSystem questSystem) {

        SkullMeta meta = questSystem.getSkullMeta().clone();
        if (questSystem.isUsePlaceholders()) meta.setDisplayName(PAPIHook.getPlaceholders(player, meta.getDisplayName()));

        meta.setOwningPlayer(player);
        List<String> itemDesc = meta.getLore();

        for (String string : itemDesc) {

            int index = itemDesc.indexOf(string);

            if (questSystem.isUsePlaceholders()) {
                string = PAPIHook.getPlaceholders(player, string);
            }

            final PlayerQuests playerQuests = questSystem.getActiveQuests().get(player.getName());
            itemDesc.set(index, ColorConvert.convertColorCode(string)
                    .replace("%achieved%", String.valueOf(playerQuests.getAchievedQuests()))
                    .replace("%drawIn%", TimeRemain.timeRemain(player.getName(), questSystem)));
        }

        meta.setLore(itemDesc);
        questSystem.getPlayerHead().setItemMeta(meta);
        return questSystem.getPlayerHead();
    }

}
