package com.ordwen.odailyquests.quests.types;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;
import java.util.List;

public class PotionQuest extends AbstractQuest {

    final List<ItemStack> requiredItems;
    private final PotionType potionType;
    private final int potionLevel;

    public PotionQuest(GlobalQuest globalQuest, List<ItemStack> requiredItems, @Nullable PotionType potionType, int potionLevel) {
        super(globalQuest);
        this.potionType = potionType;
        this.potionLevel = potionLevel;
        this.requiredItems = requiredItems;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public int getPotionLevel() {
        return potionLevel;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public boolean isRequiredItem(ItemStack itemStack) {

        for (ItemStack item : requiredItems) {
            if (item.getType() != itemStack.getType()) continue;
            if (potionType == null && potionLevel == -1) return true;

            if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof PotionMeta potionMeta) {
                if (potionType != null && potionMeta.getBasePotionData().getType() != potionType) continue;
                // check potion level
                // if (potionLevel != -1 && ) continue;
                return true;
            }
        }

        return false;
    }
}
