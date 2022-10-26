/*
 * InstructionPrintAt.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.MessageBox;

import java.io.IOException;
import java.io.InputStream;

/**
 * Print At Instruction.
 *
 * <P><CODE><B></B> Instruction 0x</CODE><BR>
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionPrintAtV extends Instruction {
    /**
     * Bytecode
     */
    protected short bytecode;

    /**
     * Parameter #1
     */
    protected short p1;

    /**
     * Parameter #2
     */
    protected short p2;

    /**
     * Parameter #3
     */
    protected short p3;

    /**
     * Parameter #4
     */
    protected short p4;

    /**
     * Intruction Size
     */
    protected int size;

    /**
     * Creates new Print At Instruction (V).
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionPrintAtV(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        this.bytecode = bytecode;
        p1 = (short) stream.read();
        p2 = (short) stream.read();
        p3 = (short) stream.read();

        if (engineEmulation > 0x2272) {
            p4 = (short) stream.read();
            size = 5;
        } else {
            p4 = (short) 30;
            size = 4;
        }
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        short tp1 = p1;
        short tp2 = p2;
        short tp3 = p3;
        short tp4 = p4;

        tp1 = logicContext.getVar(tp1);
        tp2 = logicContext.getVar(tp2);
        tp3 = logicContext.getVar(tp3);

        if (size > 4) {
            tp4 = logicContext.getVar(tp4);
        }

        (new MessageBox(logicContext.processMessage(logic.getMessageProcessed(tp1)), tp2, tp3, tp4)).show(logicContext, logicContext.getViewScreen(), true);
        return size;
    }

    /**
     * Determine Instruction Size.
     *
     * @return Returns the instruction size.
     */
    public int getSize() {
        return size;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[5];

        names[0] = "print.at.v";
        names[1] = "mv" + p1;
        names[2] = "mv" + p2;
        names[3] = "mv" + p3;

        if (size > 4) {
            names[4] = "mv" + p4;
        } else {
            names[4] = "0";
        }

        return names;
    }
//#endif DEBUG
}