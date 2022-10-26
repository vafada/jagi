/*
 * InstructionReleaseLoop.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Release Loop Instruction.
 *
 * <P>Turns on automatic choice of loop number depending on the direction of motion
 * of the object <CODE>p1</CODE>.
 * <PRE>
 * 1
 * 8     |     2
 * \   |   /
 * \ | /
 * 7 ------------- 3      0 - object stands still
 * / | \
 * /   |   \
 * 6     |     4
 * 5</PRE>
 *
 * <P>Automatic choice of the loop is done according to the table:</P>
 *
 * <TABLE BORDER=1>
 * <TR><TD COLSPAN=10>For objects with fewer than 4 but more than 1 loops</TD></TR>
 * <TR><TD>Direction</TD><TD>0</TD><TD>1</TD><TD>2</TD><TD>3</TD><TD>4</TD><TD>5</TD><TD>6</TD><TD>7</TD><TD>8</TD></TR>
 * <TR><TD>Loop</TD><TD>x</TD><TD>x</TD><TD>0</TD><TD>0</TD><TD>0</TD><TD>x</TD><TD>1</TD><TD>1</TD><TD>1</TD></TR>
 * <TR><TD COLSPAN=10>for objects with more than 4 loops:</TD></TR>
 * <TR><TD>Direction</TD><TD>0</TD><TD>1</TD><TD>2</TD><TD>3</TD><TD>4</TD><TD>5</TD><TD>6</TD><TD>7</TD><TD>8</TD></TR>
 * <TR><TD>Loop</TD><TD>x</TD><TD>3</TD><TD>0</TD><TD>0</TD><TD>0</TD><TD>2</TD><TD>1</TD><TD>1</TD><TD>1</TD></TR>
 * </TABLE>
 *
 * <P><CODE>x</CODE> means that the current loop number is retained.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionReleaseLoop extends InstructionUni {
    /**
     * Creates new Release Loop Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionReleaseLoop(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getViewTable().releaseLoop(p1);
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
        return new String[]{"release.loop", "o" + p1};
    }
//#endif DEBUG
}