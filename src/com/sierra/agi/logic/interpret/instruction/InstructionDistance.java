/*
 * InstructionDistance.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Distance Instruction.
 *
 * <P><CODE><B>distance</B> Instruction 0x45</CODE><BR>
 * If both objects <CODE>o[p1]</CODE> and <CODE>o[p2]</CODE> are on the screen,
 * then <CODE>v[p3] = abs(o[p1].x - o[p2].x) + abs(o[p1].y - o[p2].y)</CODE>,
 * otherwise <CODE>v[p3] = 255</CODE>.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionDistance extends InstructionTri {
    /**
     * Creates new Distance Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionDistance(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.setVar(p3, logicContext.getViewTable().distance(p1, p2));
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
        return new String[]{"distance", "o" + p1, "o" + p2, "v" + p3};
    }
//#endif DEBUG
}