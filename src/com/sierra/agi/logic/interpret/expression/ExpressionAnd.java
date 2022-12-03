/**
 * ExpressionAnd.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.jit.CompilableExpression;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;

import java.util.Enumeration;
import java.util.Vector;

/**
 * And Expression.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public final class ExpressionAnd extends Expression implements CompilableExpression {
    /** Contained expressions. */
    private final Vector contained = new Vector(5, 2);

    /** Expression size. */
    private int instructionSize = 0;

    /** Creates a new And Expression. */
    public ExpressionAnd() {
    }

    /**
     * Evaluate Expression.
     *
     * @param logic         Logic used to evaluate the expression.
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext logicContext) throws Exception {
        Enumeration en = contained.elements();
        Expression expression;

        while (en.hasMoreElements()) {
            expression = (Expression) en.nextElement();

            if (!expression.evaluate(logic, logicContext)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Add a expression to this expression.
     *
     * @param expression Expression to add.
     */
    public void add(Expression expression) {
        contained.add(expression);
        instructionSize += expression.getSize();
    }

    /**
     * Determine Expression Size.
     *
     * @return Returns the expression size.
     */
    public int getSize() {
        return instructionSize;
    }

    public void compile(LogicCompileContext compileContext, boolean jumpOnTrue, String destination) {
        String adaptedDestination;
        CompilableExpression compilable;
        int i;

        if (jumpOnTrue) {
            adaptedDestination = compileContext.scope.generateLabel();
        } else {
            adaptedDestination = destination;
        }

        for (i = 0; i < contained.size() - 1; i++) {
            compilable = (CompilableExpression) contained.get(i);
            compilable.compile(compileContext, false, adaptedDestination);
        }

        compilable = (CompilableExpression) contained.get(i);
        compilable.compile(compileContext, jumpOnTrue, destination);

        if (jumpOnTrue) {
            compileContext.scope.addLabel(adaptedDestination);
        }
    }

//#ifdef DEBUG

    /**
     * Retreive contained expressions.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a Enumeration of expression contained. May be <CODE>null</CODE>.
     */
    public Enumeration getContainedExpressions() {
        return contained.elements();
    }

    /**
     * Returns a String represention of the expression.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a String representation.
     */
    public String toString() {
        String s = "";
        Enumeration en = getContainedExpressions();
        Expression expression;

        while (en.hasMoreElements()) {
            expression = (Expression) en.nextElement();
            s += expression.toString();

            if (en.hasMoreElements()) {
                s += " && ";
            }
        }

        return s;
    }

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Always return <CODE>null</CODE> in this implentation.
     */
    public String[] getNames() {
        return null;
    }
//#endif DEBUG
}