package de.mark225.bluebridge.core.util;

import de.bluecolored.bluemap.api.math.Color;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

public class BlueBridgeUtils {

    public static String escapeHtml(String in) {
        return StringEscapeUtils.escapeHtml4(in);
    }

    public static String replace(StringLookupWrapper stringLookup, String input) {
        return new StringSubstitutor(stringLookup.lookup).replace(input);
    }

    public static int colorToInt(Color color) {
        return (((int) (color.getAlpha() * 255) & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8) |
                ((color.getBlue() & 0xFF));
    }
}
