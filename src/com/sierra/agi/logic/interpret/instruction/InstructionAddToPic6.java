package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class InstructionAddToPic6 extends InstructionHex {
    /**
     * Creates new Add to Picture Instruction (for 6 parameter AGI v1 version).
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionAddToPic6(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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

        logicContext.getViewTable().addToPic(tp1, tp2, tp3, tp4, tp5, (byte) tp6, (short) 0);
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
        String[] names = new String[7];

        names[0] = "add.to.pic";
        names[1] = Integer.toString(p1);
        names[2] = Integer.toString(p2);
        names[3] = Integer.toString(p3);
        names[4] = Integer.toString(p4);
        names[5] = Integer.toString(p5);
        names[6] = Integer.toString(p6);

        return names;
    }
//#endif DEBUG
}
