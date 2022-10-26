package com.sierra.agi.debug.logic.analysis;

import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents the more abstract concept of a Logic script being simply a unit of code,
 * containing Instructions, that can be called.
 *
 * @author Lance Ewing
 */
public class CallableCodeUnit {

    /**
     * A Map containing the decoded Instructions that make up this CallableCodeUnit.
     */
    private final TreeMap<Integer, Instruction> instructions;

    /**
     * A Set containing the addresses of "Basic Block" leader addresses. These are determined
     * at the time that the Instructions are being added in to the CallableCodeUnit. It
     * includes the entry address, explicit targets of conditional and unconditional branches,
     * and implicit targets of conditional branches.
     */
    private final Set<Integer> leaderAddresses;

    /**
     * The ControlFlowGraph for this CallableCodeUnit.
     */
    private ControlFlowGraph controlFlowGraph;

    /**
     * Constructor for CallableCode Unit.
     *
     * @param instructions
     */
    public CallableCodeUnit(Instruction[] instructions) {
        this.instructions = new TreeMap<Integer, Instruction>();
        this.leaderAddresses = new TreeSet<Integer>();

        for (Instruction instruction : instructions) {
            addInstruction(instruction);
        }
    }

    /**
     * Adds the given Instruction in to the CallableCodeUnit at its given address. It is
     * assumed that Instructions are always added in address order, which is how the leader
     * addresses for the first Instruction and the implicit targets work. If they were
     * added in a random sequence, this class wouldn't work.
     *
     * @param instruction The Instruction add. It's address is contained within it.
     */
    private void addInstruction(Instruction instruction) {
        // Get the last Instruction if there was one. We use this when determining whether
        // the Instruction is a leader Instruction.
        Instruction lastInstruction = null;
        if (!this.instructions.isEmpty()) {
            lastInstruction = this.instructions.lastEntry().getValue();
        }

        // Add the Instruction to this CallableCodeUnit's Map of Instructions.
        this.instructions.put(instruction.getAddress(), instruction);

        // We identify the leader addresses while we're at it. There are 3 types of leader Instruction:
        //
        // - The first instruction is a leader.
        // - The target of a conditional or an unconditional goto/jump instruction is a leader.
        // - The instruction that immediately follows a conditional or an unconditional goto/jump instruction is a leader.

        if ((lastInstruction == null) || (lastInstruction instanceof InstructionMoving)) {
            // If this is the first Instruction, or an Instruction immediately following a branch,
            // then add the address to the leader addresses. It is starting a new block.
            this.leaderAddresses.add(instruction.getAddress());

        }
        if (instruction instanceof InstructionMoving) {
            // If this instruction is a branch (either conditional bnt/bt or non-conditional jmp)
            // then add it's target to the Set of leader addresses for this CallableCodeUnit.
            this.leaderAddresses.add(((InstructionMoving) instruction).getAbsoluteGotoAddress());
        }
    }

    /**
     * Gets a Map containing the Instructions that make up this CallableCodeUnit,
     * where the key is the address of the Instruction.
     *
     * @return A Map containing the Instructions in this CallableCodeUnit.
     */
    public Map<Integer, Instruction> getInstructions() {
        return this.instructions;
    }

    /**
     * Gets the Set of leader addresses within this CallableCodeUnit, i.e. entry address
     * and addresses that are either explicit or implicit targets for conditional or
     * non-conditional branches.
     *
     * @return Set of leader addresses for this CallableCodeUnit.
     */
    public Set<Integer> getLeaderAddresses() {
        return leaderAddresses;
    }

    /**
     * Gets the ControlFlowGraph for this CallableCodeUnit, building it if it hasn't yet
     * been built.
     *
     * @return The ControlFlowGraph for this CallableCodeUnit.
     */
    public ControlFlowGraph getControlFlowGraph() {
        if (this.controlFlowGraph == null) {
            buildControlFlowGraph();
        }
        return controlFlowGraph;
    }

    /**
     * Builds (or rebuilds) the ControlFlowGraph for this CallableCodeUnit.
     */
    private void buildControlFlowGraph() {
        this.controlFlowGraph = new ControlFlowGraph(this);
    }

    /**
     * Generates a String containing DOT language syntax for drawing the control flow
     * graph and the dominator tree for this CallableCodeUnit.
     *
     * @return The generated DOT syntax to draw the control flow graph and dominator tree.
     */
    public String toDotString() {
        StringBuilder str = new StringBuilder();

        if (this.controlFlowGraph != null) {
            str.append("digraph controlFlowGraph {\n");

            for (BasicBlock block : this.controlFlowGraph.getBlocksInAddressOrder()) {
                for (BasicBlock successor : block.getSuccessors()) {
                    str.append(String.format("  n_%04x -> n_%04x [color=red];\n", block.getStartAddress(), successor.getStartAddress()));
                    str.append(String.format("  n_%04x [label =\"%04x:\\n", block.getStartAddress(), block.getStartAddress()));

                    for (Instruction instruction : block.getInstructions()) {
                        str.append(instruction.toString());
                        str.append("\\n");
                    }

                    str.append("\\n\"] [color=red];\n");
                }
            }

            str.append("}\n");

            str.append("digraph dominatorTree {\n");

            DominatorTree dominatorTree = controlFlowGraph.getDominatorTree();

            for (BasicBlock block : dominatorTree.getDominatorTree().keySet()) {
                for (BasicBlock child : dominatorTree.getDominatorTree().get(block)) {
                    str.append(String.format("  n_%04x -> n_%04x [color=red];\n", block.getStartAddress(), child.getStartAddress()));
                }
            }
            for (BasicBlock block : this.controlFlowGraph.getBlocksInAddressOrder()) {
                str.append(String.format("  n_%04x [label =\"%04x:\\n", block.getStartAddress(), block.getStartAddress()));

                for (Instruction instruction : block.getInstructions()) {
                    str.append(instruction.toString());
                    str.append("\\n");
                }

                str.append("\\n\"] [color=red];\n");
            }

            str.append("}\n");

        }

        return str.toString();
    }
}
