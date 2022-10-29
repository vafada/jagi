/*
 *  Box.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2002 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.logic.LogicContext;

import java.awt.event.KeyEvent;

public abstract class Box {
    protected int timeout;

    public Box() {
        timeout = -1;
    }

    public abstract KeyEvent show(LogicContext logicContext, ViewScreen viewScreen);

    public abstract int getLineCount();

    public abstract int getColumnCount();

    public abstract int getWidth();

    public abstract int getHeight();

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
