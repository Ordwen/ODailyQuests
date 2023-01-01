package com.ordwen.odailyquests.quests.types;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;
import java.util.List;

public class PotionQuest extends AbstractQuest {

    final List<ItemStack> requiredItems;
    private final PotionType potionType;
    private final boolean upgraded;
    private final boolean extended;

    public PotionQuest(GlobalQuest globalQuest, List<ItemStack> requiredItems, @Nullable PotionType potionType, boolean upgraded, boolean extended) {
        super(globalQuest);
        this.potionType = potionType;
        this.upgraded = upgraded;
        this.extended = extended;
        this.requiredItems = requiredItems;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public boolean isExtended() {
        return extended;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public boolean isRequiredItem(ItemStack itemStack) {

        for (ItemStack item : requiredItems) {
            if (item.getType() != itemStack.getType()) continue;
            if (potionType == null && !upgraded && !extended) return true;

            if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof PotionMeta potionMeta) {
                if (potionType != null && potionMeta.getBasePotionData().getType() != potionType) continue;
                if (upgraded && !potionMeta.getBasePotionData().isUpgraded()) continue;
                if (extended && !potionMeta.getBasePotionData().isExtended()) continue;

                return true;
            }
        }

        return false;
    }
}
