/*
 * InstructionStatus.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Status Instruction.
 *
 * <P><CODE><B>status</B> Instruction 0x7F</CODE><BR>
 * The screen is switched to text mode; the top line displays "You are carrying:",
 * then the names of the object with room field equal to <CODE>255</CODE> are listed. If
 * there are no such objects, the word "nothing" is displayed.
 * </P><P>
 * If <CODE>f[13]</CODE> is set (allow item selection), a highlight appears
 * which allows the player to select an item name. When ENTER is pressed, the
 * selected object number is stored in v25. When ESC is pressed, <CODE>255</CODE>
 * is stored in <CODE>v[25]</CODE>.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionStatus extends Instruction {
    /**
     * Creates new Status Instruction.
     *
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionStatus(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        logicContext.showInventoryScreen();
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
        return new String[]{"status"};
    }
//#endif DEBUG
}