/*
 *  LogicLine.java
 *  Adventure Game Interpreter Debug Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2002 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug.logic;

import com.sierra.agi.logic.interpret.instruction.Instruction;

public class LogicLine {
    public int level;
    public int instructionNumber;
    public Instruction instruction;
    public String line;
    public String text;

    public LogicLine(int level, String line) {
        this.level = level;
        this.instructionNumber = -1;
        this.line = line;

        generateText();
    }

    public LogicLine(int level, int instructionNumber, Instruction instruction, String line) {
        this.level = level;
        this.instructionNumber = instructionNumber;
        this.instruction = instruction;
        this.line = line;

        generateText();
    }

    public LogicLine(int level, int instructionNumber, Instruction[] instructions, String line) {
        this.level = level;
        this.instructionNumber = instructionNumber;
        this.instruction = instructions[instructionNumber];
        this.line = line;

        generateText();
    }

    protected void generateText() {
        StringBuffer buffer = new StringBuffer();
        int index;

        for (index = 0; index < level; index++) {
            buffer.append("    ");
        }

        buffer.append(line);
        text = buffer.toString();
    }
}
