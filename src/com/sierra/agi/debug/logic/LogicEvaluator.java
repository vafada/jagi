/*
 *  LogicEvaluator.java
 *  Adventure Game Interpreter Debug Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug.logic;

import com.sierra.agi.inv.InventoryObject;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.interpret.LogicInterpreter;
import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionGoto;
import com.sierra.agi.logic.interpret.instruction.InstructionIf;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.word.Word;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A class that evaluates the test and action commands in a LOGIC for the purpose
 * of generating a decompiled script.
 *
 * @author Dr. Z.
 */
public class LogicEvaluator {
    /**
     * Currently not used, and purpose unknown.
     */
    protected Vector listeners = new Vector();

    /**
     * Holds mappings between variable/flag/aniobj numbers and a more readable name.
     */
    protected Properties tokenMappings;

    /**
     * The ResourceCache from where to get things like word and inv item text.
     */
    protected ResourceCache resourceCache;

    /**
     * Constructor for LogicEvaluator.
     *
     * @param resourceCache The ResourceCache from where to get things like word and inv item text.
     */
    public LogicEvaluator(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
        this.tokenMappings = loadTokenMappings();
    }

    public void addLogicEvaluatorListener(LogicEvaluatorListener listener) {
        listeners.add(listener);
    }

    public void removeLogicEvaluatorListener(LogicEvaluatorListener listener) {
        listeners.remove(listener);
    }

    protected void evaluateExpression(Logic logic, int ip, InstructionIf instructionIf, String condition, StringBuffer buffer) {
        StringTokenizer tokenizer = new StringTokenizer(condition, " ,()", true);
        String s;

        buffer.append(" (");

        while (tokenizer.hasMoreTokens()) {
            buffer.append(evaluateToken(logic, tokenizer.nextToken()));
        }

        buffer.append(")");

        if (ip >= 0) {
            buffer.append(" (else goto ");
            buffer.append(Integer.toHexString(ip + instructionIf.getRelativeGotoAddress() + instructionIf.getSize()));
            buffer.append(")");
        }
    }

    protected String evaluateExpression(Logic logic, int ip, InstructionIf instructionIf) {
        StringBuffer buffer = new StringBuffer();

        evaluateExpression(logic, ip, instructionIf, instructionIf.getNames()[2], buffer);
        return buffer.toString();
    }

    protected String evaluateInstruction(Logic logic, int ip, Instruction instruction) {
        String[] names = instruction.getNames();
        StringBuffer buffer = new StringBuffer();

        if (instruction instanceof InstructionIf) {
            buffer.append(names[0]);
            evaluateExpression(logic, ip, (InstructionIf) instruction, names[2], buffer);
        } else if (instruction instanceof InstructionGoto) {
            buffer.append(names[0]);
            buffer.append(Integer.toHexString(ip + ((InstructionGoto) instruction).getRelativeGotoAddress() + instruction.getSize()));
        } else {
            StringTokenizer tokenizer = new StringTokenizer(instruction.toString(), " ,()", true);
            while (tokenizer.hasMoreTokens()) {
                buffer.append(evaluateToken(logic, tokenizer.nextToken()));
            }
        }

        return buffer.toString();
    }

    /**
     * Loads from a conf file the mappings to be used when converting numeric parameter
     * tokens in to a more readable form. For things like words, inventory items, and
     * messages, there are resources from which we can read to lookup the appropriate
     * data to show for the parameter, but for variables, flags, and animated objects,
     * there is no such data in the game itself that tells us what they originally
     * were.
     * <p>
     * We're lucky that recently Sierra's original AGI documentation turned up, so
     * the original names of some of the "system" variables and flags can be loaded
     * from a mapping file. That is essentially what this mention does. But it needs
     * to support muliple AGI interpreter versions. The flag and variable usage was
     * different in AGI v1 when compared with AGI v3. So for this reason, the loading
     * of the mapping file is done in a way that uses the game's interpreter version
     * to look for a version specific conf file prior to falling back on the common
     * case.
     *
     * @return A Properties object containing the loaded token mappings.
     */
    protected Properties loadTokenMappings() {
        Properties props = new Properties();

        try {
            // Use the engineEmulation value (i.e. the interpreter version) to see if 
            // there is a version specific conf file for flag, variable and view object names.
            InputStream is = null;
            String versionStr = String.format("%04X", resourceCache.getResourceProvider().getConfiguration().engineEmulation);
            for (int i = versionStr.length(); i >= 0 && is == null; i--) {
                String suffix = versionStr.substring(0, i);
                suffix = ((suffix.length() > 0 ? "_" : "") + suffix);
                is = getClass().getResourceAsStream("LogicEvaluator" + suffix + ".conf");
            }

            props.load(is);

            // Remove any mappings for blank values. This allows for the conf file to
            // have a placeholder key without yet knowing what the value should be.
            List<String> propsToRemove = new ArrayList<String>();
            for (Object propName : props.keySet()) {
                String propValue = (String) props.get(propName);
                if (propValue.trim().isEmpty()) {
                    // Need to store these in a separate collection so as to avoid a
                    // concurrent modification exception. We remove in a separate loop.
                    propsToRemove.add((String) propName);
                }
            }
            for (String propName : propsToRemove) {
                props.remove(propName);
            }

        } catch (IOException ioex) {
        }

        return props;
    }

    /**
     * @param logic
     * @param s
     * @return
     */
    public String evaluateToken(Logic logic, String s) {
        int i;
        String t;

        for (i = s.length() - 1; i >= 0; i--) {
            switch (s.charAt(i)) {
                // Variables, flags, and animated objects.
                case 'v':
                case 'f':
                case 'o':
                    if (i > 0) {
                        t = s.substring(0, i);
                        s = s.substring(i);
                    } else {
                        t = "";
                    }

                    s = tokenMappings.getProperty(s, s);
                    return t + s;

                // Inventory items.
                case 'i':
                    try {
                        t = s.substring(i + 1);
                        i = Integer.parseInt(t);

                        InventoryObject item = resourceCache.getObjects().getObject((short) i);
                        if (item != null) {
                            return item.getName();
                        } else {
                            return s;
                        }
                    } catch (IOException e) {
                        return s;
                    } catch (ResourceException e) {
                        return s;
                    } catch (NumberFormatException e) {
                        return s;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        return s;
                    }

                    // Words.
                case 'w':
                    try {
                        t = s.substring(i + 1);
                        i = Integer.parseInt(t);

                        Word word = resourceCache.getWords().getWordByNumber(i);
                        if (word != null) {
                            return word.text;
                        } else {
                            return s;
                        }
                    } catch (ResourceException e) {
                        return s;
                    } catch (IOException e) {
                        return s;
                    } catch (NumberFormatException e) {
                        return s;
                    }

                    // Messages.
                case 'm':
                    try {
                        t = s.substring(i + 1);
                        i = Integer.parseInt(t);

                        return "\"" + logic.getMessageProcessed(i) + "\"";
                    } catch (NumberFormatException nfex) {
                        return s;
                    } catch (ArrayIndexOutOfBoundsException aiobe) {
                        return s;
                    }
            }
        }

        return s;
    }

    /**
     * Decompiles the given Logic in to a text representation held within a Vector
     * of LogicLine instances, where each line is a line of the logic source code.
     *
     * @param logicInterpreter The Logic to decompile, containing the raw bytecode Instructions.
     * @return The decompiled Logic as a Vector of LogicLines.
     */
    public Vector<LogicLine> decompile(LogicInterpreter logicInterpreter) {
        Vector<LogicLine> result = new Vector<LogicLine>();
        Instruction[] instructions = logicInterpreter.getInstructions();

        result.add(new LogicLine(0, "void logic" + logicInterpreter.getLogicNumber() + "()"));
        result.add(new LogicLine(0, "{"));

        //decompile(logicInterpreter, instructions, logicInterpreter.getInstructionSizes(), 1, 0, instructions.length, result);
        decompile(logicInterpreter, instructions, result);

        result.add(new LogicLine(0, "}"));

        String[] messages = logicInterpreter.getMessages();

        if (messages != null) {
            result.add(new LogicLine(0, ""));
            result.add(new LogicLine(0, "Messages"));
            for (int i = 0; i < messages.length; i++) {
                result.add(new LogicLine(0, "Message #" + i + ": " + messages[i]));
            }
        }

        return result;
    }

    /**
     * Decompiles the given Instruction array in to a Vector of LogicLines. The LogicLine
     * instances represent lines in the generated source code. They are structured in such
     * a way to support the debugging features of JAGI, such as stepping through the code.
     *
     * @param logic        The Logic that is being decompiled. We need it mainly for the messages.
     * @param instructions The array of Instructions from the Logic to decompile.
     * @param result       The Vector to add the decompiled LogicLines to.
     */
    protected void decompile(Logic logic, Instruction[] instructions, Vector<LogicLine> result) {
        // Temporary Vector to hold the LogicLines prior to merging with the label lines.
        Vector<LogicLine> lines = new Vector<LogicLine>();

        // Stores the address to label name mappings for any "goto" destinations identified.
        HashMap<Integer, String> labels = new HashMap<Integer, String>();

        // Keeps track of the current active moving/branch Instruction, if there is one. 
        Stack<InstructionMoving> branchStack = new Stack<InstructionMoving>();

        // For the main Instruction decompile iteration, we do it by index, so that we have the 
        // instruction number required by the LogicLine class. This is used by the debugger tools.
        for (int instructionNum = 0; instructionNum < instructions.length; instructionNum++) {
            Instruction instruction = instructions[instructionNum];

            // Close any open branch blocks that have reached their end.
            while (!branchStack.isEmpty() && (branchStack.peek().getAbsoluteGotoAddress() <= instruction.getAddress())) {
                branchStack.pop();
                lines.add(new LogicLine(branchStack.size() + 1, "}"));
            }

            // Our new indent size is the number of branch blocks left in the stack.
            int indent = branchStack.size() + 1;

            if (instruction instanceof InstructionIf) {
                // If it's an "if", add it, along with the contained condition, as a LogicLine.
                lines.add(new LogicLine(indent, instructionNum, instruction, "if" + evaluateExpression(logic, -1, (InstructionIf) instruction)));
                lines.add(new LogicLine(indent, "{"));

                // And push the InstructionIf on to the branch stack since it is now active.
                branchStack.push((InstructionMoving) instruction);
            } else if (instruction instanceof InstructionGoto) {
                int destAddress = ((InstructionGoto) instruction).getAbsoluteGotoAddress();

                // Build a Label String for this destination address. We may not need it though.
                String label = (labels.get(destAddress) != null ? labels.get(destAddress) : "Label" + (labels.size() + 1));

                if (destAddress < instruction.getAddress()) {
                    // If the destination address is less than the current address, then it is 
                    // always a "goto" keyword. AGI doesn't support "while", "do..while", "for",
                    // or any other loop keywords. Basic looping is handled solely with "goto".
                    lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                    labels.put(destAddress, label);

                } else {
                    if (branchStack.isEmpty()) {
                        // If we're at the top level, then it is always a "goto" keyword.
                        lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                        labels.put(destAddress, label);

                    } else {
                        // If we're not at the top level, then what is the currently active branch instruction?
                        InstructionMoving activeBranchBlock = branchStack.peek();

                        if (activeBranchBlock instanceof InstructionIf) {
                            if (activeBranchBlock.getAbsoluteGotoAddress() == instruction.getNextInstructionAddress()) {
                                // The InstructionGoto is the last instruction within the "if" block.
                                if (activeBranchBlock.getNextInstructionAddress() == instruction.getAddress()) {
                                    // If the InstructionGoto is also the first Instruction in the "if" block, then
                                    // it is almost certainly a "goto" keyword (since a blank "if" block is unlikely).
                                    lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                                    labels.put(destAddress, label);

                                } else {
                                    // Otherwise it is possible, and in most times probable, that this is an "else". It may 
                                    // not always be so though. We need to check that none of the other Instructions on the
                                    // branch stack will end before we get to the destination address.
                                    boolean elseTooBig = false;
                                    for (InstructionMoving branchInstruction : branchStack) {
                                        if (branchInstruction != activeBranchBlock) {
                                            if (branchInstruction.getAbsoluteGotoAddress() < destAddress) {
                                                elseTooBig = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (elseTooBig) {
                                        // The destination jumps beyond the extent of a branch instruction on the branch
                                        // stack. It can't be an "else" then.
                                        lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                                        labels.put(destAddress, label);

                                    } else {
                                        // If we didn't find any, then there is no harm assuming it is an "else". It is 
                                        // still possible that the original source had a goto, but the "else" will be equivalent.

                                        // Close the "if" before adding the "else".
                                        branchStack.pop();

                                        lines.add(new LogicLine(indent - 1, "}"));
                                        lines.add(new LogicLine(indent - 1, instructionNum, instruction, "else"));
                                        lines.add(new LogicLine(indent - 1, "{"));

                                        // Push the InstructionGoto (aka "else") on to the branch stack since it is now active.
                                        branchStack.push((InstructionMoving) instruction);
                                    }
                                }
                            } else {
                                // If the InstructionGoto is within an "if" block but is not the last instruction
                                // in that "if" block, then it must be a "goto" keyword.
                                lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                                labels.put(destAddress, label);
                            }
                        } else {
                            // If the currently active branch block is an "else", then an InstructionGoto nested within
                            // it must be a "goto" keyword.
                            lines.add(new LogicLine(indent, instructionNum, instruction, "goto " + label + ";"));
                            labels.put(destAddress, label);
                        }
                    }
                }
            } else {
                // Normal instruction.
                lines.add(new LogicLine(indent, instructionNum, instruction, evaluateInstruction(logic, -1, instruction) + ";"));
            }
        }

        // Add the created LogicLines to the output Vector, including the identified Labels in the 
        // positions where they should be.
        for (LogicLine logicLine : lines) {
            if ((logicLine.instruction != null) && (labels.containsKey(logicLine.instruction.getAddress()))) {
                result.add(new LogicLine(logicLine.level, labels.get(logicLine.instruction.getAddress()) + ":"));
            }
            result.add(logicLine);
        }
    }

    /**
     * (NOT currently used).
     * <p>
     * This was the original decompile method in JAGI, which attempted to recognise different
     * types of loops, such as while, and do..while. We've now learnt though that the original
     * AGI syntax did not support such keywords. Instead they used a goto for loops.
     * <p>
     * if (...) ...
     * if (...) ... else ...
     * for (...; ...)
     * while (...) ...
     * do ... while(...)
     *
     * @param logic            The Logic to decompile.
     * @param instructions     The Instruction array for the Logic.
     * @param instructionSizes The sizes of each Instruction in the instructions array.
     * @param level            The nested level.
     * @param start            The starting index within the instructions array to use.
     * @param end              The ending index within the instructions array to use.
     * @param result           The Vector in to which to put the decompiled LogicLine instances.
     */
    protected int decompile(Logic logic, Instruction[] instructions, int[] instructionSizes, int level, int start, int end, Vector<LogicLine> result) {
        int in, destination;
        Instruction instruction;
        Instruction firstInstruction;
        int firstInstructionDestination;
        Instruction lastInstruction;
        int lastInstructionDestination;
        String line;
        StringBuffer buffer;

        if (start == end) {
            return 0;
        }

        firstInstruction = instructions[start];
        lastInstruction = instructions[end - 1];

        if (lastInstruction instanceof InstructionMoving) {
            lastInstructionDestination = ((InstructionMoving) lastInstruction).getDestination(end - 1, instructionSizes);
        } else {
            lastInstructionDestination = -1;
        }

        if (firstInstruction instanceof InstructionMoving) {
            firstInstructionDestination = ((InstructionMoving) firstInstruction).getDestination(start, instructionSizes);
        } else {
            firstInstructionDestination = -1;
        }

        // Scan for Patterns.
        if (lastInstructionDestination == start) {
            if (lastInstruction instanceof InstructionGoto) {
                // it's a "while (...) ..." loop.
                if (firstInstruction instanceof InstructionIf) {
                    line = evaluateExpression(logic, -1, (InstructionIf) firstInstruction);
                } else {
                    line = "(true)";
                }

                result.add(new LogicLine(level, start, firstInstruction, "while " + line));
                result.add(new LogicLine(level, "{"));
                decompile(logic, instructions, instructionSizes, level + 1, start + 1, end - 1, result);
                result.add(new LogicLine(level, "}"));
                return 0;
            } else {
                // it's a "do ... while(...)" loop.
                result.add(new LogicLine(level, "do"));
                result.add(new LogicLine(level, "{"));

                decompile(logic, instructions, instructionSizes, level + 1, start, end - 1, result);

                if (firstInstruction instanceof InstructionIf) {
                    line = evaluateExpression(logic, -1, (InstructionIf) lastInstruction);
                } else {
                    line = "(true)";
                }

                result.add(new LogicLine(level, "}"));
                result.add(new LogicLine(level, start, firstInstruction, "while " + line + ";"));
                return 0;
            }
        } else if (firstInstruction instanceof InstructionIf) {
            if (firstInstructionDestination == end) {
                // It's a if.
                line = evaluateExpression(logic, -1, (InstructionIf) firstInstruction);
                result.add(new LogicLine(level, start, firstInstruction, "if " + line));
                result.add(new LogicLine(level, "{"));

                if (lastInstruction instanceof InstructionGoto) {
                    decompile(logic, instructions, instructionSizes, level + 1, start + 1, end - 1, result);
                } else {
                    decompile(logic, instructions, instructionSizes, level + 1, start + 1, end, result);
                }

                result.add(new LogicLine(level, "}"));
                return 0;
            }
        }

        // Scan for Backward Jump.
        for (in = end - 1; in >= start; in--) {
            instruction = instructions[in];

            if (instruction instanceof InstructionMoving) {
                destination = ((InstructionMoving) instruction).getDestination(in, instructionSizes);

                if (destination < in) {
                    // Backward Jump... Probably a loop.

                    if (destination != start) {
                        // Decompile the start seperatly.
                        decompile(logic, instructions, instructionSizes, level, start, destination, result);
                    }

                    decompile(logic, instructions, instructionSizes, level, destination, in + 1, result);

                    if (in != end) {
                        // Decompile the end seperatly.
                        decompile(logic, instructions, instructionSizes, level, in + 1, end, result);
                    }

                    return 0;
                }
            }
        }

        // Scan for Forward Jump.
        for (in = start; in < end; in++) {
            instruction = instructions[in];

            if (instruction instanceof InstructionMoving) {
                destination = ((InstructionMoving) instruction).getDestination(in, instructionSizes);

                if (destination >= in) {
                    // Forward Jump... Probably a if.

                    if (destination != start) {
                        // Decompile the start seperatly.
                        decompile(logic, instructions, instructionSizes, level, start, in, result);
                    }

                    decompile(logic, instructions, instructionSizes, level, in, destination, result);

                    lastInstruction = instructions[destination - 1];

                    if (lastInstruction instanceof InstructionGoto) {
                        // It's a "if ... else ..."
                        lastInstructionDestination = ((InstructionGoto) lastInstruction).getDestination(destination - 1, instructionSizes);

                        if (lastInstructionDestination > destination) {
                            result.add(new LogicLine(level, destination - 1, lastInstruction, "else"));
                            result.add(new LogicLine(level, "{"));

                            decompile(logic, instructions, instructionSizes, level + 1, destination, lastInstructionDestination, result);
                            destination = lastInstructionDestination;

                            result.add(new LogicLine(level, "}"));
                        }
                    }

                    if (in != end) {
                        // Decompile the end seperatly.
                        decompile(logic, instructions, instructionSizes, level, destination, end, result);
                    }

                    return 0;
                }
            }
        }

        for (in = start; in < end; in++) {
            result.add(new LogicLine(level, in, instructions[in], evaluateInstruction(logic, -1, instructions[in]) + ";"));
        }

        return 0;
    }

    protected boolean hasJumpOutsideArea(Instruction[] instructions, int[] sizes, int start, int end) {
        int in, address;
        Instruction instruction;

        for (in = start; in < end; in++) {
            instruction = instructions[in];

            if (instruction instanceof InstructionMoving) {
                address = ((InstructionMoving) instructions[in]).getDestination(in, sizes);

                if ((address > end) || (address < start)) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getVariableTokenMappings(int index) {
        String retVal = tokenMappings.getProperty("v" + index, String.valueOf(index));

        if (retVal.charAt(0) == 'v') {
            retVal = retVal.substring(1);
        }

        return retVal;
    }

    public String getFlagTokenMappings(int index) {
        String retVal = tokenMappings.getProperty("f" + index, String.valueOf(index));

        if (retVal.charAt(0) == 'f') {
            retVal = retVal.substring(1);
        }

        return retVal;
    }
}
