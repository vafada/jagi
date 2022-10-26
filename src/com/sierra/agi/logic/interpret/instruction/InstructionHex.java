package com.sierra.agi.logic.interpret.instruction;

import java.io.IOException;
import java.io.InputStream;

public abstract class InstructionHex extends Instruction {
    /**
     * Bytecode
     */
    protected short bytecode;

    /**
     * Parameter #1
     */
    protected short p1;

    /**
     * Parameter #2
     */
    protected short p2;

    /**
     * Parameter #3
     */
    protected short p3;

    /**
     * Parameter #4
     */
    protected short p4;

    /**
     * Parameter #5
     */
    protected short p5;

    /**
     * Parameter #6
     */
    protected short p6;

    /**
     * Creates new Instruction
     */
    protected InstructionHex(InputStream stream, short bytecode) throws IOException {
        this.bytecode = bytecode;
        this.p1 = (short) stream.read();
        this.p2 = (short) stream.read();
        this.p3 = (short) stream.read();
        this.p4 = (short) stream.read();
        this.p5 = (short) stream.read();
        this.p6 = (short) stream.read();
    }

    /**
     * Determine Instruction Size
     */
    public int getSize() {
        return 7;
    }
}
