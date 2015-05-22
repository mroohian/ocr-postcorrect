package de.iisys.ocr.utils;

/**
 * TextUtils
 * de.iisys.ocr.utils
 * Created by reza on 04.08.14.
 */
public class TextUtils {
    private TextUtils() { }

    public static String formatColumnString(String input, int length) {
        int range = length - input.length();
        range = range < 0 ? 0 : range;
        return input + ("                                                                             ").substring(0, range) +"\t";
    }
}
