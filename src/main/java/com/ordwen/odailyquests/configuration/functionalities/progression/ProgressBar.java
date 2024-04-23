package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ProgressBar {

    private static String symbol;
    private static String completedColor;
    private static String remainingColor;
    private static int amountOfSymbols;

    private final ConfigurationFiles configurationFiles;

    public ProgressBar(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Load the progress bar configuration.
     */
    public void load() {
        final ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("progress_bar");
        if (section == null) {
            PluginLogger.warn("Progress bar section is missing in the configuration file.");
            PluginLogger.warn("Using default progress bar.");

            setSymbol("|");
            setCompletedColor(ChatColor.GREEN.toString());
            setRemainingColor(ChatColor.GRAY.toString());
            setAmountOfSymbols(20);

            return;
        }

        setSymbol(section.contains("symbol") ? section.getString("symbol") : "|");
        setCompletedColor(section.contains("completed_color") ? ColorConvert.convertColorCode(section.getString("completed_color")) : ChatColor.GREEN.toString());
        setRemainingColor(section.contains("remaining_color") ? ColorConvert.convertColorCode(section.getString("remaining_color")) : ChatColor.GRAY.toString());
        setAmountOfSymbols(section.contains("amount_of_symbols") ? section.getInt("amount_of_symbols") : 20);
    }

    /**
     * Get a progress bar, with a specified amount and required amount.
     *
     * @param amount   progress amount.
     * @param required required amount.
     * @return progress bar.
     */
    public static String getProgressBar(int amount, int required) {
        final StringBuilder builder = new StringBuilder();

        final int amountOfCompleted = amount * amountOfSymbols / required;
        final int amountOfRemaining = amountOfSymbols - amountOfCompleted;

        for (int i = 0; i < amountOfCompleted; i++) {
            builder.append(completedColor).append(symbol);
        }

        for (int i = 0; i < amountOfRemaining; i++) {
            builder.append(remainingColor).append(symbol);
        }

        return builder.toString();
    }

    private static void setSymbol(String symbol) {
        ProgressBar.symbol = symbol;
    }

    private static void setCompletedColor(String completedColor) {
        ProgressBar.completedColor = completedColor;
    }

    private static void setRemainingColor(String remainingColor) {
        ProgressBar.remainingColor = remainingColor;
    }

    private static void setAmountOfSymbols(int amountOfSymbols) {
        ProgressBar.amountOfSymbols = amountOfSymbols;
    }
}
