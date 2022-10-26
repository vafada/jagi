/*
 * InstructionShowPic.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Show Picture Instruction.
 *
 * <P><CODE><B>show.pic</B> Instruction 0x1a</CODE><BR>
 * Shows internal buffer on the screen.
 * <B>ATTENTION</B>! Please use the following sequence of commands when loading
 * PICTURE resources in the interpreter memory:
 *
 * <HR>
 * <PRE>
 * load.pic(n);
 * draw.pic(n);
 * discard.pic(n);
 * ...
 * show.pic();
 * </PRE>
 * <HR>
 * <p>
 * Any other order may crash the interpreter without any diagnostic messages.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionShowPic extends Instruction {
    /**
     * Creates new Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionShowPic(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        logicContext.getViewTable().showPic();
        return 1;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"show.pic"};
    }
//#endif DEBUG
}