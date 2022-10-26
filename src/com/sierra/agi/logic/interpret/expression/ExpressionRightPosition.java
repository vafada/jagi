/**
 * ExpressionRightPosition.java
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
 * Right Position Expression.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public final class ExpressionRightPosition extends ExpressionPent implements CompilableExpression {
    /**
     * Creates a new Right Position Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionRightPosition(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        return logicContext.getViewTable().inRight(p1, p2, p3, p4, p5);
    }

    public void compile(LogicCompileContext compileContext, boolean jumpOnTrue, String destination) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("viewTable");
        scope.addPushConstant(p1);
        scope.addPushConstant(p2);
        scope.addPushConstant(p3);
        scope.addPushConstant(p4);
        scope.addPushConstant(p5);
        scope.addInvokeVirtual("com.sierra.agi.view.ViewTable", "inRight", "(SSSSS)Z");

        scope.addConditionalGoto(
                jumpOnTrue ? InstructionConditionalGoto.CONDITION_IFNE : InstructionConditionalGoto.CONDITION_IFEQ,
                destination);
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Always return <CODE>null</CODE> in this implentation.
     */
    public String[] getNames() {
        return new String[]{"right.pos", Integer.toString(p1), Integer.toString(p2), Integer.toString(p3), Integer.toString(p4), Integer.toString(p5)};
    }
//#endif DEBUG
}