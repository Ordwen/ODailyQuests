package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.quests.player.QuestsManager;

import java.util.Calendar;

public class TimeRemain {

    /**
     * Get the time remain before the next quests draw.
     *
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {

        long timestamp = QuestsManager.getActiveQuests().get(playerName).getTimestamp();
        long diff;

        if (Modes.getTimestampMode() == 1) {
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

        String d = Temporality.getDayInitial();
        String h = Temporality.getHourInitial();
        String m = Temporality.getMinuteInitial();

        long rest;

        switch (Temporality.getTemporalityMode()) {
            case 1 -> {
                rest = 86400000L - diff;
                int minutes = (int) ((rest / (1000 * 60)) % 60);
                int hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                if (hours != 0) {
                    timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
                } else if (minutes != 0) {
                    timeRemain = String.format("%d" + m, minutes);
                } else {
                    timeRemain = "Few seconds.";
                }
            }
            case 2 -> {
                rest = 604800000L - diff;
                timeRemain = getTimeRemainString(rest, d, h, m);
            }
            case 3 -> {
                rest = 2678400000L - diff;
                timeRemain = getTimeRemainString(rest, d, h, m);
            }
        }

        return timeRemain;
    }

    /**
     * Get a text from remaining time.
     * @param rest remaining time in milliseconds.
     * @param d day initial.
     * @param h hour initial.
     * @param m minute initial.
     * @return formatted text with remaining time.
     */
    private static String getTimeRemainString(long rest, String d, String h, String m) {
        int minutes;
        int hours;
        int days;
        String timeRemain;

        minutes = (int) ((rest / (1000 * 60)) % 60);
        hours = (int) ((rest / (1000 * 60 * 60)) % 24);
        days = (int) (rest / (1000 * 60 * 60 * 24));

        if (days != 0) {
            timeRemain = String.format("%d" + d + "%d" + h + "%d" + m, days, hours, minutes);
        } else if (hours != 0) {
            timeRemain = String.format("%d" + h + "%d" + m, hours, minutes);
        } else if (minutes != 0) {
            timeRemain = String.format("%d" + m, minutes);
        } else {
            timeRemain = "Few seconds.";
        }

        return timeRemain;
    }
}
