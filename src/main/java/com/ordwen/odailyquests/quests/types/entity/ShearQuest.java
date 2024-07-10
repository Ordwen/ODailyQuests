package com.ordwen.odailyquests.quests.types.entity;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearQuest extends EntityQuest {

    public ShearQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "SHEAR";
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!super.loadParameters(section, file, index)) return false;
        dyeColor = getDyeColor(section, file, index);

        return true;
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerShearEntityEvent event) {
            if (!isRequiredEntity(event.getEntity().getType())) return false;

            if (event.getEntity() instanceof Sheep sheep) {
                if (dyeColor == null) return true;
                return sheep.getColor() == dyeColor;
            }

            return true;
        }

        return false;
    }

    /**
     * Get the required dye color.
     *
     * @param section the configuration section
     * @param file    the file name
     * @param index   the quest index
     * @return the dye color, or null if the dye color is invalid/missing
     */
    private DyeColor getDyeColor(ConfigurationSection section, String file, String index) {
        final String expected = section.getString(".sheep_color");
        if (expected == null) return null;

        try {
            return DyeColor.valueOf(expected.toUpperCase());
        } catch (Exception e) {
            PluginLogger.configurationError(file, index, "sheep_color", "Invalid dye type detected.");
            return null;
        }
    }
}
