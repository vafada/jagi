/*
 * InstructionWander.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wander Instruction.
 *
 * <P><CODE><B>wander</B> Instruction 0x54</CODE><BR>
 * Object <CODE>o[p1]</CODE> randomly changes the direction of its motion
 * (wanders). If <CODE>p1 == 0</CODE> (Ego), <CODE>program.control</CODE> is issued automatically.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see com.sierra.agi.logic.interpret.instruction.InstructionProgramControl
 */
public class InstructionWander extends InstructionUni {
    /**
     * Creates new Wander Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionWander(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getViewTable().wanderObject(p1);
        return 2;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"wander", "o" + p1};
    }
//#endif DEBUG
}