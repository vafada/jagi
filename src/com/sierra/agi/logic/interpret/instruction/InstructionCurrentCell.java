/*
 * InstructionCurrentCell.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Current Cell instruction.
 *
 * <P><CODE><B>current.cel</B> Instruction 0x32</CODE><BR>
 * The number of the current cell of the View resource associated with the object
 * <CODE>p1</CODE> is stored in <CODE>v[p2]</CODE>.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionCurrentCell extends InstructionBi {
    /**
     * Creates new Current Cell Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     */
    public InstructionCurrentCell(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        logicContext.setVar(p2, logicContext.getViewTable().getCell(p1));
        return 3;
    }

//#ifdef DEBUG

    /**
     * Retreive the textual name of the instruction.
     *
     * @return Returns the textual name of the instruction.
     */
    public String[] getNames() {
        return new String[]{"current.cel", "o" + p1, "v" + p2};
    }
//#endif DEBUG
}