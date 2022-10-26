/*
 * InstructionNewRoom.java
 */

package com.sierra.agi.logic.interpret.instruction;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.jit.Compilable;
import com.sierra.agi.logic.interpret.jit.LogicCompileContext;
import com.sierra.jit.code.Scope;

import java.io.IOException;
import java.io.InputStream;

/**
 * New Room Instruction.
 *
 * <P>The <CODE>new.room</CODE> instruction is one of the most powerful
 * commands of the interpreter.</P>
 *
 * <P>It is used to change algorithms of the object behaviour, props, etc.
 * Automatic change of Ego coordinates imitates moving into a room adjacent to
 * the edge of the initial one.</P>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class InstructionNewRoom extends InstructionUni implements Compilable {
    /**
     * Creates a new New Room Instruction.
     *
     * @param context  Game context where this instance of the instruction will be used. (ignored)
     * @param stream   Logic Stream. Instruction must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this instruction. (ignored)
     * @param bytecode Bytecode of the current instruction.
     * @throws IOException I/O Exception are throw when <CODE>stream.read()</CODE> fails.
     */
    public InstructionNewRoom(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Execute the Instruction.
     *
     * <p>
     * Do the following:
     * <OL>
     * <LI>Commands stop.update and unanimate are issued to all objects;</LI>
     * <LI>All resources except Logic(0) are discarded;</LI>
     * <LI>Command player.control is issued;</LI>
     * <LI>unblock command is issued;</LI>
     * <LI>set.horizon(36) command is issued;</LI>
     * <LI>v1 is assigned the value of v0; v0 is assigned n (or the value of vn when the command is new.room.v); v4 is assigned 0; v5 is assigned 0; v16 is assigned the ID number of the VIEW resource that was associated with Ego (the player character).</LI>
     * <LI>Logic(i) resource is loaded where i is the value of v0 !</LI>
     * <LI>Set Ego coordinates according to v2:</LI>
     * <UL>
     * <LI>if Ego touched the bottom edge, put it on the horizon;</LI>
     * <LI>if Ego touched the top edge, put it on the bottom edge of the screen;</LI>
     * <LI>if Ego touched the right edge, put it at the left and vice versa.</LI>
     * </UL>
     * <LI>v2 is assigned 0 (meaning Ego has not touched any edges).</LI>
     * <LI>f5 is set to 1 (meaning in the first interpreter cycle after the new_room command all initialization parts of all logics loaded and called from the initialization part of the new room's logic will be called. In the subsequent cycle f5 is reset to 0.</LI>
     * <LI>Clear keyboard input buffer and return to the main AGI loop.</LI>
     * </OL>
     * </P>
     *
     * @param logic        Logic used to execute the instruction.
     * @param logicContext Logic Context used to execute the instruction.
     * @return Returns the number of byte of the uninterpreted instruction.
     */
    public int execute(Logic logic, LogicContext logicContext) throws Exception {
        logicContext.newRoom(p1);
        return 2;
    }

    /**
     * Compile the Instruction into Java Bytecode.
     *
     * @param compileContext Logic Compile Context.
     */
    public void compile(LogicCompileContext compileContext) {
        Scope scope = compileContext.scope;

        scope.addLoadVariable("logicContext");

        scope.addPushConstant(p1);

        scope.addInvokeSpecial("com.sierra.agi.logic.LogicContext", "newRoom", "(S)V");
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

        names[0] = "new.room";
        names[1] = Integer.toString(p1);

        return names;
    }
//#endif DEBUG
}