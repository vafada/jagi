/**
 * LogicStackEntry.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

public class LogicStackEntry {
    protected static final int RUNNING = 0;
    protected static final int STEP_OVER = 1;
    protected static final int STEP_INTO = 2;
    protected static final int STEP_OUT = 3;
    protected static final int PAUSED = 10;
    /** Logic Being Debugged */
    public LogicDebug logic;
    /** Logic Number */
    public short logicNumber;
    /** Instruction Number */
    public int in;
    /** Status */
    public int command;

    public LogicStackEntry(LogicDebug logic, short logicNumber) {
        this.logic = logic;
        this.logicNumber = logicNumber;
        this.command = RUNNING;
    }

    public String toString() {
        if (logic == null) {
            return "<interpreter>";
        }

        return "Logic " + logicNumber;
    }
}
