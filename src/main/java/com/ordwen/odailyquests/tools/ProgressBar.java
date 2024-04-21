package com.ordwen.odailyquests.tools;

import org.bukkit.ChatColor;

/**
 * Utility class for progress bars.
 * author: Robotv2
 */
public class ProgressBar {

    private ProgressBar() {
    }

    /**
     * Get a progress bar, with a specified amount and required amount.
     *
     * @param amount    progress amount.
     * @param required  required amount.
     * @param barNumber number of bars.
     * @return progress bar.
     */
    public static String getProgressBar(int amount, int required, int barNumber) {
        final StringBuilder builder = new StringBuilder();

        final int greenBarNumber = amount * barNumber / required;
        final int grayBarNumber = barNumber - greenBarNumber;

        for (int i = 0; i < greenBarNumber; i++) {
            builder.append(ChatColor.GREEN).append("|");
        }

        for (int i = 0; i < grayBarNumber; i++) {
            builder.append(ChatColor.GRAY).append("|");
        }

        return builder.toString();
    }

    /**
     * Get a progress bar, with a specified amount and required amount.
     *
     * @param amount   progress amount.
     * @param required required amount.
     * @return progress bar.
     */
    public static String getProgressBar(int amount, int required) {
        return getProgressBar(amount, required, 20);
    }
}
