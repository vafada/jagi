/**
 * LogicDebug.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

import com.sierra.agi.logic.InternalLogicException;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.LogicExitAll;
import com.sierra.agi.logic.interpret.LogicInterpreter;
import com.sierra.agi.logic.interpret.LogicResetScanStart;
import com.sierra.agi.logic.interpret.LogicReturn;
import com.sierra.agi.logic.interpret.LogicSetScanStart;
import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;

import java.util.BitSet;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Vector;

public class LogicDebug extends LogicInterpreter {
    protected Vector listeners = new Vector();
    protected BitSet breakpoints;
    protected BitSet breakpointsActivated;

    public LogicDebug(short logicNumber, Vector instructions, String[] messages) {
        super(logicNumber, instructions, messages);
        this.breakpoints = new BitSet(instructions.size());
        this.breakpointsActivated = new BitSet(instructions.size());
    }

    public void execute(LogicContext logicContext) throws LogicException {
        LogicContextDebug logicDebug = (LogicContextDebug) logicContext;
        int in = logicContext.getScanStart(logicNumber);
        int result = 0;
        Instruction instruction = null;
        LogicStackEntry stackEntry = new LogicStackEntry(this, logicNumber);
        LogicStackEntry previousStackEntry = null;

        try {
            previousStackEntry = (LogicStackEntry) logicDebug.peekLogic();

            if (previousStackEntry.command == LogicStackEntry.STEP_INTO) {
                stackEntry.command = LogicStackEntry.PAUSED;
            }
        } catch (EmptyStackException esex) {
        }

        try {
            logicContext.pushLogic(stackEntry);

            while (true) {
                if (breakpointsActivated.get(in) || logicDebug.isBreaked()) {
                    stackEntry.command = LogicStackEntry.PAUSED;
                }

                if (stackEntry.command == LogicStackEntry.PAUSED) {
                    stackEntry.in = in;
                    logicDebug.breakpointReached();

                    while (stackEntry.command == LogicStackEntry.PAUSED) {
                        try {
                            Thread.sleep(64);
                        } catch (InterruptedException iex) {
                        }
                    }
                }

                instruction = instructions[in];

                try {
                    result = instruction.execute(this, logicContext);

                    if ((instruction instanceof InstructionMoving) && (result != sizes[in])) {
                        in = ((InstructionMoving) instruction).getDestination(in, sizes);
                    } else {
                        in++;
                    }
                } catch (LogicSetScanStart ex) {
                    logicContext.setScanStart(logicNumber, in);
                    in++;
                } catch (LogicResetScanStart ex) {
                    logicContext.setScanStart(logicNumber, 0);
                    in++;
                }

                switch (stackEntry.command) {
                    case LogicStackEntry.STEP_OVER:
                    case LogicStackEntry.STEP_INTO:
                        stackEntry.command = LogicStackEntry.PAUSED;
                        break;
                }
            }
        } catch (LogicReturn lrex) {
            switch (stackEntry.command) {
                case LogicStackEntry.STEP_OVER:
                case LogicStackEntry.STEP_INTO:
                    logicDebug.breakExecution();
                    break;
            }

        } catch (LogicExitAll leaex) {
            switch (stackEntry.command) {
                case LogicStackEntry.STEP_OVER:
                case LogicStackEntry.STEP_INTO:
                    logicDebug.breakExecution();
                    break;
            }

            throw leaex;
        } catch (LogicException lex) {
            throw lex;
        } catch (Exception ex) {
            throw new InternalLogicException(logicContext, ex);
        } finally {
            logicContext.popLogic();

            if (stackEntry.command == LogicStackEntry.STEP_OUT) {
                try {
                    stackEntry = (LogicStackEntry) logicContext.peekLogic();
                    stackEntry.command = LogicStackEntry.PAUSED;
                } catch (EmptyStackException ex) {
                    logicDebug.breakExecution();
                }
            }
        }
    }

    public void addLogicListener(LogicListener listener) {
        listeners.add(listener);
    }

    public void removeLogicListener(LogicListener listener) {
        listeners.remove(listener);
    }

    public void addBreakpoint(int instructionNumber) {
        Enumeration en;
        LogicEvent event;

        if (!breakpoints.get(instructionNumber)) {
            breakpoints.set(instructionNumber);
            breakpointsActivated.set(instructionNumber);

            en = listeners.elements();
            event = new LogicEvent(this, instructionNumber, LogicEvent.TYPE_ADDED);

            while (en.hasMoreElements()) {
                ((LogicListener) en.nextElement()).logicBreakpointAdded(event);
            }
        }
    }

    public void removeBreakpoint(int instructionNumber) {
        Enumeration en;
        LogicEvent event;

        if (breakpoints.get(instructionNumber)) {
            breakpoints.clear(instructionNumber);
            breakpointsActivated.clear(instructionNumber);

            en = listeners.elements();
            event = new LogicEvent(this, instructionNumber, LogicEvent.TYPE_REMOVED);

            while (en.hasMoreElements()) {
                ((LogicListener) en.nextElement()).logicBreakpointRemoved(event);
            }
        }
    }

    public void enableBreakpoint(int instructionNumber) {
        Enumeration en;
        LogicEvent event;

        if (!breakpointsActivated.get(instructionNumber)) {
            breakpoints.set(instructionNumber);
            breakpointsActivated.set(instructionNumber);

            en = listeners.elements();
            event = new LogicEvent(this, instructionNumber, LogicEvent.TYPE_REMOVED);

            while (en.hasMoreElements()) {
                ((LogicListener) en.nextElement()).logicBreakpointRemoved(event);
            }
        }
    }

    public void disableBreakpoint(int instructionNumber) {
        Enumeration en;
        LogicEvent event;

        if (breakpointsActivated.get(instructionNumber)) {
            breakpointsActivated.clear(instructionNumber);

            en = listeners.elements();
            event = new LogicEvent(this, instructionNumber, LogicEvent.TYPE_REMOVED);

            while (en.hasMoreElements()) {
                ((LogicListener) en.nextElement()).logicBreakpointRemoved(event);
            }
        }
    }

    public void disableBreakpoint(int instructionNumber, boolean force) {
        Enumeration en;
        LogicEvent event;

        if (force) {
            breakpoints.set(instructionNumber);
        }

        if (breakpointsActivated.get(instructionNumber)) {
            breakpointsActivated.clear(instructionNumber);

            en = listeners.elements();
            event = new LogicEvent(this, instructionNumber, LogicEvent.TYPE_REMOVED);

            while (en.hasMoreElements()) {
                ((LogicListener) en.nextElement()).logicBreakpointRemoved(event);
            }
        }
    }

    public BitSet getBreakpoints() {
        return breakpoints;
    }

    public BitSet getBreakpointsActivated() {
        return breakpointsActivated;
    }
}
