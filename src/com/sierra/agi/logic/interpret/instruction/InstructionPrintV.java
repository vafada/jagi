/*
 * InstructionPrint.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.MessageBox;

import java.io.IOException;
import java.io.InputStream;

/**
 * Print Instruction.
 *
 * <P><CODE><B>print.n</B> Instruction 0x65</CODE><BR>
 * Opens a text window in the centre of the screen, where a message number
 * <CODE>p1</CODE> from the messages field of the current LOGIC resource is
 * displayed. Output mode is determined by f[15].
 * </P>
 *
 * <P><CODE><B>print.v</B> Instruction 0x66</CODE><BR>
 * Opens a text window in the centre of the screen, where a message number
 * <CODE>v[p1]</CODE> from the messages field of the current LOGIC resource is
 * displayed. Output mode is determined by f[15].
 * </P>
 *
 * <P>In addition to letters, digits, and other symbols, the string may contain:<BR>
 * Newline character (0x0A);<BR>
 * Format element:</P>
 * <UL>
 * <LI>%v<decimal number>: at this place the output will include a decimal value of variable with the given number.</LI>
 * <LI>%m <number>: the text of the message with the given number is inserted at this place.</LI>
 * <LI>%0 <number>: the name of the item with the given number is inserted at this place.</LI>
 * <LI>%w <number>: a vocabulary word with the given number is inserted at this place.</LI>
 * <LI>%s <number>: a string variable with the given number is inserted at this place.</LI>
 * <LI>%g <number>: a message with this number from message field of Logic(0) is inserted at this place.</LI>
 * </UL>
 * <p>
 * For %v, you can add a vertical line and a number of characters the output
 * should take. In this case leading zeros are not suppressed in the output.<BR>
 * Example: <CODE>%v34|2</CODE></P>
 *
 * <P>When you write your messages, remember that the interpreter wraps the
 * text between the lines as needed when the message is displayed.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionPrintV extends InstructionUni {
    /**
     * Creates new Print Instruction (V).
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionPrintV(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        short p = logicContext.getVar(p1);
        String message = logic.getMessageProcessed(p);
        (new MessageBox(logicContext.processMessage(message))).show(logicContext, logicContext.getViewScreen());
        return 2;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        String[] names = new String[2];

        names[0] = "print.v";
        names[1] = "mv" + p1;

        return names;
    }
//#endif DEBUG
}