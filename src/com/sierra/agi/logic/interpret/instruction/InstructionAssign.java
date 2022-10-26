/*
 * InstructionAssign.java
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
 * Assign Instruction.
 *
 * <P><CODE><B>assign.n</B> Instruction 0x03</CODE><BR>
 * Variable <CODE>p1</CODE> is assigned the value <CODE>p2</CODE>,
 * i.e. <CODE>v[p1] = p2</CODE>.
 * </P>
 *
 * <P><CODE><B>assign.v</B> Instruction 0x04</CODE><BR>
 * Variable <CODE>p1</CODE> is assigned the value of variable <CODE>p2</CODE>,
 * i.e. <CODE>v[p1] = v[p2]</CODE>.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionAssign extends InstructionBi implements Compilable {
    /**
     * Creates new Assign Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when stream.read() fails.
     */
    public InstructionAssign(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        short v = p2;
        logicContext.setVar(p1, v);
        return getSize();
    }

    /**
     * Compile the Instruction into Java Bytecode.
     *
     * @param compileContext Logic Compile Context.
     */
    public void compile(LogicCompileContext compileContext) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("logicContext");
        scope.addPushConstant(p1);
        scope.addPushConstant(p2);

        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "setVar", "(SS)V");
    }

//#ifdef DEBUG

    /**
     * Retreive the textual name of the instruction.
     *
     * @return Returns the textual name of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[3];

        names[0] = "assign";
        names[1] = "v" + p1;
        names[2] = Integer.toString(p2);

        return names;
    }

    /**
     * Returns a String representation of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString() {

        String buffer = "v" + p1 +
                " = " +
                p2;
        return buffer;
    }
//#endif DEBUG
}