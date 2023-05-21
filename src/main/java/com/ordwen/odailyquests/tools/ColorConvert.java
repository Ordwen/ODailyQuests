package com.ordwen.odailyquests.tools;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorConvert {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String convertColorCode(String msg) {
        if (msg == null) return null;

        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            String color = msg.substring(matcher.start(), matcher.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            matcher = pattern.matcher(msg);
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
