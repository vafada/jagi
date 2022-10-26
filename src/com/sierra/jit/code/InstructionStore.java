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

public class InstructionStore extends Instruction {
    protected String name;

    public InstructionStore(String name) {
        this.name = name;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        int n = context.getVariableNumber(scope, name);

        if (n >= 256) {
            outs.write(0xc4);
        }

        switch (context.getVariableType(scope, name).charAt(0)) {
            default:
                if (n <= 3) {
                    outs.write(0x3b + n);
                    return;
                }

                outs.write(0x36);
                break;

            case 'J':
                if (n <= 3) {
                    outs.write(0x3f + n);
                    return;
                }

                outs.write(0x37);
                break;

            case 'F':
                if (n <= 3) {
                    outs.write(0x43 + n);
                    return;
                }

                outs.write(0x38);
                break;

            case 'D':
                if (n <= 3) {
                    outs.write(0x47 + n);
                    return;
                }

                outs.write(0x39);
                break;

            case '[':
            case 'L':
                if (n <= 3) {
                    outs.write(0x4b + n);
                    return;
                }

                outs.write(0x3a);
                break;
        }

        if (n >= 256) {
            outs.writeShort(n);
        } else {
            outs.write(n);
        }
    }

    public int getSize(CompileContext context, Scope scope, int pc) {
        int varNumber = context.getVariableNumber(scope, name);

        if (varNumber >= 256) {
            return 4;
        } else if (varNumber <= 3) {
            return 1;
        } else {
            return 2;
        }
    }

    public int getPopCount() {
        return 1;
    }

    public int getPushCount() {
        return 0;
    }
}