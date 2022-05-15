package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.QuestsManager;

import java.util.Calendar;

public class TimeRemain {

    private static ConfigurationFiles configurationFiles;

    public TimeRemain(ConfigurationFiles configurationFiles) {
        TimeRemain.configurationFiles = configurationFiles;
    }
    /**
     * Get the time remain before the next quests draw.
     *
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {

        long timestamp = QuestsManager.getActiveQuests().get(playerName).getTimestamp();
        long diff;

        if (configurationFiles.getConfigFile().getInt("timestamp_mode") == 1) {
            Calendar oldCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);
            oldCal.set(Calendar.HOUR_OF_DAY, oldCal.getActualMinimum(Calendar.HOUR_OF_DAY));
            oldCal.set(Calendar.MINUTE, oldCal.getActualMinimum(Calendar.MINUTE));
            oldCal.set(Calendar.SECOND, oldCal.getActualMinimum(Calendar.SECOND));
            oldCal.set(Calendar.MILLISECOND, oldCal.getActualMinimum(Calendar.MILLISECOND));

            Calendar currentCal = Calendar.getInstance();
            currentCal.setTimeInMillis(System.currentTimeMillis());

            diff = currentCal.getTimeInMillis() - oldCal.getTimeInMillis();
        } else {
            diff = System.currentTimeMillis() - timestamp;
        }

        String timeRemain = "";

        long rest;
        int minutes;
        int hours;
        int days;

        String d = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("days");
        String h = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("hours");
        String m = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("minutes");

        switch (configurationFiles.getConfigFile().getInt("temporality_mode")) {
            case 1:
                rest = 86400000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);

                if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
            case 2:
                rest = 604800000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                days = (int) (rest / (1000 * 60 * 60 * 24));

                if (days != 0) {
                    timeRemain = String.format("%d" + d + "%d" + h + "%d" + m, days, hours, minutes);
                }
                else if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
            case 3:
                rest = 2678400000L - diff;
                minutes = (int) ((rest / (1000 * 60)) % 60);
                hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                days = (int) (rest / (1000 * 60 * 60 * 24));

                if (days != 0) {
                    timeRemain = String.format("%d" + d + "%d" + h + "%d" + m, days, hours, minutes);
                }
                else if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
                break;
        }

        return timeRemain;
    }
}
