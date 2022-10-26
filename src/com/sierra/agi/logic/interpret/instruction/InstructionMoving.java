/*
 * InstructionMoving.java
 */

package com.sierra.agi.logic.interpret.instruction;

/**
 * Base Class for Instruction that have the ability to control the logics flows.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class InstructionMoving extends Instruction {
    /**
     * Relative Goto Position, i.e. the raw value exactly as read from the LOGIC.
     */
    protected short relativeGotoAddress;

    /**
     * Creates new Moving Instruction. Does absolutly nothing. Serve only has a
     * formal declaration.
     */
    protected InstructionMoving() {
    }

    /**
     * Retreive the relative goto address which is referenced by this instruction.
     *
     * @return Returns the relative goto address referenced by this instruction.
     */
    public int getRelativeGotoAddress() {
        return relativeGotoAddress;
    }

    /**
     * Returns the absolute goto address that is reference by this instruction.
     *
     * @return The absolute goto address that is referenced by this instruction.
     */
    public int getAbsoluteGotoAddress() {
        return (address + getSize() + relativeGotoAddress);
    }

    /**
     * Determine Destination Instruction Number
     *
     * @param instructionNum   Instruction number of the branching instruction.
     * @param instructionSizes Array containing the size in bytes of each Instruction in the Logic.
     */
    public int getDestination(int instructionNum, int[] instructionSizes) {
        int gotoFactor = getRelativeGotoAddress() + instructionSizes[instructionNum];

        if (gotoFactor > 0) {
            // If the jump is forward, scan forward from the size array.
            while (gotoFactor > 0) {
                gotoFactor -= instructionSizes[instructionNum++];
            }
        } else {
            // If the jump is backward, scan back through the size array.
            while (gotoFactor < 0) {
                gotoFactor += instructionSizes[--instructionNum];
            }
        }

        return instructionNum;
    }
}