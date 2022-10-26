/*
 * InstructionQuit.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.view.MessageBox;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * Quit Instruction.
 *
 * <P><CODE><B>quit</B> Instruction 0x86</CODE><BR>
 * Exits the interpreter. If <CODE>p1 = 1</CODE>, quits immediately.
 * If <CODE>n = 0</CODE>, asks "Press ENTER to quit. Press ESC to continue."
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionQuit extends Instruction {
    /**
     * Parameter #1
     */
    protected short p1;

    /**
     * Instruction Size
     */
    protected int size;

    /**
     * Creates new Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionQuit(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        if (engineEmulation > 0x2089) {
            p1 = (short) stream.read();
            size = 2;
        } else {
            p1 = (short) 1;
            size = 1;
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
        if (p1 == (short) 1) {
            System.exit(0);
        } else {
            MessageBox box = new MessageBox("Press ENTER to quit.\r\nPress ESC to keep playing.");
            KeyEvent ev;

            ev = box.show(logicContext, logicContext.getViewScreen(), true);

            if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
                System.exit(0);
            }
        }

        return size;
    }

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
        return new String[]{"quit", Integer.toString(p1)};
    }
//#endif DEBUG
}