/*
 * InstructionRestoreGame.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicVariables;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.LogicReturn;
import com.sierra.agi.save.RestoreGame;

import java.io.IOException;
import java.io.InputStream;

/**
 * Restore Game Instruction.
 *
 * <P><CODE><B>restore.game</B> Instruction 0x7e</CODE><BR>
 * These command restore the current state of the game from files.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionRestoreGame extends Instruction {
    /**
     * Creates new Restore Game Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionRestoreGame(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        RestoreGame restoreGame = new RestoreGame(logicContext);
        if (restoreGame.restore()) {
            // TODO: soundPlayer.Reset();
            logicContext.getMenuBar().enableAllMenuItem();
            // TODO: ReplayScriptEvents();
            logicContext.getViewTable().showPic();
            logicContext.newRoom(logicContext.getVar(LogicVariables.VAR_CURRENT_ROOM));
            logicContext.updateStatusLine();
            throw new LogicReturn();
        }
        return 1;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"restore.game"};
    }
//#endif DEBUG
}