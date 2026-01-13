package com.ordwen.odailyquests.events.antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Anti-glitch helper for PLACE quests: prevents "break -> place" abuse.
 * <p>
 * When enabled (see {@link Antiglitch#isStoreBrokenBlocks()}), this utility tracks
 * the amount of placeable block drops obtained from recent block breaks by a player.
 * If the same player places those blocks shortly after, PLACE quest progression is
 * canceled for those placements (while still allowing the placement to occur).
 * </p>
 *
 * <h3>Performance and compatibility</h3>
 * <ul>
 *   <li>No ItemStack NBT/PDC is added (stacking and plugin compatibility are preserved).</li>
 *   <li>All operations are O(1) on the server thread.</li>
 *   <li>Memory is strictly bounded per player (see {@link Antiglitch#getBrokenBlocksMaxMaterialsPerPlayer()}).</li>
 *   <li>Expiration is lazy: entries are validated only when accessed (no global scans/tasks).</li>
 * </ul>
 *
 * <h3>Typical usage</h3>
 * <ul>
 *   <li>Call {@link #recordBrokenDrops(Player, List)} from {@code BlockDropItemEvent}.</li>
 *   <li>Call {@link #recordBrokenDropsFromStacks(Player, Collection)} from virtual drop systems
 *       (custom events where drops are {@link ItemStack}s only).</li>
 *   <li>Call {@link #shouldCancelPlaceProgress(Player, Material)} from {@code BlockPlaceEvent}.</li>
 *   <li>Optionally call {@link #clear(Player)} on {@code PlayerQuitEvent}.</li>
 * </ul>
 */
public final class BrokenBlocksAntiglitch {

    private BrokenBlocksAntiglitch() {
    }

    /**
     * Per-material counter used to decide whether a placement should be ignored.
     */
    private static final class MaterialCounter {
        /**
         * Expiration timestamp (epoch millis). If now > expiresAtEpochMs, the entry is stale.
         */
        long expiresAtEpochMs;

        /**
         * Remaining amount of tracked drops for this material.
         */
        int countRemaining;
    }

    /**
     * Stores counters per player:
     * player UUID -> (material -> counter).
     * <p>
     * This map is intentionally kept small and bounded. Each player map is capped by
     * {@link Antiglitch#getBrokenBlocksMaxMaterialsPerPlayer()} distinct materials.
     * </p>
     */
    private static final Map<UUID, EnumMap<Material, MaterialCounter>> playerCounters = new HashMap<>();

    /**
     * Records block drops coming from a recent block break so they can be ignored later
     * for PLACE quest progression.
     * <p>
     * Intended usage: call from {@code BlockDropItemEvent} using {@code event.getItems()}.
     * Only drops that are placeable blocks ({@link Material#isBlock()}) are tracked.
     * </p>
     *
     * @param player       the player who broke the block
     * @param droppedItems the list of dropped {@link Item} entities
     */
    public static void recordBrokenDrops(Player player, List<Item> droppedItems) {
        if (!isEnabled()) return;
        if (player == null || droppedItems == null || droppedItems.isEmpty()) return;

        final UUID playerId = player.getUniqueId();

        for (Item droppedItem : droppedItems) {
            if (droppedItem == null) continue;

            final ItemStack stack = droppedItem.getItemStack();
            final Material material = stack.getType();
            if (!material.isBlock()) continue;

            addBrokenAmount(playerId, material, stack.getAmount());
        }
    }

    /**
     * Records recently broken block drops for PLACE anti-glitch when drops are already provided
     * as {@link ItemStack}s (virtual drop systems).
     * <p>
     * Intended usage: call from custom/virtual drop pipelines where no {@link Item} entity exists.
     * Only ItemStacks that are placeable blocks ({@link Material#isBlock()}) are tracked.
     * </p>
     *
     * @param player        the player who broke the block
     * @param droppedStacks the collection of dropped {@link ItemStack}s
     */
    public static void recordBrokenDropsFromStacks(Player player, Collection<? extends ItemStack> droppedStacks) {
        if (!isEnabled()) return;
        if (player == null || droppedStacks == null || droppedStacks.isEmpty()) return;

        final UUID playerId = player.getUniqueId();

        for (ItemStack stack : droppedStacks) {
            if (stack == null) continue;

            final Material material = stack.getType();
            if (!material.isBlock()) continue;

            addBrokenAmount(playerId, material, stack.getAmount());
        }
    }

    /**
     * Checks whether PLACE quest progression should be canceled for this placement.
     * <p>
     * If this method returns {@code true}, the block placement should still occur normally,
     * but your quest logic must NOT count it towards PLACE progression.
     * </p>
     *
     * @param player         the player placing the block
     * @param placedMaterial the material being placed
     * @return {@code true} if the placement is considered to come from a recent break by this player
     */
    public static boolean shouldCancelPlaceProgress(Player player, Material placedMaterial) {
        if (!isEnabled()) return false;
        if (player == null || placedMaterial == null) return false;

        return consumeOneIfRecent(player.getUniqueId(), placedMaterial);
    }

    /**
     * Clears all tracked data for the given player.
     *
     * @param player the player to clear
     */
    public static void clear(Player player) {
        if (player == null) return;
        playerCounters.remove(player.getUniqueId());
    }

    /**
     * Returns whether this anti-glitch feature is enabled in configuration.
     *
     * @return true if break->place tracking is enabled
     */
    private static boolean isEnabled() {
        return Antiglitch.isStoreBrokenBlocks();
    }

    /**
     * Adds a tracked amount of a given material for a player, refreshing the expiration window.
     * <p>
     * This method is O(1) and enforces a strict per-player cap on distinct tracked materials.
     *
     * @param playerId the player UUID
     * @param material the drop material
     * @param amount   the amount to add
     */
    private static void addBrokenAmount(UUID playerId, Material material, int amount) {
        if (amount <= 0) return;

        final long nowEpochMs = now();
        final long expiresAtEpochMs = nowEpochMs + Antiglitch.getBrokenBlocksPlaceWindowMillis();

        EnumMap<Material, MaterialCounter> countersByMaterial = playerCounters.get(playerId);
        if (countersByMaterial == null) {
            countersByMaterial = new EnumMap<>(Material.class);
            playerCounters.put(playerId, countersByMaterial);
        }

        MaterialCounter counter = countersByMaterial.get(material);

        // Lazy cleanup for this specific material entry.
        if (counter != null && counter.expiresAtEpochMs < nowEpochMs) {
            countersByMaterial.remove(material);
            counter = null;
        }

        // Hard cap: never allow tracking unbounded numbers of different materials per player.
        final int maxDistinctMaterials = Antiglitch.getBrokenBlocksMaxMaterialsPerPlayer();
        if (counter == null && countersByMaterial.size() >= maxDistinctMaterials) {
            // Fail-open: in extreme cases, do not track new materials to keep performance bounded.
            return;
        }

        if (counter == null) {
            counter = new MaterialCounter();
            countersByMaterial.put(material, counter);
        }

        counter.expiresAtEpochMs = expiresAtEpochMs; // refresh window
        counter.countRemaining += amount;
    }

    /**
     * Consumes one unit of the given material for the given player if it is still within the
     * configured time window.
     *
     * @param playerId the player UUID
     * @param material the material being placed
     * @return true if one unit was consumed (meaning the placement should be ignored for progression)
     */
    private static boolean consumeOneIfRecent(UUID playerId, Material material) {
        final long nowEpochMs = now();

        final EnumMap<Material, MaterialCounter> countersByMaterial = playerCounters.get(playerId);
        if (countersByMaterial == null) return false;

        final MaterialCounter counter = countersByMaterial.get(material);
        if (counter == null) return false;

        // Expired or empty => drop entry
        if (counter.expiresAtEpochMs < nowEpochMs || counter.countRemaining <= 0) {
            countersByMaterial.remove(material);
            if (countersByMaterial.isEmpty()) playerCounters.remove(playerId);
            return false;
        }

        // Consume one placement
        counter.countRemaining -= 1;

        if (counter.countRemaining <= 0) {
            countersByMaterial.remove(material);
            if (countersByMaterial.isEmpty()) playerCounters.remove(playerId);
        }

        return true;
    }

    /**
     * Returns the current time in epoch milliseconds.
     *
     * @return current epoch time in milliseconds
     */
    private static long now() {
        return System.currentTimeMillis();
    }
}
