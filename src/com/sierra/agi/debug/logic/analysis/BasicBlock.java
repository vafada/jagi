package com.sierra.agi.debug.logic.analysis;

import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionGoto;
import com.sierra.agi.logic.interpret.instruction.InstructionReturn;

import java.util.*;

/**
 * A basic block is a portion of the code within a program with only one entry point
 * and only one exit point. Basic blocks form the vertices or nodes in a control flow
 * graph.
 *
 * @author Lance Ewing
 */
public class BasicBlock {

    /**
     * The start address for this BasicBlock (i.e. address of the leader Instruction).
     */
    private final int startAddress;

    /**
     * Instructions in this BasicBlock.
     */
    private final ArrayList<Instruction> instructions;

    /**
     * Successors to this BasicBlock.
     */
    private final Set<BasicBlock> successors;

    /**
     * Constructor for BasicBlock.
     *
     * @param address The BasicBlock's entry address.
     */
    public BasicBlock(int address) {
        this.startAddress = address;
        this.instructions = new ArrayList<Instruction>();
        this.successors = new HashSet<BasicBlock>();
    }

    /**
     * Adds an Instruction to this BasicBlock.
     *
     * @param instruction The Instruction to add to the BasicBlock.
     */
    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }

    /**
     * Add a successor to this block. This creates an edge to the successor block.
     *
     * @param succ The successor block.
     */
    public void addSuccessor(BasicBlock succ) {
        this.successors.add(succ);
    }

    /**
     * Get this block's successors; this defines the set of edges in the flow graph.
     *
     * @return this block's successors in the control-flow graph.
     */
    public Collection<? extends BasicBlock> getSuccessors() {
        return this.successors;
    }

    /**
     * Get the contents of the block.
     *
     * @return this block's instructions as a mutable List.
     */
    public List<Instruction> getInstructions() {
        return this.instructions;
    }

    /**
     * Gets the first Instruction in this BasicBlock.
     *
     * @return The first Instruction in this BasicBlock.
     */
    public Instruction getFirstInstruction() {
        return this.instructions.get(0);
    }

    /**
     * Gets the last Instruction in this BasicBlock.
     *
     * @return The last Instruction in this BasicBlock.
     */
    public Instruction getLastInstruction() {
        return this.instructions.get(this.instructions.size() - 1);
    }

    /**
     * Gets the starting address of this BasicBlock.
     *
     * @return The starting address of this BasicBlock.
     */
    public int getStartAddress() {
        return this.startAddress;
    }

    /**
     * How big is this block?
     *
     * @return The number of Instructions in this BasicBlock.
     */
    public int size() {
        return this.instructions.size();
    }

    /**
     * Fetch the Instruction at the specified position.
     *
     * @param position The Instruction's position.
     * @return the Instruction at that position.
     */
    public Instruction get(int position) {
        return this.instructions.get(position);
    }

    /**
     * Determines if control can fall through the end of the Block. Returns, and
     * unconditional branches will not fall through, but conditional branches and
     * all other instructions will fall through.
     *
     * @return true if control can fall through this Block (i.e. doesn't unconditionally transfer control)
     */
    public boolean canFallThrough() {
        // Get the last Instruction if there was one.
        if (!this.instructions.isEmpty()) {
            Instruction lastInstruction = this.instructions.get(this.instructions.size() - 1);
            // It's an unconditional transfer of control, so no fall through.
            // In all other cases, it can fall through.
            return (!(lastInstruction instanceof InstructionReturn)) && (!(lastInstruction instanceof InstructionGoto));
        } else {
            // No Instructions, so it falls through.
            return true;
        }
    }
}
