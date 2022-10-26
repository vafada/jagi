/*
 * InstructionGoto.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.io.ByteCasterStream;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Goto Instruction.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGoto extends InstructionMoving implements Compilable {
    /**
     * Creates a new Goto Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGoto(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        ByteCasterStream bstream = new ByteCasterStream(stream);

        relativeGotoAddress = (short) (bstream.lohiReadUnsignedShort());
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the address referenced by this instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        return relativeGotoAddress + 3;
    }

    /**
     * Determine Instruction Size. In this class, it always return 3. (It is the
     * size of a goto instruction.)
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return 3;
    }

    public void compile(LogicCompileContext compileContext) {
        int addr = compileContext.pc + 3 + relativeGotoAddress;

        compileContext.scope.addGoto("agi_" + addr);
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[2];

        names[0] = "goto";
        names[1] = String.format("$%04X", getAbsoluteGotoAddress());

        return names;
    }
//#endif DEBUG
}