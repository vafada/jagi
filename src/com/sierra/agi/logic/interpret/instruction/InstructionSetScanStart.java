/*
 * InstructionSetScanStart.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.LogicResetScanStart;
import com.sierra.agi.logic.interpret.LogicSetScanStart;

import java.io.InputStream;

/**
 * Set Scan Start Intruction.
 *
 * <P>Normally, when a logic is called using call command, execution begins at
 * the first instruction. <CODE>set.scan.start</CODE> command sets the entry
 * point at the command following it, while <CODE>reset.scan.start</CODE>
 * returns the entry point to the beginning.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSetScanStart extends Instruction {
    /**
     * Bytecode
     */
    protected short bytecode;

    /**
     * Creates a new Set Scan Start Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     */
    public InstructionSetScanStart(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) {
        this.bytecode = bytecode;
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Never Returns.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        if (bytecode == 0x91) {
            throw new LogicSetScanStart();
        } else {
            throw new LogicResetScanStart();
        }
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[1];

        switch (bytecode) {
            default:
            case 0x91:
                names[0] = "set.scan.start";
                break;
            case 0x92:
                names[0] = "reset.scan.start";
                break;
        }

        return names;
    }
//#endif DEBUG
}