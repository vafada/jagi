/*
 * InstructionFollowEgo.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Follow Ego Instruction.
 *
 * <P><CODE><B>follow.ego</B> Instruction 0x53</CODE><BR>
 * Object <CODE>o[p1]</CODE> is told to chase object 0 (Ego) by <CODE>p2</CODE>
 * pixels every step. When Ego and object coordinates become equal,
 * <CODE>f[p3]</CODE> is set to <CODE>true</CODE>.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionFollowEgo extends InstructionTri {
    /**
     * Creates new Follow Ego Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionFollowEgo(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Execute the Follow Ego Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        logicContext.getViewTable().followEgo(p1, p2, p3);
        return 4;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"follow.ego", "o" + p1, Integer.toString(p2), "f" + p3};
    }
//#endif DEBUG
}