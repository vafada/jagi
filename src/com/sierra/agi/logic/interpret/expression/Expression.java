/**
 * Expression.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.InputStream;
import java.util.Enumeration;

/**
 * Base Class for all Logic's Boolean Expressions.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class Expression {
    /**
     * Creates a new Expression. Does absolutly nothing in this class, it is
     * included as a formal declaration.
     */
    protected Expression() {
    }

    /**
     * Creates a new Expression. Does absolutly nothing in this class, it is
     * included as a formal declaration.
     *
     * @param logicContext Game context where this instance of the expression will be used.
     * @param stream       Logic Stream. Expression must be written in uninterpreted format.
     * @param reader       LogicReader used in the reading of this expression.
     * @param bytecode     Bytecode of the current expression.
     */
    protected Expression(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) {
    }

    /**
     * Evaluate Expression.
     *
     * @param logicContext  Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public abstract boolean evaluate(Logic logic, LogicContext logicContext) throws Exception;

    /**
     * Determine Expression Size. In this class, it always return 1. (It is the
     * size of a expression that has no parameter.)
     *
     * @return Returns the expression size.
     */
    public int getSize() {
        return 1;
    }

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the expression.
     */
    public abstract String[] getNames();

    /**
     * Retreive contained expressions.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns a Enumeration of expression contained. May be <CODE>null</CODE>.
     */
    public Enumeration getContainedExpressions() {
        return null;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer(32);
        String[] names = getNames();

        buff.append(names[0]);

        if (names.length > 1) {
            int i;

            buff.append("(");
            for (i = 1; i < names.length; i++) {
                if (i != 1) {
                    buff.append(",");
                }

                buff.append(names[i]);
            }
            buff.append(")");
        }

        return buff.toString();
    }
}