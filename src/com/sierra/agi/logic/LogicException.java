/**
 * LogicException.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

/**
 * Base class for Logic Exceptions.
 *
 * @author Dr. Z
 * @version 0.00.01.00
 */
public class LogicException extends Exception {
    protected LogicContext logicContext;

    /**
     * Creates new <code>LogicException</code> without detail message.
     */
    public LogicException(LogicContext logicContext) {
        super();

        if (logicContext != null) {
            this.logicContext = new LogicContext(logicContext);
        }
    }

    /**
     * Constructs an <code>LogicException</code> with the specified detail message.
     *
     * @param msg Detail message.
     */
    public LogicException(LogicContext logicContext, String msg) {
        super(msg);

        if (logicContext != null) {
            this.logicContext = new LogicContext(logicContext);
        }
    }

    public LogicContext getLogicContext() {
        return logicContext;
    }
}
