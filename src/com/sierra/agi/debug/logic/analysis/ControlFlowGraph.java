package com.sierra.agi.debug.logic.analysis;

import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A ControlFlowGraph represents all paths that might be traversed through a sequence of
 * Instructions in a CallableCodeUnit. Nodes in the graph represents a BasicBlock, i.e.
 * a straight-line sequence of Instructions; the first Instruction in the sequence being
 * the only way for execution to enter the BasicBlock, and the last Instruction in the
 * sequence being the only way for execution to exit the BasicBlock. Edges are stored as
 * successors within each BasicBlock and represent possible transfer of control flow
 * from one BasicBlock to another.
 *
 * @author Lance Ewing
 */
public class ControlFlowGraph {

    /**
     * BasicBlocks that make up the control flow graph, keyed by address, and whose entries
     * are iterated over in address order.
     */
    private final TreeMap<Integer, BasicBlock> blocks;

    /**
     * The start block of this ControlFlowGraph.
     */
    private BasicBlock startBlock;

    /**
     * The graph's dominator tree, built on demand.
     */
    private DominatorTree dominatorTree;

    /**
     * Constructor for ControlFlowGraph.
     * <p>
     * Builds itself using the Instructions contained within the CallableCodeUnit.
     *
     * @param ccu The CallableCodeUnit of the method whose control flow graph is to be built.
     */
    public ControlFlowGraph(CallableCodeUnit ccu) {
        this.blocks = new TreeMap<Integer, BasicBlock>();

        BasicBlock currentBlock = null;
        BasicBlock previousBlock = null;

        // Iterate over every Instruction in the CallableCodeUnit in address order. A BasicBlock
        // starts at a leader Instruction and contains the set of all following Instructions 
        // until and not including the next leader.
        for (Entry<Integer, Instruction> instructionEntry : ccu.getInstructions().entrySet()) {
            Instruction instruction = instructionEntry.getValue();
            Integer instructionAddress = instructionEntry.getKey();

            // If the Instruction's address is a leader address, then we create
            // a new BasicBlock.
            if (ccu.getLeaderAddresses().contains(instructionAddress)) {
                previousBlock = currentBlock;
                currentBlock = getOrCreateBlock(instructionAddress);

                if (previousBlock != null) {
                    if (previousBlock.canFallThrough()) {
                        // Add an edge from the previous block to the current block, since
                        // control can fall through from that block to the current one.
                        previousBlock.addSuccessor(currentBlock);
                    }
                } else {
                    // No previous block, so it must be the start block.
                    this.startBlock = currentBlock;
                }
            }

            // Add the Instruction to the current BasicBlock.
            currentBlock.addInstruction(instruction);

            // If the Instruction is a branch, then we store the target BasicBlock as a successor.
            if (instruction instanceof InstructionMoving) {
                currentBlock.addSuccessor(getOrCreateBlock(((InstructionMoving) instruction).getAbsoluteGotoAddress()));
            }
        }
    }

    /**
     * Get the start block.
     *
     * @return the start block.
     */
    public BasicBlock getStartBlock() {
        return this.startBlock;
    }

    /**
     * Gets the BasicBlocks for this ControlFlowGraph in address order.
     *
     * @return The BasicBlocks for this ControlFlowGraph in address order.
     */
    public Iterable<BasicBlock> getBlocksInAddressOrder() {
        return this.blocks.values();
    }

    /**
     * Get an Iterable that will iterate over the blocks in the graph. in
     * depth-first preorder. This will traverse each edge in the graph once, but
     * may return the same block multiple times if multiple edges lead to it.
     */
    public Iterable<BasicBlock> blocksInControlFlowOrder() {
        return new Iterable<BasicBlock>() {
            @Override
            public Iterator<BasicBlock> iterator() {
                return new DepthFirstPreorderIterator(ControlFlowGraph.this);
            }
        };
    }

    /**
     * Gets the BasicBlock at a given address. Returns null if there isn't one.
     *
     * @param address The address to get the BasicBlock for.
     * @return The BasicBlock for the given address, or null if there isn't one.
     */
    public BasicBlock getBlock(int address) {
        return this.blocks.get(address);
    }

    /**
     * Returns the BasicBlock for the given address or creates it first if it doesn't
     * yet exist in the ControlFlowGraph. This should only be called for an address that
     * is known to be a BasicBlock leader address.
     *
     * @param address The address to get (or create) the BasicBlock for.
     * @return The BasicBlock for the given address.
     */
    private BasicBlock getOrCreateBlock(int address) {
        BasicBlock block = getBlock(address);
        if (block == null) {
            block = new BasicBlock(address);
            this.blocks.put(address, block);
        }
        return block;
    }

    /**
     * Gets the DominatorTree for this ControlFlowGraph.
     *
     * @return The graph's DominatorTree.
     */
    public DominatorTree getDominatorTree() {
        if (this.dominatorTree == null) {
            this.dominatorTree = new DominatorTree(this);
        }
        return this.dominatorTree;
    }
}
