/*
 * InstructionLoadView.java
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
 * Load View Instruction.
 *
 * <P><CODE><B>load.view.n</B> Instruction 0x1e</CODE><BR>
 * View <CODE>p1</CODE> is loaded into memory.</P>
 *
 * <P><CODE><B>load.view.v</B> Instruction 0x1f</CODE><BR>
 * View <CODE>v[p1]</CODE> is loaded into memory.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionLoadView extends InstructionUni implements Compilable {
    /**
     * Creates new Load View Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionLoadView(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        logicContext.getScriptBuffer().addScript(ScriptBuffer.ScriptBufferEventType.LoadView, p1, null);
        logicContext.getCache().loadView(p1);
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

        scope.addPushConstant(p1);

        scope.addInvokeSpecial("com.sierra.agi.res.ResourceCache", "loadView", "(S)V");
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

        names[0] = "load.view";
        names[1] = Integer.toString(p1);

        return names;
    }
//#endif DEBUG
}