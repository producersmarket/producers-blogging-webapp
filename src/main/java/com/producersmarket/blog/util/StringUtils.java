package com.producersmarket.blog.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import java.util.regex.Pattern;

public class StringUtils {

    private static final Random RANDOM = new Random();
    static long current = System.currentTimeMillis();

    public static final int PAD_LEFT = -1;
    public static final int PAD_BOTH = 0;
    public static final int PAD_RIGHT = 1;

    /**
     * Pad the given string with the given pad value to the given length in the
     * given direction. Valid directions are StringX.PAD_LEFT, StringX.PAD_BOTH
     * and StringX.PAD_RIGHT. When using StringX.PAD_BOTH, padding left has
     * precedence over padding right when difference between string's length
     * and the given length is odd.
     * @param string The string to be padded.
     * @param pad The value to pad the given string with.
     * @param length The length to pad the given string to.
     * @param direction The direction to pad the given string to.
     * @return The padded string.
     * @throws IllegalArgumentException When invalid direction is given.
     */
    public static String pad(String string, String pad, int length, int direction) throws IllegalArgumentException {
        switch (direction) {
            case PAD_LEFT :
                while (string.length() < length) {
                    string = pad + string;
                }
                break;

            case PAD_RIGHT :
                while (string.length() < length) {
                    string += pad;
                }
                break;

            case PAD_BOTH :
                int right = (length - string.length()) / 2 + string.length();
                string = pad(string, pad, right, PAD_RIGHT);
                string = pad(string, pad, length, PAD_LEFT);
                break;

            default :
                throw new IllegalArgumentException(
                    "Invalid direction, must be one of "
                        + "StringX.PAD_LEFT, StringX.PAD_BOTH or StringX.PAD_RIGHT.");
        }
        return string;
    }

    public static String padLeft(String string, int length, char filler) {
        return padLeftSideOfString(string, length, filler);
    }

    public static String padLeftSideOfString(String string, int length, char filler) {

        if(string.length() >= length) {
            return string;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(length - string.length());
        for(int i = 0; i < length - string.length(); i++) {
            sb.append(filler);
        }
        sb.append(string);
        return sb.toString();
    }

    public static String padBothSidesOfString(String string, int length, char filler) {

        if(string.length() >= length) {
            return string;
        }

        int halfLengthOfString  = 0;
        int fillerLength = length - string.length();
        int leftFillerLength = 0;
        int rightFillerLength = 0;

        if(fillerLength % 2 == 0) {
            leftFillerLength = fillerLength/2;
            rightFillerLength = fillerLength/2;
        } else {
            leftFillerLength = fillerLength/2;
            rightFillerLength = (fillerLength/2) + 1;
        }

        return generateString(filler, leftFillerLength) + string + generateString(filler, rightFillerLength);
    }

    /**
     * Trim the given string with the given trim value.
     * @param string The string to be trimmed.
     * @param trim The value to trim the given string off.
     * @return The trimmed string.
     */
    public static String trim(String string, String trim) {
        if (trim.length() == 0) {
            return string;
        }

        int start = 0;
        int end = string.length();

        while (start < end && string.substring(start, start + trim.length()).equals(trim)) {
            start += trim.length();
        }
        while (start < end && string.substring(end - trim.length(), end).equals(trim)) {
            end -= trim.length();
        }

        return string.substring(start, end);
    }

    public static String trimEnd(String value, char[] trimChars) {
        return trimEnd(value, new String(trimChars));
    }
    public static String trimEnd(String value, String trimChars) {
        String trimString = Pattern.quote(trimChars);
        return value.replaceAll("["+trimString+"]*$", "");
    }
    public static String trimStart(String value, char[] trimChars) {
        return trimStart(value, new String(trimChars));
    }
    public static String trimStart(String value, String trimChars) {
        String trimString = Pattern.quote(trimChars);
        return value.replaceAll("^["+trimString+"]*", "");
    }
    public static String trimEnds(String value, String trimChars) {
        String s = trimStart(value, trimChars);
        s = trimEnd(value, trimChars);
        return s;
    }


    /**
     * Join the given collection with the given join value.
     * @param collection The collection (List, Set) to be joined.
     * @param join The value to be joined between each part.
     * @return The joined collection.
     */
    public static String join(Collection collection, String join) {
        StringBuilder sb = new StringBuilder();
        for(Iterator iter = collection.iterator(); iter.hasNext();) {
            sb.append(iter.next());
            if(iter.hasNext()) sb.append(join);
        }
        return sb.toString();
    }

    /**
     * Join the given collection with the given join value.
     * @param collection The collection (List, Set) to be joined.
     * @param join The value to be joined between each part.
     * @return The joined collection.
     */
    public static String join(Map map, String join, String separator) {

        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> set = map.entrySet();
        int x = 0;
        for(Map.Entry<String, String> entry : set) {
            if(x++ > 0) sb.append(separator);
            sb.append(entry.getKey()).append(join).append(entry.getValue());
        }

        return sb.toString();
    }

    public static String join(String[] s, String glue) {
        int len = s.length;
        if(len == 0) return null;
        StringBuilder sb = new StringBuilder();
        //sb.append(s[0]); for(int i = 1; i < len; ++i) out.append(glue).append(s[i]);
        for(int i = 0; i < len; i++) {
            if(i > 0) sb.append(glue);
            sb.append(s[i]);
        }
        return sb.toString();
    }

    /**
     * Check if given string is numeric. Dot separators are allowed.
     * @param string The string to check on.
     * @return True if string is numeric. False if not.
     */
    public static boolean isNumeric(String string) {
        return string.matches("^[-+]?\\d+(\\.\\d+)?$");
    }

    /**
     * Check if given string contains numbers.
     * @param string The string to check on.
     * @return True if string contains numbers. False if not.
     */
    public static boolean hasNumbers(String string) {
        return string.matches("^.*\\d.*$");
    }

    /**
     * Check if given string is a valid email address.
     * This confirms the RFC822 & RFC1035 specifications.
     * @param string The string to check on.
     * @return True if string is an valid email address. False if not.
     */
    public static boolean isEmailAddress(String string) {
        return string.toLowerCase().matches("^[a-z0-9-~#&\\_]+(\\.[a-z0-9-~#&\\_]+)*@([a-z0-9-]+\\.)+[a-z]{2,5}$");
    }

    public static String generateString(char filler, int length) {

        StringBuilder sb = new StringBuilder();
        sb.append(length);
        for(int i = 0; i < length; i++) {
            sb.append(filler);
        }
        return sb.toString();

    }

}


