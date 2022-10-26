/**
 * ExpressionObjInRoom.java
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
 * Object In Room Expression.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public final class ExpressionObjInRoom extends ExpressionBi implements CompilableExpression {
    /**
     * Creates a new Object In Room Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    public ExpressionObjInRoom(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
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
        return (logicContext.getObject(p1) == logicContext.getVar(p2));
    }

    public void compile(LogicCompileContext compileContext, boolean jumpOnTrue, String destination) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("logicContext");
        scope.addPushConstant(p1);
        scope.addInvokeVirtual("com.sierra.agi.logic.LogicContext", "getObject", "(S)S");

        compileContext.compileGetVariableValue(p2);

        scope.addConditionalGoto(
                jumpOnTrue ? InstructionConditionalGoto.CONDITION_CMPEQ : InstructionConditionalGoto.CONDITION_CMPNE,
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
        return new String[]{"obj.in.room", "i" + p1, "v" + p2};
    }
//#endif DEBUG
}