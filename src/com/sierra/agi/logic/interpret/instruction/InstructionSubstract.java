/*
 * InstructionSubstract.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.*;
import com.sierra.agi.logic.*;
import com.sierra.agi.logic.interpret.*;
import com.sierra.agi.logic.interpret.jit.*;
import com.sierra.jit.code.*;
import java.io.*;

/**
 * Substract Instruction.
 *
 * <P><CODE><B>sub.n</B> Instruction 0x07</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is decremented by <CODE>p2</CODE>,
 * i.e. <CODE>v[p1] -= p2</CODE>.
 * </P>
 *
 * <P><CODE><B>sub.v</B> Instruction 0x08</CODE><BR>
 * The value of variable <CODE>v[p1]</CODE> is decremented by <CODE>v[p2]</CODE>,
 * i.e. <CODE>v[p1] -= v[p2]</CODE>.
 * </P>
 *
 * If the value is lesser than <CODE>0</CODE> the result wraps (so <CODE>1 - 2 == 255</CODE>).
 *
 * @author  Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSubstract extends InstructionBi
{
    /** 
     * Creates new Substract Instruction.
     *
     * @param context   Game context where this instance of the instruction will be used. (ignored)
     * @param stream    Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode  Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSubstract(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException
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
        short v;
        
        if (bytecode == 0x08)
        {
            v = logicContext.getVar(p2);
        }
        else
        {
            v = p2;
        }

        if (v != 0)
        {
            v -= logicContext.getVar(p1);
            logicContext.setVar(p1, (short)(v & 0xff));
        }
        
        return 3;
    }

    /**
     * Compile the Instruction into Java Bytecode.
     *
     * @param compileContext Logic Compile Context.
     */
    public void compile(LogicCompileContext compileContext)
    {
        Scope scope = compileContext.scope;
       
        scope.addLoadVariable("logicContext");
        scope.addPushConstant(p1);
        scope.addDuplicateLong();
        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "getVar", "(S)S");

        if (bytecode == 0x06)
        {
            compileContext.compileGetVariableValue(p2);
        }
        else
        {
            scope.addPushConstant(p2);
        }

        scope.addIntegerSubstract();
        scope.addPushConstant(0xff);
        scope.addIntegerAnd();
        
        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "setVar", "(SS)V");
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
        String[] names = new String[3];
        
        names[0] = "sub";
        names[1] = "v" + p1;

        switch (bytecode)
        {
        default:
        case 0x07:
            names[2] = Integer.toString(p2);
            break;
        case 0x08:
            names[2] = "v" + p2;
            break;
        }

        return names;
    }
//#endif DEBUG
}