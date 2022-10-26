/*
 * InstructionSetLoop.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Set Loop Instruction.
 *
 * <P><CODE><B>set.loop.n</B> Instruction 0x2b</CODE><BR>
 * Chooses the loop <CODE>p2</CODE> in the VIEW resource associated with the
 * object <CODE>p1</CODE>.</P>
 *
 * <P><CODE><B>set.loop.v</B> Instruction 0x2c</CODE><BR>
 * Chooses the loop <CODE>v[p2]</CODE> in the VIEW resource associated with the
 * object <CODE>p1</CODE>.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSetLoopV extends InstructionBi {
    /**
     * Creates new Set Loop Instruction (V).
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSetLoopV(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short p = logicContext.getVar(p2);
        logicContext.getViewTable().setLoop(p1, p);
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
        String[] names = new String[3];

        names[0] = "set.loop.v";
        names[1] = "o" + p1;
        names[2] = "v" + p2;

        return names;
    }
//#endif DEBUG
}