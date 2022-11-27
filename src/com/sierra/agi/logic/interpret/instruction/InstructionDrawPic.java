/*
 * InstructionDrawPic.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.ScriptBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Draw Picture Instruction.
 *
 * <P><CODE><B>draw.pic.v</B> Instruction 0x19</CODE><BR>
 * Picture <CODE>v[p1]</CODE> is draw onto screen.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionDrawPic extends InstructionUni {
    /**
     * Creates new Load Picture Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionDrawPic(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short pictureNumber = logicContext.getVar(p1);
        logicContext.setPictureNumber(pictureNumber);
        logicContext.getScriptBuffer().addScript(ScriptBuffer.ScriptBufferEventType.DrawPic, pictureNumber, null);
        logicContext.getViewTable().drawPic(logicContext.getCache().getPicture(pictureNumber));
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
        String[] names = new String[2];

        names[0] = "draw.pic";
        names[1] = "v" + p1;

        return names;
    }
//#endif DEBUG
}