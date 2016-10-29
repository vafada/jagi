/*
 * InstructionGet.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.logic.interpret.jit.*;
import com.sierra.jit.code.*;
import java.io.*;

/**
 * Get Instruction.
 *
 * <P><CODE><B>get.n</B> Instruction 0x5C</CODE><BR>
 * Stores <CODE>255</CODE> in room field of an object <CODE>i[p1]</CODE>,
 * which means that the player owns it.
 * </P>
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionGet extends InstructionUni
{
    /** 
     * Creates new Get Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionGet(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException
    {
        super(stream, bytecode);
    }
    
    /**
     * Execute the Instruction.
     *
     * @param logic         Logic used to execute the instruction.
     * @param logicContext  Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext)
    {
        short p = p1;
        
        if (bytecode == 0x5d)
        {
            p = logicContext.getVar(p);
        }

        logicContext.setObject(p, LogicContext.EGO_OWNED);
        return 2;
    }

//#ifdef DEBUG
    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames()
    {
        String[] names = new String[2];
        
        names[0] = "get";
        
        switch (bytecode)
        {
        case 0x5c:
            names[1] = "i" + p1;
            break;
        case 0x5d:
            names[1] = "vi" + p1;
            break;
        }
        
        return names;
    }
//#endif DEBUG
}