/*
 * InstructionQuad.java
 */

package com.sierra.agi.logic.interpret.instruction;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class InstructionQuad extends Instruction {
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
     * Creates new Instruction
     */
    protected InstructionQuad(InputStream stream, short bytecode) throws IOException {
        this.bytecode = bytecode;
        this.p1 = (short) stream.read();
        this.p2 = (short) stream.read();
        this.p3 = (short) stream.read();
        this.p4 = (short) stream.read();
    }

    /**
     * Determine Instruction Size
     */
    public int getSize() {
        return 5;
    }
}