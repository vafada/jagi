/*
 * InstructionMenuInput.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Menu Input Instruction.
 *
 * <P><CODE><B>menu.input</B> Instruction 0xa1</CODE><BR>
 * If f[14] is set, a menu system is shown on the screen, allowing the user to
 * choose an item. Whether an item with the code c has been chosen can be
 * tested using a command controller(c), where c is the code assigned to the
 * menu item.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionMenuInput extends Instruction {
    /**
     * Creates new Menu Input Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionMenuInput(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        if (logicContext.getFlag(LogicContext.FLAG_MENUS_WORK)) {
            logicContext.showMenu();
        }

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
        return new String[]{"menu.input"};
    }
//#endif DEBUG
}