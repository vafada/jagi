/*
 * InstructionGetNum.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Get Number Instruction.
 *
 * <P><CODE><B>get.num</B> Instruction 0x76</CODE><BR>
 * Enters a number from the keyboard into <CODE>v[p1]</CODE>. Message
 * <CODE>m[p2]</CODE> is used as the prompt.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGetNum extends InstructionBi {
    /**
     * Creates new Get Number Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGetNum(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        String message = logic.getMessage(p1);
        int answer = logicContext.getNum(message);
        logicContext.setVar(p2, (short) answer);
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
        return new String[]{"get.num", "v" + p1, "m" + p2};
    }
//#endif DEBUG
}