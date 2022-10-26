/*
 * InstructionSetTextAttributes.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.ViewScreen;

import java.io.IOException;
import java.io.InputStream;

/**
 * Set Text Attributes Instruction.
 *
 * <P><CODE><B>set.text.attr</B> Instruction 0x6d</CODE><BR>
 * Sets foreground and background colours for <CODE>display</CODE>,
 * <CODE>get.num</CODE> and <CODE>get.string</CODE> commands.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see com.sierra.agi.logic.interpret.instruction.InstructionDisplay
 * @see com.sierra.agi.logic.interpret.instruction.InstructionGetNum
 * @see com.sierra.agi.logic.interpret.instruction.InstructionGetString
 */
public class InstructionSetTextAttributes extends InstructionBi {
    /**
     * Creates new Set Text Attributes Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSetTextAttributes(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        ViewScreen viewScreen = logicContext.getViewScreen();

        viewScreen.setForegroundColor((byte) p1);
        viewScreen.setBackgroundColor((byte) p2);
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
        return new String[]{"set.text.attr", Integer.toString(p1), Integer.toString(p2)};
    }
//#endif DEBUG
}