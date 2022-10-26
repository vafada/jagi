package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class InstructionUnknown2 extends InstructionBi {

    /**
     * Creates new Unknown instruction with two parametes.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     */
    public InstructionUnknown2(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    @Override
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        return 3;
    }

    @Override
    public String[] getNames() {
        String[] names = new String[3];

        names[0] = "unknown" + Integer.toHexString(bytecode);
        names[1] = Integer.toString(p1);
        names[2] = Integer.toString(p2);

        return names;
    }
}
