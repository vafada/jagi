/*
 * InstructionGetString.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Get String Instruction.
 *
 * <P><CODE><B>get.string</B> Instruction 0x73</CODE><BR>
 * User input is stored in <CODE>s[p1]</CODE>. <CODE>m[p2]</CODE> is the number
 * of the message used as the prompt. <CODE>p3</CODE>, <CODE>p4</CODE> and
 * <CODE>p5</CODE> are input position and maximum string length.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGetString extends InstructionPent {
    /**
     * Creates new Get String Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGetString(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        return 6;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"get.string", "s" + p1, "m" + p2, Integer.toString(p3), Integer.toString(p4), Integer.toString(p5)};
    }
//#endif DEBUG
}