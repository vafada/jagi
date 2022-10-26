/**
 * LogicListener.java
 * Adventure Game Interface Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

public interface LogicListener {
    void logicBreakpointAdded(LogicEvent ev);

    void logicBreakpointRemoved(LogicEvent ev);

    void logicBreakpointEnabled(LogicEvent ev);

    void logicBreakpointDisabled(LogicEvent ev);
}
