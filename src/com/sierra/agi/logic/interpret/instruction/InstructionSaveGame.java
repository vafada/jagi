/*
 * InstructionSaveGame.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.save.ChooseRestoreGameBox;
import com.sierra.agi.save.ChooseSaveGameBox;
import com.sierra.agi.view.SavedGame;

import java.io.IOException;
import java.io.InputStream;

/**
 * Save Game Instruction.
 *
 * <P><CODE><B>save.game</B> Instruction 0x7d</CODE><BR>
 * These command save the current state of the game into disk files.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionSaveGame extends Instruction {
    /**
     * Creates new Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionSaveGame(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) {
        String path = logicContext.getCache().getPath().getAbsolutePath();
        ChooseSaveGameBox box = new ChooseSaveGameBox(logicContext.getGameID(), path);
        SavedGame chosenGame = box.show(logicContext, logicContext.getViewScreen());

        System.out.println("chosenGame = " + chosenGame);
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
        return new String[]{"save.game"};
    }
//#endif DEBUG
}