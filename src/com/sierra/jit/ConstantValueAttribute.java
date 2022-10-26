package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConstantValueAttribute extends Attribute {
    protected int constantIndex;

    public ConstantValueAttribute(int nameIndex, int constantIndex) {
        super(nameIndex);
        this.constantIndex = constantIndex;
    }

    public int getConstantIndex() {
        return constantIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        super.compile(outs);
        outs.writeShort(constantIndex);
    }

    public int getSize() {
        return 2;
    }
}
