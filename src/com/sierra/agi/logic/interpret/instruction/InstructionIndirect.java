/*
 * InstructionIndirect.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Indirect Assign Instruction.
 *
 * <P><CODE><B>lindirect.n</B> Instruction 0x09</CODE><BR>
 * Variable <CODE>v[i]</CODE> where <CODE>i</CODE> is the value of <CODE>v[p1]</CODE>
 * is assigned a value of <CODE>p2</CODE>, i.e. <CODE>v[v[p1]] = p2</CODE>.
 * </P>
 *
 * <P><CODE><B>lindirect.v</B> Instruction 0x0a</CODE><BR>
 * Variable <CODE>v[i]</CODE> where <CODE>i</CODE> is the value of <CODE>v[p1]</CODE>
 * is assigned a value of <CODE>v[p2]</CODE>, i.e. <CODE>v[v[p1]] = v[p2]</CODE>.
 * </P>
 *
 * <P><CODE><B>rindirect</B> Instruction 0x0b</CODE><BR>
 * Variable <CODE>v[p1]</CODE> is assigned the value of <CODE>v[i]</CODE> where
 * <CODE>i</CODE> is the value of <CODE>v[p2]</CODE>, i.e. <CODE>v[p1] = v[v[p2]]</CODE>.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionIndirect extends InstructionBi {
    /**
     * Creates new Indirect Assign Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionIndirect(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        switch (bytecode) {
            default:
            case 0x09:
                logicContext.setVar(logicContext.getVar(p1), logicContext.getVar(p2));
                break;
            case 0x0a:
                logicContext.setVar(p1, logicContext.getVar(logicContext.getVar(p2)));
                break;
            case 0x0b:
                logicContext.setVar(logicContext.getVar(p1), p2);
                break;
        }

        return 3;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[3];

        names[0] = "assign";

        switch (bytecode) {
            default:
            case 0x09:
                names[1] = "vv" + p1;
                names[2] = "v" + p2;
                break;
            case 0x0a:
                names[1] = "v" + p1;
                names[2] = "vv" + p2;
                break;
            case 0x0b:
                names[1] = "vv" + p1;
                names[2] = Integer.toString(p2);
                break;
        }

        return names;
    }
//#endif DEBUG
}