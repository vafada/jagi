/**
 * LogicVariables.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

public class LogicVariables {
    public static final short EGO_OWNED = 0x00FF;
    public static final short DEFAULT_HORIZON = (short) 36;
    public static final short FLAG_EGO_WATER = (short) 0;
    public static final short FLAG_EGO_INVISIBLE = (short) 1;
    public static final short FLAG_ENTERED_COMMAND = (short) 2;
    public static final short FLAG_EGO_TOUCHED_ALERT = (short) 3;
    public static final short FLAG_SAID_ACCEPTED_INPUT = (short) 4;
    public static final short FLAG_NEW_ROOM_EXEC = (short) 5;
    public static final short FLAG_RESTART_GAME = (short) 6;
    public static final short FLAG_SCRIPT_BLOCKED = (short) 7;
    public static final short FLAG_JOYSTICK_SENSITIVITY = (short) 8;
    public static final short FLAG_SOUND_ON = (short) 9;
    public static final short FLAG_DEBUGGER_ON = (short) 10;
    public static final short FLAG_LOGIC_ZERO_FIRSTTIME = (short) 11;
    public static final short FLAG_RESTORE_JUST_RAN = (short) 12;
    public static final short FLAG_STATUS_SELECTS_ITEMS = (short) 13;
    public static final short FLAG_MENUS_WORK = (short) 14;
    public static final short FLAG_OUTPUT_MODE = (short) 15;

    /* ------------------------ System variables -------------------------- */
    public static final short VAR_CURRENT_ROOM = (short) 0;
    public static final short VAR_PREVIOUS_ROOM = (short) 1;
    public static final short VAR_EGO_EDGE = (short) 2;
    public static final short VAR_SCORE = (short) 3;
    public static final short VAR_BORDER_CODE = (short) 4;
    public static final short VAR_BORDER_TOUCHING = (short) 5;
    public static final short VAR_EGO_DIRECTION = (short) 6;
    public static final short VAR_MAX_SCORE = (short) 7;
    public static final short VAR_FREE_PAGES = (short) 8;
    public static final short VAR_WORD_NOT_FOUND = (short) 9;
    public static final short VAR_TIME_DELAY = (short) 10;
    public static final short VAR_SECONDS = (short) 11;
    public static final short VAR_MINUTES = (short) 12;
    public static final short VAR_HOURS = (short) 13;
    public static final short VAR_DAYS = (short) 14;
    public static final short VAR_JOYSTICK_SENSITIVITY = (short) 15;
    public static final short VAR_EGO_VIEW_RESOURCE = (short) 16;
    public static final short VAR_AGI_ERR_CODE = (short) 17;
    public static final short VAR_AGI_ERR_CODE_INFO = (short) 18;
    public static final short VAR_KEY = (short) 19;
    public static final short VAR_COMPUTER = (short) 20;
    public static final short VAR_WINDOW_RESET = (short) 21;
    public static final short VAR_SOUND_GENERATOR = (short) 22;
    public static final short VAR_VOLUME = (short) 23;
    public static final short VAR_MAX_INPUT_CHARS = (short) 24;
    public static final short VAR_SEL_ITEM = (short) 25;
    public static final short VAR_MONITOR = (short) 26;
    protected static final int MAX_CONTROLLERS = 256;
    protected static final int MAX_FLAGS = 256;
    protected static final int MAX_VARS = 256;
    protected static final int MAX_LOGICS = 256;
    protected static final int MAX_OBJECTS = 256;
    protected static final int MAX_STRINGS = 24;
    public static final int STRING_LENGTH = 40;
    public static final int GET_LINE_SIZE = 40;                                    /* maximum length for GetLine calls, used internally for things like save dialog */

    /* ------------------------ Screen edges ------------------------ */

    protected static final int TOP = 1;

    protected static final int RIGHT = 2;

    protected static final int BOTTOM = 3;

    protected static final int LEFT = 4;

    protected static final short MINX = 0;

    protected static final int MINY = 0;

    protected static final int MAXX = 159;

    protected static final short MAXY = 167;

    protected static final short HORIZON = 36;

    protected boolean[] controllers = new boolean[MAX_CONTROLLERS];
    protected boolean[] flags = new boolean[MAX_FLAGS];
    protected short[] vars = new short[MAX_VARS];
    protected String[] strings = new String[MAX_STRINGS];
    protected int[] scanStarts = new int[MAX_LOGICS];
    protected short[] objects = new short[MAX_OBJECTS];
    protected short horizon;
    protected String gameID;

    public final boolean getFlag(short flagNumber) {
        return flags[flagNumber];
    }

    public void setFlag(short flagNumber, boolean value) {
        flags[flagNumber] = value;
    }

    public boolean toggleFlag(short flagNumber) {
        return flags[flagNumber] = !flags[flagNumber];
    }

    public final short getVar(short varNumber) {
        return vars[varNumber];
    }

    public void setVar(short varNumber, short value) {
        vars[varNumber] = value;
    }

    public boolean getController(short controller) {
        boolean b = controllers[controller];

        if (b) {
            controllers[controller] = false;
        }

        return b;
    }

    public void setController(short controller) {
        controllers[controller] = true;
    }

    public final String getString(short strNumber) {
        return strings[strNumber];
    }

    public void setString(short strNumber, String str) {
        strings[strNumber] = str;
    }

    public void setScanStart(short logicNumber, int scanStart) {
        scanStarts[logicNumber] = scanStart;
    }

    public final int getScanStart(short logicNumber) {
        return scanStarts[logicNumber];
    }

    public final short getObject(short objectNumber) {
        return objects[objectNumber];
    }

    public void setObject(short objectNumber, short location) {
        objects[objectNumber] = location;
    }

    public final String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public final short getHorizon() {
        return this.horizon;
    }

    public void setHorizon(short horizon) {
        this.horizon = horizon;
    }
}
