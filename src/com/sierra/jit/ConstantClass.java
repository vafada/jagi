package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantClass extends Constant {
    protected int classIndex;

    public ConstantClass(int classIndex) {
        this.classIndex = classIndex;
    }

    public boolean equals(Object o) {
        if (o instanceof ConstantClass) {
            return ((ConstantClass) o).classIndex == classIndex;
        }

        return false;
    }

    public int hashCode() {
        return classIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        outs.write(7);
        outs.writeShort(classIndex);
    }
}
