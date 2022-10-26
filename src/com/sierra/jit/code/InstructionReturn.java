/**
 * InstructionLoad.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.DataOutputStream;
import java.io.IOException;

public class InstructionReturn extends Instruction {
    protected String returnValue;

    public InstructionReturn(String returnValue) {
        this.returnValue = returnValue;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        switch (returnValue.charAt(0)) {
            case '[':
            case 'L':
                outs.write(0xb0);
                break;

            case 'V':
                outs.write(0xb1);
                break;

            case 'D':
                outs.write(0xaf);

            case 'F':
                outs.write(0xae);

            case 'J':
                outs.write(0xad);
                break;

            default:
                outs.write(0xac);
                break;
        }
    }

    public int getSize(CompileContext context, Scope scope, int pc) {
        return 1;
    }

    public int getPopCount() {
        switch (returnValue.charAt(0)) {
            case 'V':
                return 0;

            case 'D':
            case 'J':
                return 2;

            default:
                return 1;
        }
    }

    public int getPushCount() {
        return 0;
    }
}