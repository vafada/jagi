/**
 * InstructionGoto.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.DataOutputStream;
import java.io.IOException;

public class InstructionGoto extends Instruction {
    protected String name;
    protected Scope scope;
    protected boolean begin;

    public InstructionGoto(String name) {
        this.name = name;
    }

    public InstructionGoto(Scope scope, boolean begin) {
        this.scope = scope;
        this.begin = begin;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        int target;

        if (name != null) {
            target = context.getLabelAddress(name);
        } else {
            if (begin) {
                target = context.getScopeBegin(scope);
            } else {
                target = context.getScopeEnd(scope);
            }
        }

        if (target >= pc) {
            target -= pc;
        } else {
            target = -(pc - target);
        }

        outs.write(0xa7);
        outs.writeShort(target);
    }

    public int getSize(CompileContext context, Scope scope, int pc) {
        return 3;
    }

    public int getPopCount() {
        return 0;
    }

    public int getPushCount() {
        return 0;
    }
}