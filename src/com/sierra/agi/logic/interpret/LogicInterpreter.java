/*
 *  LogicInterpreter.java
 *  Adventure Game Interpreter Logic Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.interpret;

import com.sierra.agi.logic.InternalLogicException;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.LogicExitAll;
import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;

import java.util.Vector;

public class LogicInterpreter extends Logic {
    protected Instruction[] instructions;
    protected String[] messages;
    protected int[] sizes;

    public LogicInterpreter(short logicNumber, Vector instructions, String[] messages) {
        int i;
        Instruction instruction;

        this.logicNumber = logicNumber;
        this.instructions = new Instruction[instructions.size()];
        this.messages = messages;
        this.sizes = new int[instructions.size()];

        /* Transfert Vector to an Array of Instructions. */
        instructions.toArray(this.instructions);

        /* Precalculate the sizes of Each Instructions */
        for (i = 0; i < sizes.length; i++) {
            instruction = (Instruction) instructions.get(i);
            sizes[i] = instruction.getSize();
        }
    }

    public void execute(LogicContext logicContext) throws LogicException {
        int in = logicContext.getScanStart(logicNumber);
        int result = 0;
        Instruction instruction = null;
        Instruction[] instructions = this.instructions;
        int[] sizes = this.sizes;

        try {
            logicContext.pushLogic(logicNumber);

            while (true) {
                instruction = instructions[in];

                // System.out.println(logicNumber + ": instruction = " + in + " = " + instruction);

                try {
                    result = instruction.execute(this, logicContext);

                    if ((instruction instanceof InstructionMoving) && (result != sizes[in])) {
                        in = ((InstructionMoving) instruction).getDestination(in, sizes);
                    } else {
                        in++;
                    }
                } catch (LogicSetScanStart ex) {
                    logicContext.setScanStart(logicNumber, in);
                    in++;
                } catch (LogicResetScanStart ex) {
                    logicContext.setScanStart(logicNumber, 0);
                    in++;
                }
            }
        } catch (LogicReturn lrex) {
            //System.out.println("catch LogicReturn");
        } catch (LogicExitAll lea) {
            throw lea;
        } catch (LogicException lex) {
            throw lex;
        } catch (Throwable thr) {
            throw new InternalLogicException(logicContext, thr);
        } finally {
            logicContext.popLogic();
        }
    }

    public String getMessage(int msgNumber) {
        return messages[msgNumber];
    }

    public String[] getMessages() {
        return messages;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public int[] getInstructionSizes() {
        return sizes;
    }

    public int getInstructionAddress(int instructionNumber) {
        int address = 0;
        int index;

        for (index = 0; index < instructionNumber; index++) {
            address += sizes[index];
        }

        return address;
    }

    public int getLogicNumber() {
        return logicNumber;
    }
}
