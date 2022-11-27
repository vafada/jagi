package com.sierra.agi.save;

import static com.sierra.agi.logic.LogicVariables.MAX_STRINGS;

public class SaveUtils {
    private SaveUtils() {}

    public static char[] POINTER_CHAR = {26};

    public static int[] convertToUnsignedInt(byte[] b) {
        int[] converted = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            converted[i] = b[i] & 0xFF;
        }
        return converted;
    }

    public static int getNumberOfControllers(String version) {
        switch (version) {
            case "2.089":
            case "2.272":
            case "2.277":
                return 40;
            // Most versions have a max of 50 controllers, as defined in the Defines constant.
            default:
                return 50;
        }
    }

    public static int getNumberOfStrings(String version) {
        switch (version) {
            case "2.089":
            case "2.272":
            case "2.277":
            case "3.002.149":
                return 12;
            // Most versions have 24 strings, as defined in the Defines constant.
            default:
                return MAX_STRINGS;
        }
    }
}
