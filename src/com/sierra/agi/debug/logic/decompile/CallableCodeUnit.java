package com.sierra.agi.debug.logic.decompile;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;

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
    private TreeMap<Integer, Instruction> instructions;
    
    /**
     * A Set containing the addresses of "Basic Block" leader addresses. These are determined
     * at the time that the Instructions are being added in to the CallableCodeUnit. It 
     * includes the entry address, explicit targets of conditional and unconditional branches,
     * and implicit targets of conditional branches.
     */
    private Set<Integer> leaderAddresses;
    
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
        
        int address = 0;
        
        for (Instruction instruction : instructions) {
            addInstruction(address, instruction);
            address += instruction.getSize();
        }
        
        
    }
    
    /**
     * Adds the given Instruction in to the CallableCodeUnit at its given address. It is 
     * assumed that Instructions are always added in address order, which is how the leader
     * addresses for the first Instruction and the implicit targets work. If they were 
     * added in a random sequence, this class wouldn't work.
     * 
     * @param address The address (or offset) of the Instruction within the Logic.
     * @param instruction The Instruction add. It's address is contained within it.
     */
    private void addInstruction(int address, Instruction instruction) {
        // Get the last Instruction if there was one. We use this when determining whether
        // the Instruction is a leader Instruction.
        Instruction lastInstruction = null;
        if (!this.instructions.isEmpty()) {
            lastInstruction = this.instructions.lastEntry().getValue();
        }
        
        // Add the Instruction to this CallableCodeUnit's Map of Instructions.
        this.instructions.put(address, instruction);
        
        // We identify the leader addresses while we're at it. There are 3 types of leader Instruction:
        //
        // - The first instruction is a leader.
        // - The target of a conditional or an unconditional goto/jump instruction is a leader.
        // - The instruction that immediately follows a conditional or an unconditional goto/jump instruction is a leader.
            
        if ((lastInstruction == null) || (lastInstruction instanceof InstructionMoving)) {
            // If this is the first Instruction, or an Instruction immediately following a branch,
            // then add the address to the leader addresses. It is starting a new block.
            this.leaderAddresses.add(address);
            
        }
        if (instruction instanceof InstructionMoving) {
            // If this instruction is a branch (either conditional bnt/bt or non-conditional jmp)
            // then add it's target to the Set of leader addresses for this CallableCodeUnit.
            this.leaderAddresses.add(((InstructionMoving)instruction).getAddress());
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
    public void buildControlFlowGraph() {
        this.controlFlowGraph = new ControlFlowGraph(this);
    }
    
    /**
     * Performs control flow analysis on this CallableCodeUnit's ControlFlowGraph.
     */
    public void performControlFlowAnalysis() {
        // If the ControlFlowGraph hasn't been built yet, force it now.
        ControlFlowGraph controlFlowGraph = getControlFlowGraph();
        
        // Then get the DominatorTree of the graph, which we'll use to identify things like loops.
        DominatorTree dominatorTree = controlFlowGraph.getDominatorTree();
        
        // Iterate over all BasicBlocks in the control flow graph...
        for (BasicBlock basicBlock : controlFlowGraph.getBlocksInAddressOrder()) {
            
            
            
            
        }
    }
    
    public String toString() {
        StringBuilder str = new StringBuilder();
        
        if (this.controlFlowGraph != null) {
            for (BasicBlock block : this.controlFlowGraph.getBlocksInAddressOrder()) {
                //str.append(String.format("|%04X", block.getStartAddress()));
                str.append("\n");
                
                Collection<Instruction> instructions = block.getInstructions();
                
                for (Instruction instruction : instructions) {
                    str.append("        ");
                    str.append(instruction.toString());
                    str.append("\n");
                }
            }
        } else {
            for (Instruction instruction : this.instructions.values()) {
                str.append("        ");
                str.append(instruction.toString());
                str.append("\n");
            }
        }
        
        return str.toString();
    }
}
