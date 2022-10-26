/**
 * InternalLogicException.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InternalLogicException extends LogicException {
    protected Throwable throwable;

    /**
     * Creates new <code>InternalLogicException</code> without detail message.
     */
    public InternalLogicException(LogicContext logicContext, Throwable throwable) {
        super(logicContext);
        this.throwable = throwable;
    }

    /**
     * Constructs an <code>InternalLogicException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InternalLogicException(LogicContext logicContext, String msg, Throwable throwable) {
        super(logicContext, msg);
        this.throwable = throwable;
    }

    public Throwable getExceptionTarget() {
        return throwable;
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        s.print(getClass().getName());

        if (getMessage() == null) {
            s.println();
        } else {
            s.print(": ");
            s.println(getMessage());
        }

        throwable.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        s.print(getClass().getName());

        if (getMessage() == null) {
            s.println();
        } else {
            s.print(": ");
            s.print(getMessage());
        }

        throwable.printStackTrace(s);
    }
}