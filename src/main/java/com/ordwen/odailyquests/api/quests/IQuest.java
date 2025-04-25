package com.ordwen.odailyquests.api.quests;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the base interface for all quest types in the quest system.
 * <p>
 * This interface defines the necessary methods that must be implemented by any class that represents a quest.
 * These methods include retrieving the quest type, determining whether the quest can progress, and loading quest parameters.
 */
public interface IQuest {

    /**
     * Returns the type of the quest.
     *
     * @return a string representing the type of the quest.
     */
    String getType();

    /**
     * Determines if the quest can progress based on the provided event and progression information.
     * <p>
     * This method allows quests to check if they can proceed, often based on player actions or other conditions.
     * </p>
     *
     * @param provided the event that might trigger the progression (can be {@code null} if not applicable).
     * @param progression the progression information for the player.
     * @return {@code true} if the quest can progress, {@code false} otherwise.
     */
    boolean canProgress(@Nullable Event provided, Progression progression);

    /**
     * Loads the parameters required for the quest from the provided configuration section.
     * <p>
     * This method is used to load specific data about the quest from a configuration file. This could include quest objectives, items, or other quest-related data.
     * </p>
     *
     * @param section the configuration section containing the quest data.
     * @param file the file path from which the parameters are loaded.
     * @param index the index of the quest in the configuration.
     * @return {@code true} if the parameters were successfully loaded, {@code false} otherwise.
     */
    boolean loadParameters(ConfigurationSection section, String file, String index);
}