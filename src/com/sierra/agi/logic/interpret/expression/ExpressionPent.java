/**
 * ExpressionPent.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.expression;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base Class for all Logic's Expressions that has 5 parameters.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class ExpressionPent extends Expression {
    /** Bytecode */
    protected short bytecode;

    /** Parameter #1 */
    protected short p1;

    /** Parameter #2 */
    protected short p2;

    /** Parameter #3 */
    protected short p3;

    /** Parameter #4 */
    protected short p4;

    /** Parameter #5 */
    protected short p5;

    /**
     * Creates a new Expression.
     *
     * @param context   Game context where this instance of the expression will be used.
     * @param stream    Logic Stream. Expression must be written in uninterpreted format.
     * @param reader    LogicReader used in the reading of this expression.
     * @param bytecode  Bytecode of the current expression.
     */
    protected ExpressionPent(InputStream stream, short bytecode) throws IOException {
        this.bytecode = bytecode;
        this.p1 = (short) stream.read();
        this.p2 = (short) stream.read();
        this.p3 = (short) stream.read();
        this.p4 = (short) stream.read();
        this.p5 = (short) stream.read();
    }

    /**
     * Determine Expression Size. In this class, it always return 3. (It is the
     * size of a expression that has 2 parameters.)
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return 6;
    }
}