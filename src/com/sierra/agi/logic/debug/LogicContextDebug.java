/**
 * LogicContextDebug.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.res.ResourceCache;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public final class LogicContextDebug extends LogicContext {
    private boolean breaked = false;
    private final List<LogicContextListener> listeners = new ArrayList<>();

    public LogicContextDebug(ResourceCache cache) {
        super(cache);
    }

    public void breakpointReached() {
        LogicContextEvent event = new LogicContextEvent(this);

        breaked = true;

        for (LogicContextListener listener : listeners) {
            listener.logicBreakpointReached(event);
        }
    }

    private synchronized boolean ensureExecution() {
        if (!isRunning()) {
            Thread thread;

            thread = new Thread(this);
            thread.start();


            LogicContextEvent event = new LogicContextEvent(this);

            for (LogicContextListener listener : listeners) {
                listener.logicResumed(event);
            }

            return true;
        }

        return false;
    }

    public void breakExecution() {
        ensureExecution();
        breaked = true;
    }

    public boolean isBreaked() {
        return breaked;
    }

    public void resumeExecution() {
        breaked = false;

        if (!ensureExecution()) {
            LogicContextEvent event = new LogicContextEvent(this);

            ((LogicStackEntry) peekLogic()).command = LogicStackEntry.RUNNING;

            for (LogicContextListener listener : listeners) {
                listener.logicResumed(event);
            }
        }
    }

    public void stepIntoExecution() {
        ensureExecution();
        breaked = false;

        try {
            ((LogicStackEntry) peekLogic()).command = LogicStackEntry.STEP_INTO;
        } catch (EmptyStackException esex) {
        }
    }

    public void stepOverExecution() {
        ensureExecution();
        breaked = false;

        try {
            ((LogicStackEntry) peekLogic()).command = LogicStackEntry.STEP_OVER;
        } catch (EmptyStackException esex) {
        }
    }

    public void stepOutExecution() {
        ensureExecution();
        breaked = false;

        try {
            ((LogicStackEntry) peekLogic()).command = LogicStackEntry.STEP_OUT;
        } catch (EmptyStackException esex) {
        }
    }

    public void addLogicContextListener(LogicContextListener listener) {
        listeners.add(listener);
    }

    public void removeLogicContextListener(LogicContextListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setFlag(short flagNumber, boolean value) {
        super.setFlag(flagNumber, value);
        LogicContextEvent event = new LogicContextEvent(this);
        event.setFlagNumber(flagNumber);

        for (LogicContextListener listener : listeners) {
            listener.flagChanged(event);
        }
    }

    @Override
    public boolean toggleFlag(short flagNumber) {
        boolean retVal = super.toggleFlag(flagNumber);
        LogicContextEvent event = new LogicContextEvent(this);
        event.setFlagNumber(flagNumber);

        for (LogicContextListener listener : listeners) {
            listener.flagChanged(event);
        }

        return retVal;
    }

    @Override
    public void setVar(short varNumber, short value) {
        super.setVar(varNumber, value);

        LogicContextEvent event = new LogicContextEvent(this);
        event.setVariableNumber(varNumber);

        for (LogicContextListener listener : listeners) {
            listener.variableChanged(event);
        }
    }

    @Override
    public void setObject(short objectNumber, short location) {
        super.setObject(objectNumber, location);

        LogicContextEvent event = new LogicContextEvent(this);
        event.setInventoryNumber(objectNumber);

        for (LogicContextListener listener : listeners) {
            listener.inventoryChanged(event);
        }
    }
}
