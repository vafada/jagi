/*
 *  InstructionConditionalGoto.java
 *  Adventure Game Interpreter JIT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.DataOutputStream;
import java.io.IOException;

public class InstructionConditionalGoto extends Instruction {
    // Single Parameters
    public static final byte CONDITION_IFEQ = (byte) 0x99;
    public static final byte CONDITION_IFNE = (byte) 0x9a;
    public static final byte CONDITION_IFLT = (byte) 0x9b;
    public static final byte CONDITION_IFGE = (byte) 0x9c;
    public static final byte CONDITION_IFGT = (byte) 0x9d;
    public static final byte CONDITION_IFLE = (byte) 0x9e;
    public static final byte CONDITION_IFNULL = (byte) 0xc6;
    public static final byte CONDITION_IFNOTNULL = (byte) 0xc7;
    // Dual Parameters
    public static final byte CONDITION_CMPEQ = (byte) 0x9f;
    public static final byte CONDITION_CMPNE = (byte) 0xa0;
    public static final byte CONDITION_CMPLT = (byte) 0xa1;
    public static final byte CONDITION_CMPGE = (byte) 0xa2;
    public static final byte CONDITION_CMPGT = (byte) 0xa3;
    public static final byte CONDITION_CMPLE = (byte) 0xa4;
    public static final byte CONDITION_CMPAEQ = (byte) 0xa5;
    public static final byte CONDITION_CMPANE = (byte) 0xa6;
    protected byte type;
    protected String name;

    public InstructionConditionalGoto(byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        int target = context.getLabelAddress(name);

        if (target >= pc) {
            target -= pc;
        } else {
            target = -(pc - target);
        }

        outs.write(type);
        outs.writeShort(target);
    }

    public int getSize(CompileContext context, Scope scope, int pc) {
        return 3;
    }

    public int getPopCount() {
        return 1;
    }

    public int getPushCount() {
        return 0;
    }
}
