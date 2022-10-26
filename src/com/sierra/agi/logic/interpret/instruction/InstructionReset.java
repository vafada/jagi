/*
 * InstructionReset.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;
import com.sierra.jit.code.Scope;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reset Flag Instruction.
 *
 * <P><CODE><B>reset.n</B> Instruction 0x0e</CODE><BR>
 * <CODE>f[p1]</CODE> is set to <CODE>false</CODE>.</P>
 *
 * <P><CODE><B>reset.v</B> Instruction 0x0f</CODE><BR>
 * <CODE>f[v[p1]]</CODE> is set to <CODE>false</CODE>.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionReset extends InstructionUni implements Compilable {
    /**
     * Creates new Reset Flag Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionReset(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short p = p1;

        if (bytecode >= 0x10) {
            p = logicContext.getVar(p);
        }

        logicContext.setFlag(p, false);
        return 2;
    }

    public void compile(LogicCompileContext compileContext) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("logicContext");

        if (bytecode == 0x0f) {
            compileContext.compileGetVariableValue(p1);
        } else {
            scope.addPushConstant(p1);
        }

        scope.addPushConstant(0);
        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "setFlag", "(SZ)V");
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

        switch (bytecode) {
            default:
            case 0x0D:
                names[0] = "reset";
                names[1] = "f" + p1;
                break;
            case 0x10:
                names[0] = "reset";
                names[1] = "vf" + p1;
                break;
        }

        return names;
    }
//#endif DEBUG
}