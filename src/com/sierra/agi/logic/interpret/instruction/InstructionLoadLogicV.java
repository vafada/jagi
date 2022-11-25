/*
 * InstructionLoadLogic.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;
import com.sierra.agi.view.ScriptBuffer;
import com.sierra.jit.code.Scope;

import java.io.IOException;
import java.io.InputStream;

/**
 * Load Logic Instruction.
 *
 * <P><CODE><B>load.logic.n</B> Instruction 0x14</CODE><BR>
 * Logic <CODE>p1</CODE> is loaded into memory.</P>
 *
 * <P><CODE><B>load.logic.v</B> Instruction 0x15</CODE><BR>
 * Logic <CODE>v[p1]</CODE> is loaded into memory.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionLoadLogicV extends InstructionUni implements Compilable {
    /**
     * Creates new Load Logic Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionLoadLogicV(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short p = logicContext.getVar(p1);
        logicContext.getScriptBuffer().addScript(ScriptBuffer.ScriptBufferEventType.LoadLogic, p, null);
        logicContext.getCache().loadLogic(p);
        return 2;
    }

    /**
     * Compile the Instruction into Java Bytecode.
     *
     * @param compileContext Logic Compile Context.
     */
    public void compile(LogicCompileContext compileContext) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("cache");

        compileContext.compileGetVariableValue(p1);

        scope.addInvokeSpecial("com.sierra.agi.res.ResourceCache", "loadLogic", "(S)V");
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

        names[0] = "load.logics.v";
        names[1] = "v" + p1;

        return names;
    }
//#endif DEBUG
}