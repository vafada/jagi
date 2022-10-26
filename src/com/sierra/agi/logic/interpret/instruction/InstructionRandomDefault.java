package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * The AGIv1 version of the random command that did not take the range but
 * rather uses a default range.
 *
 * @author Lance Ewing
 */
public class InstructionRandomDefault extends InstructionUni {
    /**
     * Random number generator.
     */
    protected static Random random = new Random();

    /**
     * Creates new AGI v1 random instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionRandomDefault(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    @Override
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        // Currently assuming that this is between 0 and 255 inclusive.
        logicContext.setVar(p1, (short) (random.nextInt(256)));
        return 1;
    }

    @Override
    public String[] getNames() {
        return new String[]{"random", "v" + p1};
    }
}
