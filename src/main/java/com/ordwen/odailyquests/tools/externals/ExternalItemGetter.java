package com.ordwen.odailyquests.tools.externals;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.Buttons;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.tools.Pair;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.inventory.ItemStack;

public abstract class ExternalItemGetter implements IExternalItemGetter {

    /**
     * Get an Oraxen item by its namespace.
     * @param namespace the namespace of the item
     * @return the ItemStack or null if it does not exist
     */
    @Override
    public Pair<String, ItemStack> getOraxenItem(String namespace) {
        if (!OraxenEnabled.isEnabled()) {
            return new Pair<>("Oraxen is not enabled.", null);
        }

        if (!OraxenItems.exists(namespace)) {
            return new Pair<>("The item " + namespace + " does not exist in Oraxen.", null);
        }

        return new Pair<>("", OraxenItems.getItemById(namespace).build());
    }

    /**
     * Get an ItemsAdder item by its namespace.
     * @param namespace the namespace of the item
     * @return the ItemStack or null if it does not exist
     */
    @Override
    public Pair<String, ItemStack> getItemsAdderItem(String namespace) {
        if (!ItemsAdderEnabled.isEnabled()) {
            return new Pair<>("ItemsAdder is not enabled.", null);
        }

        if (!namespace.contains(":")) {
            return new Pair<>("You need to provide the namespace and the id of the item.", null);
        }

        if (!CustomStack.isInRegistry(namespace)) {
            return new Pair<>("The item " + namespace + " does not exist in ItemsAdder.", null);
        }

        return new Pair<>("", CustomStack.getInstance(namespace).getItemStack());
    }

    /**
     * Get an MMOItems item by its namespace.
     * @param namespace the namespace of the item
     * @return the ItemStack or null if it does not exist
     */
    @Override
    public Pair<String, ItemStack> getMMOItemsItem(String namespace) {
        if (!namespace.contains(":")) {
            return new Pair<>("You need to provide the category and the id of the item.", null);
        }

        final String[] split = namespace.split(":", 2);

        final Type type = MMOItems.plugin.getTypes().get(split[0]);
        if (type == null) {
            return new Pair<>("The category " + split[0] + " does not exist in MMOItems.", null);
        }

        final MMOItem item = MMOItems.plugin.getMMOItem(type, split[1]);
        if (item == null) {
            return new Pair<>("The item " + split[1] + " does not exist in MMOItems.", null);
        }

        return new Pair<>("", item.newBuilder().build());
    }

    /**
     * Get a custom head by its texture.
     * @param texture the texture of the head
     * @return the ItemStack textured or not
     */
    @Override
    public Pair<String, ItemStack> getCustomHead(String texture) {
        return new Pair<>("", Buttons.getCustomHead(texture));
    }
}
