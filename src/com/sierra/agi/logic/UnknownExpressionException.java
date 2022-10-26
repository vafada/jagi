/**
 * UnknownExpressionException.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class UnknownExpressionException extends LogicException {
    /**
     * Creates new <code>UnknownExpressionException</code> without detail
     * message.
     */
    public UnknownExpressionException() {
        super(null);
    }

    /**
     * Constructs an <code>UnknownExpressionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownExpressionException(String msg) {
        super(null, msg);
    }
}