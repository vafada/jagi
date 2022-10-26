/*
 * InstructionParse.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parse Instruction.
 *
 * <P><CODE><B>parse</B> Instruction 0x75</CODE><BR>
 * Parses <CODE>s[p1]</CODE> as if it was entered by the player.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionParse extends InstructionUni {
    /**
     * Creates new Parse Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionParse(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.setVar(LogicContext.VAR_WORD_NOT_FOUND, (short) 0);
        logicContext.setFlag(LogicContext.FLAG_ENTERED_COMMAND, false);
        logicContext.setFlag(LogicContext.FLAG_SAID_ACCEPTED_INPUT, false);

        logicContext.enterCommand(logicContext.processMessage(logicContext.getString(p1)));
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
        return new String[]{"parse", "s" + p1};
    }
//#endif DEBUG
}