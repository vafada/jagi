/*
 * InstructionGetPriority.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Get Priority Instruction.
 *
 * <P><CODE><B>get.priority</B> Instruction 0x39</CODE><BR>
 * Get priority of the view of the object <CODE>p1</CODE> to <CODE>v[p2]</CODE>.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGetPriority extends InstructionBi {
    /**
     * Creates new Get Priority Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGetPriority(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.setVar(p2, logicContext.getViewTable().getPriority(p1));
        return 3;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"get.priority", "o" + p1, "v" + p2};
    }
//#endif DEBUG
}