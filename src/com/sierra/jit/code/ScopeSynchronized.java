/**
 * ScopeArgument.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import com.sierra.jit.ConstantPool;

public class ScopeSynchronized extends Scope {
    protected Scope leave;

    public ScopeSynchronized(Scope parent, ConstantPool constants) {
        super(parent, constants);
        variables.put("<sync>", "Ljava/lang/Object;");
        add(getDuplicate());
        add(new InstructionStore("<sync>"));
        add(getMonitorEnter());

        content.add(new InstructionLoad("<sync>"));
        content.add(getMonitorLeave());
        content.add(new InstructionGoto(this, false));

        leave = new Scope(this, constants);
        leave.add(new InstructionDummy(0, 1));
        leave.add(new InstructionLoad("<sync>"));
        leave.add(getMonitorLeave());
        leave.add(getThrow());

        content.add(leave);
    }

    public void addLeaveScope() {
        add(new InstructionLoad("<sync>"));
        add(getMonitorLeave());
    }
}
