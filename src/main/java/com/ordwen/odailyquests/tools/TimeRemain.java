package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.quests.player.QuestsManager;

import java.util.Calendar;

public class TimeRemain {

    private TimeRemain() {
    }

    /**
     * Get the time remain before the next quests draw.
     *
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {

        final String d = Temporality.getDayInitial();
        final String h = Temporality.getHourInitial();
        final String m = Temporality.getMinuteInitial();
        final String fewSeconds = Temporality.getFewSeconds();

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

        long rest;

        return switch (Temporality.getTemporalityMode()) {
            case 1 -> {
                rest = 86400000L - diff;
                int minutes = (int) ((rest / (1000 * 60)) % 60);
                int hours = (int) ((rest / (1000 * 60 * 60)) % 24);
                if (hours != 0) {
                    yield String.format("%d%s%d%s", hours, h, minutes, m);
                } else if (minutes != 0) {
                    yield String.format("%d%s", minutes, m);
                } else {
                    yield fewSeconds;
                }
            }
            case 2 -> {
                rest = 604800000L - diff;
                yield getTimeRemainString(rest, d, h, m);
            }
            case 3 -> {
                rest = 2678400000L - diff;
                yield getTimeRemainString(rest, d, h, m);
            }
            default -> "";
        };
    }

    /**
     * Get a text from remaining time.
     *
     * @param rest remaining time in milliseconds.
     * @param d    day initial.
     * @param h    hour initial.
     * @param m    minute initial.
     * @return formatted text with remaining time.
     */
    private static String getTimeRemainString(long rest, String d, String h, String m) {
        final int days = (int) (rest / (1000 * 60 * 60 * 24));
        final int hours = (int) ((rest / (1000 * 60 * 60)) % 24);
        final int minutes = (int) ((rest / (1000 * 60)) % 60);

        final String timeRemain;

        if (days != 0) timeRemain = String.format("%d%s%d%s%d%s", days, d, hours, h, minutes, m);
        else if (hours != 0) timeRemain = String.format("%d%s%d%s", hours, h, minutes, m);
        else if (minutes != 0) timeRemain = String.format("%d%s", minutes, m);
        else timeRemain = "Few seconds.";

        return timeRemain;
    }
}
