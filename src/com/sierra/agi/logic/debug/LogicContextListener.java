/**
 * LogicContextListener.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

public interface LogicContextListener {
    void logicBreakpointReached(LogicContextEvent ev);

    void logicResumed(LogicContextEvent ev);

    void variableChanged(LogicContextEvent ev);

    void flagChanged(LogicContextEvent ev);
}
