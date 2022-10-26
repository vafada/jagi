/*
 *  InstructionPutStatic.java
 *  Adventure Game Interpreter JIT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.DataOutputStream;
import java.io.IOException;

public class InstructionPutStatic extends Instruction {
    protected int fieldNumber;
    protected String signature;

    public InstructionPutStatic(int fieldNumber, String signature) {
        this.fieldNumber = fieldNumber;
        this.signature = signature;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        outs.write(0xb2);
        outs.writeShort(fieldNumber);
    }

    public int getSize(CompileContext context, Scope scope, int pc) {
        return 3;
    }

    public int getPopCount() {
        return CompileContext.getTypeSize(signature);
    }

    public int getPushCount() {
        return 0;
    }
}
