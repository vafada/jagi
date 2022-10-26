package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.ViewScreen;

import java.io.IOException;
import java.io.InputStream;

public class InstructionTextScreenAttribute extends InstructionUni {
    /**
     * Creates new Text Screen Attribute Instruction, and AGI v1 only instruction (replaced
     * by Text Screen and Set Text Attribute in AGI v2?). Would appear to change to the
     * text screen and set a foreground colour.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionTextScreenAttribute(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    @Override
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        ViewScreen viewScreen = logicContext.getViewScreen();

        viewScreen.setForegroundColor((byte) p1);

        return 2;
    }

    @Override
    public String[] getNames() {
        return new String[]{"text.screen.attribute", Integer.toString(p1)};
    }
}
