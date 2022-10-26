/**
 * ExpressionIsSet.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.CompilableExpression;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;
import com.sierra.jit.code.InstructionConditionalGoto;
import com.sierra.jit.code.Scope;

import java.io.IOException;
import java.io.InputStream;

/**
 * Is Set Expression.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public final class ExpressionIsSet extends ExpressionUni implements CompilableExpression {
    /**
     * Creates a new Is Set Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionIsSet(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Evaluate Expression.
     *
     * @param logic         Logic used to evaluate the expression.
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext logicContext) {
        return logicContext.getFlag(p1);
    }

    public void compile(LogicCompileContext compileContext, boolean jumpOnTrue, String destination) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("logicContext");

        scope.addPushConstant(p1);

        scope.addInvokeVirtual("com.sierra.agi.logic.LogicContext", "getFlag", "(S)Z");

        scope.addConditionalGoto(
                jumpOnTrue ? InstructionConditionalGoto.CONDITION_IFNE : InstructionConditionalGoto.CONDITION_IFEQ,
                destination);
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the expression.
     */
    public String[] getNames() {
        String[] names = new String[2];

        names[0] = "isset";
        names[1] = "f" + p1;

        return names;
    }
//#endif DEBUG
}