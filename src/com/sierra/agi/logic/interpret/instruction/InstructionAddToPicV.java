/*
 * InstructionAddToPic.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Add to Picture Instruction.
 *
 * <P><CODE><B>add.to.pic.n</B> Instruction 0x7a</CODE><BR>
 * </P>
 *
 * <P><CODE><B>add.to.pic.v</B> Instruction 0x7b</CODE><BR>
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionAddToPicV extends InstructionSept {
    /**
     * Creates new Add to Picture Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionAddToPicV(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        short tp1 = p1;
        short tp2 = p2;
        short tp3 = p3;
        short tp4 = p4;
        short tp5 = p5;
        short tp6 = p6;
        short tp7 = p7;

        tp1 = logicContext.getVar(tp1);
        tp2 = logicContext.getVar(tp2);
        tp3 = logicContext.getVar(tp3);
        tp4 = logicContext.getVar(tp4);
        tp5 = logicContext.getVar(tp5);
        tp6 = logicContext.getVar(tp6);
        tp7 = logicContext.getVar(tp7);

        logicContext.getViewTable().addToPic(tp1, tp2, tp3, tp4, tp5, (byte) tp6, tp7);
        return 8;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[8];

        names[0] = "add.to.pic.v";
        names[1] = "v" + p1;
        names[2] = "v" + p2;
        names[3] = "v" + p3;
        names[4] = "v" + p4;
        names[5] = "v" + p5;
        names[6] = "v" + p6;
        names[7] = "v" + p7;

        return names;
    }
//#endif DEBUG
}