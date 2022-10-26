package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantMethod extends Constant {
    protected int classIndex;
    protected int typeIndex;

    public ConstantMethod(int classIndex, int typeIndex) {
        this.classIndex = classIndex;
        this.typeIndex = typeIndex;
    }

    public boolean equals(Object o) {
        if (o instanceof ConstantMethod c) {

            return (c.classIndex == classIndex) && (c.typeIndex == typeIndex);
        }

        return false;
    }

    public int hashCode() {
        return typeIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        outs.write(10);
        outs.writeShort(classIndex);
        outs.writeShort(typeIndex);
    }
}
