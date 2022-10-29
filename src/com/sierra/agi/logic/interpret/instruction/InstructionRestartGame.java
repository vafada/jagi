/*
 * InstructionRestartGame.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.LogicReturn;

import java.io.IOException;
import java.io.InputStream;

import static com.sierra.agi.logic.LogicVariables.FLAG_RESTART_GAME;

/**
 * Restart Game Instruction.
 *
 * <P><CODE><B>restart.game</B> Instruction 0x80</CODE><BR>
 * Restarts the game from the very beginning.
 * </P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionRestartGame extends Instruction {
    /**
     * Creates new Restart Game Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionRestartGame(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
    }

    /**
     * Execute the Instruction.
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        //if (state.Flags[Defines.NO_PRMPT_RSTRT] || textGraphics.WindowPrint("Press ENTER to restart\nthe game.\n\nPress ESC to continue\nthis game."))
        //{
        // TODO: soundPlayer.Reset();
        logicContext.reset();
        logicContext.setFlag(FLAG_RESTART_GAME, true);
        logicContext.getMenuBar().enableAllMenuItem();
        logicContext.clearInput();
        logicContext.newRoom((short) 0);
        throw new LogicReturn();
        //}
        //return 1;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Instruction name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual names of the instruction.
     */
    public String[] getNames() {
        return new String[]{"restart.game"};
    }
//#endif DEBUG
}