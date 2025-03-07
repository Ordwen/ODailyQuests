package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.nms.NMSHandler;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {

    private ItemUtils() {}

    public static ItemStack getCustomHead(String texture) {
        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta = NMSHandler.applySkullTexture(skullMeta, texture);

        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
