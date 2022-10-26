/**
 * Instruction.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.InputStream;

/**
 * Base Class for all Logic's Instructions.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class Instruction {
    /**
     * The address of the Instruction.
     */
    protected int address;

    /**
     * Creates a new Instruction Does absolutely nothing in this class, it is
     * included as a formal declaration.
     */
    protected Instruction() {
    }

    /**
     * Creates a new Instruction. Does absolutely nothing in this class, it is
     * included as a formal declaration.
     *
     * @param context   Game context where this instance of the instruction will be used.
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction.
     * @param bytecode  Bytecode of the current instruction.
     */
    protected Instruction(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public abstract int execute(Logic logic, LogicContext logicContext) throws Exception;

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the instruction.
     */
    public abstract String[] getNames();

    public String toString() {
        String[] names = getNames();
        StringBuffer buff = new StringBuffer(32);

        buff.append(names[0]);

        buff.append("(");
        if (names.length > 1) {
            int i;

            for (i = 1; i < names.length; i++) {
                if (i != 1) {
                    buff.append(",");
                }

                buff.append(names[i]);
            }
        }
        buff.append(")");

        return buff.toString();
    }
//#endif DEBUG

    /**
     * Determine Instruction Size. In this class, it always return 1. (It is the
     * size of a instruction that has no parameter.)
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return 1;
    }

    /**
     * Gets the address of the Instruction within the Logic.
     *
     * @return The address of the Instruction within the Logic.
     */
    public int getAddress() {
        return address;
    }

    /**
     * Sets the address of the Instruction within the Logic.
     *
     * @param address The address of the Instruction within the Logic.
     */
    public void setAddress(int address) {
        this.address = address;
    }

    /**
     * Gets the address of the Instruction that immediately follows this Instruction.
     *
     * @return The address of the Instruction that follows this Instruction.
     */
    public int getNextInstructionAddress() {
        return this.address + this.getSize();
    }
}