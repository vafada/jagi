/*
 * InstructionSubmitMenu.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Submit Menu Instruction.
 *
 * <P><CODE><B>submit.menu</B> Instruction 0x9e</CODE><BR>
 * Ends menu creation.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSubmitMenu extends Instruction implements Compilable {
    /**
     * Creates new Submit Menu Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSubmitMenu(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        return 1;
    }

    public void compile(LogicCompileContext compileContext) {
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"submit.menu"};
    }
//#endif DEBUG
}