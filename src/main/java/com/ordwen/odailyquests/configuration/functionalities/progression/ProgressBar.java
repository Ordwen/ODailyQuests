package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ProgressBar implements IConfigurable {

    private String symbol;
    private String completedColor;
    private String remainingColor;
    private int amountOfSymbols;

    private final ConfigurationFile configurationFile;

    public ProgressBar(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * Load the progress bar configuration.
     */
    @Override
    public void load() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("progress_bar");

        if (section == null) {
            PluginLogger.warn("Progress bar section is missing in the configuration file.");
            PluginLogger.warn("Using default progress bar.");

            symbol = "|";
            completedColor = ChatColor.GREEN.toString();
            remainingColor = ChatColor.GRAY.toString();
            amountOfSymbols = 20;

            return;
        }

        symbol = section.getString("symbol", "|");
        completedColor = ColorConvert.convertColorCode(section.getString("completed_color", ChatColor.GREEN.toString()));
        remainingColor = ColorConvert.convertColorCode(section.getString("remaining_color", ChatColor.GRAY.toString()));
        amountOfSymbols = section.getInt("amount_of_symbols", 20);
    }

    /**
     * Get a progress bar, with a specified amount and required amount.
     *
     * @param amount   progress amount.
     * @param required required amount.
     * @return progress bar.
     */
    public String getProgressBarInternal(int amount, int required) {
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

    /**
     * Get loaded instance of class.
     *
     * @return ProgressBar instance.
     */
    private static ProgressBar getInstance() {
        return ConfigFactory.getConfig(ProgressBar.class);
    }

    /**
     * Get a progress bar, with a specified amount and required amount. Method is static.
     *
     * @param amount   progress amount.
     * @param required required amount.
     * @return progress bar.
     */
    public static String getProgressBar(int amount, int required) {
        return getInstance().getProgressBarInternal(amount, required);
    }
}
