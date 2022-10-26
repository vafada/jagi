/*
 * InstructionUni.java
 */

package com.sierra.agi.logic.interpret.instruction;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base Class for all Logic's Instructions that has 1 parameter.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class InstructionUni extends Instruction {
    /**
     * Bytecode
     */
    protected short bytecode;

    /**
     * Parameter #1
     */
    protected short p1;

    /**
     * Creates a new Instruction. It store the bytecode and read the next byte
     * in the <CODE>stream</CODE> object.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    protected InstructionUni(InputStream stream, short bytecode) throws IOException {
        this.bytecode = bytecode;
        this.p1 = (short) stream.read();
    }

    /**
     * Determine Instruction Size. In this class, it always return 2. (It is the
     * size of a instruction that has 1 parameter.)
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return 2;
    }
}