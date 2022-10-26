/*
 *  InstructionJumpSubRoutine.java
 *  Just-in-Time Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.DataOutputStream;
import java.io.IOException;

public class InstructionJumpSubRoutine extends Instruction {
    protected Scope target;

    public InstructionJumpSubRoutine(Scope target) {
        this.target = target;
    }

    public void compile(CompileContext context, Scope scope, DataOutputStream outs, int pc) throws IOException {
        int target = context.getScopeBegin(this.target);

        if (target >= pc) {
            target -= pc;
        } else {
            target = -(pc - target);
        }

        outs.write(0xa8);
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
