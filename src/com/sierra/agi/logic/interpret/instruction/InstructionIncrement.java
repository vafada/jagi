/*
 * InstructionIncrement.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;
import com.sierra.jit.code.InstructionConditionalGoto;
import com.sierra.jit.code.Scope;

import java.io.IOException;
import java.io.InputStream;

/**
 * Increment Instruction.
 * <p>
 * The value of the variable <CODE>v[p1]</CODE> is incremented by one,
 * i.e. <CODE>v[p1] = v[p1] + 1</CODE>. If the value is already <CODE>255</CODE>,
 * it is left unchanged.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionIncrement extends InstructionUni implements Compilable {
    /**
     * Creates a new Increment Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionIncrement(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short vn = logicContext.getVar(p1);

        if (vn < 255) {
            logicContext.setVar(p1, (short) (vn + 1));
        }

        return 2;
    }

    /**
     * Compile the Instruction into Java Bytecode.
     *
     * @param compileContext Logic Compile Context.
     */
    public void compile(LogicCompileContext compileContext) {
        Scope scope = compileContext.scope;
        String end = scope.generateLabel();

        compileContext.compileGetVariableValue(p1);
        scope.addDuplicate();
        scope.addStoreVariable("temp");
        scope.addPushConstant(255);

        scope.addConditionalGoto(InstructionConditionalGoto.CONDITION_CMPEQ, end);

        scope.addLoadVariable("logicContext");
        scope.addPushConstant(p1);
        scope.addLoadVariable("temp");
        scope.addPushConstant(1);
        scope.addIntegerAdd();
        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "setVar", "(SS)V");

        scope.addLabel(end);
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

        names[0] = "inc";
        names[1] = "v" + p1;
        return names;
    }

    /**
     * Returns a String representation of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString() {
        return "++v" + p1;
    }
//#endif DEBUG
}