/**
 * LogicEvent.java
 * Adventure Game Interface Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

import java.util.EventObject;

public class LogicEvent extends EventObject {
    public static final int TYPE_ADDED = 0;
    public static final int TYPE_REMOVED = 1;
    public static final int TYPE_ENABLED = 2;
    public static final int TYPE_DISABLED = 3;
    protected int in;
    protected int type;

    public LogicEvent(LogicDebug source, int in, int type) {
        super(source);
        this.in = in;
        this.type = type;
    }

    public LogicDebug getLogic() {
        return (LogicDebug) source;
    }

    public int getInstructionNumber() {
        return in;
    }

    public int getType() {
        return type;
    }
}
