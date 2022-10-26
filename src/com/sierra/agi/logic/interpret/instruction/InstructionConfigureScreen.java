/*
 * InstructionConfigureScreen.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configure Screen Instruction.
 *
 * <P><CODE><B>configure.screen</B> Instruction 0x6f</CODE><BR>
 * Sets position of lines on the screen, where <CODE>p1</CODE> = 1 (the minimum
 * line number for print), <CODE>p2</CODE> is the user input line and
 * <CODE>p3</CODE> is the status line.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionConfigureScreen extends InstructionTri {
    /**
     * Creates new Configure Screen Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionConfigureScreen(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getViewScreen().configure(p1, p2, p3);
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
        return new String[]{"configure.screen", Integer.toString(p1), Integer.toString(p2), Integer.toString(p3)};
    }
//#endif DEBUG
}