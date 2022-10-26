package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class InstructionUnknown0 extends Instruction {
    /**
     * Creates new unknown instruction with no parameters.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionUnknown0(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    @Override
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        return 1;
    }

    @Override
    public String[] getNames() {
        return new String[]{"unknown"};
    }
}
