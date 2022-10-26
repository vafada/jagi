/*
 *  ViewInvalidException.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class ViewInvalidException extends ViewException {
    /**
     * Creates new ViewInvalidException
     */
    public ViewInvalidException() {
    }

    /**
     * Constructs an <code>ViewInvalidException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ViewInvalidException(String msg) {
        super(msg);
    }
}