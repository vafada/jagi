/*
 * InstructionOverlayPic.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.ScriptBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Overlay Picture Instruction.
 *
 * <P><CODE><B>overlay.pic</B> Instruction 0x1C</CODE><BR>
 * Just like <CODE>draw.pic</CODE>, only the internal buffer is not cleared
 * before drawing. Picture <CODE>v[p1]</CODE> is drawn over the existing picture.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionOverlayPic extends InstructionUni {
    /**
     * Creates new Overlay Picture Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionOverlayPic(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getScriptBuffer().addScript(ScriptBuffer.ScriptBufferEventType.OverlayPic, pictureNumber, null);
        logicContext.getViewTable().overlayPic(logicContext.getCache().getPicture(pictureNumber));
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
        return new String[]{"overlay.pic", "v" + p1};
    }
//#endif DEBUG
}