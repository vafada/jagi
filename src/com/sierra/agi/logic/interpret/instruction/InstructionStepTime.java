/*
 * InstructionStepTime.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Step Time Instruction.
 *
 * <P><CODE><B>step.time</B> Instruction 0x50</CODE><BR>
 * <CODE>v[p2] determines the speed of object <CODE>o[p1]</CODE> motion:
 * delay in the interpreter cycles between consecutive steps.
 * If <CODE>v[p2] = 1</CODE>, step occurs on every cycle.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionStepTime extends InstructionBi {
    /**
     * Creates new Step Time Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionStepTime(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getViewTable().setStepTime(p1, logicContext.getVar(p2));
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
        return new String[]{"step.time", "o" + p1, "v" + p2};
    }
//#endif DEBUG
}