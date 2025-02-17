/**
 * LogicContextEvent.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

public class LogicContextEvent extends java.util.EventObject {
    private short variableNumber = -1;
    private short flagNumber = -1;
    private short inventoryNumber = -1;
    public LogicContextEvent(LogicContextDebug source) {
        super(source);
    }

    public void setVariableNumber(short variableNumber) {
        this.variableNumber = variableNumber;
    }

    public short getVariableNumber() {
        return variableNumber;
    }

    public short getFlagNumber() {
        return flagNumber;
    }

    public void setFlagNumber(short flagNumber) {
        this.flagNumber = flagNumber;
    }

    public short getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(short inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }
}
