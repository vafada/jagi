package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantField extends Constant {
    protected int classIndex;
    protected int typeIndex;

    public ConstantField(int classIndex, int typeIndex) {
        this.classIndex = classIndex;
        this.typeIndex = typeIndex;
    }

    public boolean equals(Object o) {
        if (o instanceof ConstantField c) {

            return (c.classIndex == classIndex) && (c.typeIndex == typeIndex);
        }

        return false;
    }

    public int hashCode() {
        return typeIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        outs.write(9);
        outs.writeShort((short) classIndex);
        outs.writeShort((short) typeIndex);
    }
}
