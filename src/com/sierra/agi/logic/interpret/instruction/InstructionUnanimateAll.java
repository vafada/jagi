/*
 * InstructionUnanimateAll.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.InputStream;

/**
 * Unanimate All instruction.
 *
 * <P>All objects are removed from the control list and are considered inexistent.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionUnanimateAll extends Instruction {
    /**
     * Bytecode
     */
    protected short bytecode;

    /**
     * Creates new Unanimate All Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format. (ignored)
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     */
    public InstructionUnanimateAll(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) {
        this.bytecode = bytecode;
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        logicContext.getViewTable().unanimateAll();
        return 1;
    }

//#ifdef DEBUG

    /**
     * Retreive the textual name of the instruction.
     *
     * @return Returns the textual name of the instruction.
     */
    public String[] getNames() {
        return new String[]{"unanimate.all"};
    }
//#endif DEBUG
}