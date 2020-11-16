package de.mark225.bluebridge.core.util;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

import java.awt.*;

public class BlueBridgeUtils {

    public static String escapeHtml(String in){
        return StringEscapeUtils.escapeHtml4(in);
    }

    public static String replace(StringLookupWrapper stringLookup, String input){
        return new StringSubstitutor(stringLookup).replace(input);
    }

    public static Color stringToColor(String hex){
        switch (hex.length()) {
            case 6:
                return new Color(
                        Integer.valueOf(hex.substring(0, 2), 16),
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                        Integer.valueOf(hex.substring(2, 4), 16),
                        Integer.valueOf(hex.substring(4, 6), 16),
                        Integer.valueOf(hex.substring(6, 8), 16),
                        Integer.valueOf(hex.substring(0, 2), 16));
        }
        return null;
    }
}
