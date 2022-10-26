package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantString extends Constant {
    protected int stringIndex;

    public ConstantString(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public boolean equals(Object o) {
        if (o instanceof ConstantString) {
            return ((ConstantString) o).stringIndex == stringIndex;
        }

        return false;
    }

    public int hashCode() {
        return stringIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        outs.write(8);
        outs.writeShort((short) stringIndex);
    }
}
