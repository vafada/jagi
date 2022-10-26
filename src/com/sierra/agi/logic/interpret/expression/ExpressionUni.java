/**
 * ExpressionUni.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.expression;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base Class for all Logic's Expressions that has 1 parameter.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class ExpressionUni extends Expression {
    /** Bytecode */
    protected short bytecode;

    /** Parameter #1 */
    protected short p1;

    /**
     * Creates a new Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    protected ExpressionUni(InputStream stream, short bytecode) throws IOException {
        this.bytecode = bytecode;
        this.p1 = (short) stream.read();
    }

    /**
     * Determine Expression Size. In this class, it always return 2. (It is the
     * size of a expression that has 1 parameter.)
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return 2;
    }
}