/**
 * UnknownInstructionException.java
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
public class UnknownInstructionException extends LogicException {
    /**
     * Creates new <code>UnknownInstructionException</code> without detail
     * message.
     */
    public UnknownInstructionException() {
        super(null);
    }

    /**
     * Constructs an <code>UnknownInstructionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownInstructionException(String msg) {
        super(null, msg);
    }
}