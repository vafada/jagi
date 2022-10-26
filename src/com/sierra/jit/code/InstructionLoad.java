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

public class InstructionLoad extends Instruction {
    protected String name;

    public InstructionLoad(String name) {
        this.name = name;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        int n = context.getVariableNumber(scope, name);
        String t = context.getVariableType(scope, name);

        if (n >= 256) {
            outs.write(0xc4);
        }

        if (t == null) {
            System.out.println(name);
        }

        switch (t.charAt(0)) {
            default:
                if (n <= 3) {
                    outs.write(0x1a + n);
                    return;
                }

                outs.write(0x15);
                break;

            case 'J':
                if (n <= 3) {
                    outs.write(0x1e + n);
                    return;
                }

                outs.write(0x16);
                break;

            case 'F':
                if (n <= 3) {
                    outs.write(0x22 + n);
                    return;
                }

                outs.write(0x17);
                break;

            case 'D':
                if (n <= 3) {
                    outs.write(0x26 + n);
                    return;
                }

                outs.write(0x18);
                break;

            case '[':
            case 'L':
                if (n <= 3) {
                    outs.write(0x2a + n);
                    return;
                }

                outs.write(0x19);
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
        return 0;
    }

    public int getPushCount() {
        return 1;
    }
}
