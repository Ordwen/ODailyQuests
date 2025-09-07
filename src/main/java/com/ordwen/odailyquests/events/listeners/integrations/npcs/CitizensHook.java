package com.ordwen.odailyquests.events.listeners.integrations.npcs;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;

public class CitizensHook extends AbstractNpcHook {

    public CitizensHook(InterfacesManager interfacesManager) {
        super(interfacesManager);
    }

    @EventHandler
    public void onNPCClickEvent(NPCRightClickEvent event) {
        handle(event.getNPC().getRawName(), event.getClicker());
    }
}
