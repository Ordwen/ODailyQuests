package com.ordwen.odailyquests.events.listeners.integrations.npcs;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;

public class FancyNpcsHook extends AbstractNpcHook {

    public FancyNpcsHook(InterfacesManager interfacesManager) {
        super(interfacesManager);
    }

    @EventHandler
    public void onNpcInteractEvent(NpcInteractEvent event) {
        if (event.getInteractionType() != ActionTrigger.RIGHT_CLICK) return;
        handle(event.getNpc().getData().getDisplayName(), event.getPlayer());
    }
}