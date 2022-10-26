/*
 *  CompilableExpression.java
 *  Adventure Game Interpreter Logic Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret.jit;

public interface CompilableExpression {
    void compile(LogicCompileContext compileContext, boolean jumpOnTrue, String destination);
}