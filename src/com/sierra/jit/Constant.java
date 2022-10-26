package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Constant {
    public abstract void compile(DataOutputStream outs) throws IOException;
}
