/*
 * InstructionShowObject.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Show Object Instruction.
 *
 * <P><CODE><B>show.object</B> Instruction 0x81</CODE><BR>
 * Show cel 0 of loop 0 of the VIEW resource <CODE>p1</CODE> in the bottom
 * center of the screen. In the center of the screen, a message associated\
 * with the VIEW resource is printed.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionShowObject extends InstructionUni {
    /**
     * Creates new Show Object Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionShowObject(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        try {
            logicContext.getViewTable().showInventoryObject(p1);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return new String[]{"show.object", Integer.toString(p1)};
    }
//#endif DEBUG
}