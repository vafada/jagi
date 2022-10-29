package com.sierra.agi.save;

public class SaveUtils {
    private SaveUtils() {}

    public static char POINTER_CHAR = (char)26;
    public static int NUM_GAMES = 12;
    public static int[] convertToUnsignedInt(byte[] b) {
        int[] converted = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            converted[i] = b[i] & 0xFF;
        }
        return converted;
    }
}
