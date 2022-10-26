package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class SourceFileAttribute extends Attribute {
    protected int sourceIndex;

    public SourceFileAttribute(int nameIndex, int sourceIndex) {
        super(nameIndex);
        this.sourceIndex = sourceIndex;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public void compile(DataOutputStream outs) throws IOException {
        super.compile(outs);
        outs.writeShort(sourceIndex);
    }

    public int getSize() {
        return 2;
    }
}
