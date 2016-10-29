/*
 *  LogicEvaluator.java
 *  Adventure Game Interpreter Debug Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug.logic;

import java.io.*;
import java.util.*;

import com.sierra.agi.inv.InventoryObject;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.interpret.LogicInterpreter;
import com.sierra.agi.logic.interpret.instruction.Instruction;
import com.sierra.agi.logic.interpret.instruction.InstructionIf;
import com.sierra.agi.logic.interpret.instruction.InstructionGoto;
import com.sierra.agi.logic.interpret.instruction.InstructionMoving;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.word.Word;

public class LogicEvaluator extends Object
{
    protected Vector listeners = new Vector();
    protected Properties props;
    protected ResourceCache cache;
    
    public LogicEvaluator(ResourceCache cache)
    {
        this.cache = cache;
        this.props = createDefault();
    }
    
    public void addLogicEvaluatorListener(LogicEvaluatorListener listener)
    {
        listeners.add(listener);
    }

    public void removeLogicEvaluatorListener(LogicEvaluatorListener listener)
    {
        listeners.remove(listener);
    }

    protected void evaluateExpression(Logic logic, int ip, InstructionIf instructionIf, String condition, StringBuffer buffer)
    {
        StringTokenizer tokenizer = new StringTokenizer(condition, " ,()", true);
        String          s;
            
        buffer.append(" (");
        
        while (tokenizer.hasMoreTokens())
        {
            buffer.append(evaluateToken(logic, tokenizer.nextToken()));
        }
            
        buffer.append(")");
        
        if (ip >= 0)
        {
            buffer.append(" (else goto ");
            buffer.append(Integer.toHexString(ip + instructionIf.getAddress() + instructionIf.getSize()));
            buffer.append(")");
        }
    }
    
    protected String evaluateExpression(Logic logic, int ip, InstructionIf instructionIf)
    {
        StringBuffer buffer = new StringBuffer();
        
        evaluateExpression(logic, ip, instructionIf, instructionIf.getNames()[2], buffer);
        return buffer.toString();
    }

    public String evaluateInstruction(Logic logic, int ip, Instruction instruction)
    {
        String[] names = instruction.getNames();
        StringBuffer buffer = new StringBuffer();
        
        if (instruction instanceof InstructionIf)
        {
            buffer.append(names[0]);
            evaluateExpression(logic, ip, (InstructionIf)instruction, names[2], buffer);
        }
        else if (instruction instanceof InstructionGoto)
        {
            buffer.append(names[0]);
            buffer.append(Integer.toHexString(ip + ((InstructionGoto)instruction).getAddress() + instruction.getSize()));
        }
        else {
            StringTokenizer tokenizer = new StringTokenizer(instruction.toString(), " ,()", true);
            while (tokenizer.hasMoreTokens())
            {
                buffer.append(evaluateToken(logic, tokenizer.nextToken()));
            }
        }
    
        return buffer.toString();
    }
    
    public String evaluateExpression(String expression)
    {
        return expression;
    }
    
    protected Properties createDefault()
    {
        Properties props = new Properties();

        try
        {
            // Use the engineEmulation value (i.e. the interpreter version) to see if 
            // there is a version specific conf file for flag, variable and view object names.
            InputStream is = null;
            String versionStr = String.format("%04X", cache.getResourceProvider().getConfiguration().engineEmulation);
            for (int i=versionStr.length(); i >= 0 && is == null; i--) {
                String suffix = versionStr.substring(0, i);
                suffix = ((suffix.length() > 0? "_" : "") + suffix);
                is = getClass().getResourceAsStream("LogicEvaluator" + suffix + ".conf");
            }
            
            props.load(is);
            
            // Remove any mappings for blank values. This allows for the conf file to
            // have a placeholder key without yet knowing what the value should be.
            List<String> propsToRemove = new ArrayList<String>();
            for (Object propName : props.keySet()) {
                String propValue = (String)props.get(propName);
                if (propValue.trim().isEmpty()) {
                    // Need to store these in a separate collection so as to avoid a
                    // concurrent modification exception. We remove in a separate loop.
                    propsToRemove.add((String)propName);
                }
            }
            for (String propName : propsToRemove) {
                props.remove(propName);
            }
            
        }
        catch (IOException ioex)
        {
        }
        
        return props;
    }
    
    public String evaluateToken(Logic logic, String s)
    {
        int    i;
        String t;
    
        for (i = s.length() - 1; i >= 0; i--)
        {
            switch (s.charAt(i))
            {
            case 'v':
            case 'f':
            case 'o':
                if (i > 0)
                {
                    t = s.substring(0, i);
                    s = s.substring(i);
                }
                else
                {
                    t = "";
                }
                
                s = props.getProperty(s, s);
                return t + s;
            
            case 'i':
                try {
                    t = s.substring(i + 1);
                    i = Integer.parseInt(t);

                    InventoryObject item = cache.getObjects().getObject((short)i);
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
                }
                
            case 'w':
                try  {
                    t = s.substring(i + 1);
                    i = Integer.parseInt(t);
                    
                    Word word = cache.getWords().getWordByNumber(i);
                    if (word != null) {
                        return word.text;
                    } else {
                        return s;
                    }
                } 
                catch (ResourceException e) {
                    return s;
                } 
                catch (IOException e)  {
                    return s;
                } 
                catch (NumberFormatException e) {
                    return s;
                }            
                
            case 'm':
                try
                {
                    t = s.substring(i + 1);
                    i = Integer.parseInt(t);
                    
                    return "\"" + logic.getMessageProcessed(i) + "\"";
                }
                catch (NumberFormatException nfex)
                {
                    return s;
                }
            }
        }
        
        return s;
    }
    
    public Vector decompile(LogicInterpreter logicInterpreter)
    {
        Vector        result       = new Vector();
        Instruction[] instructions = logicInterpreter.getInstructions();
    
        result.add(new LogicLine(0, "void logic" + logicInterpreter.getLogicNumber() + "()"));
        result.add(new LogicLine(0, "{"));
    
        decompile(logicInterpreter, instructions, logicInterpreter.getInstructionSizes(), 1, 0, instructions.length, result);

        result.add(new LogicLine(0, "}"));
        return result;
    }
    
    /**
     * if (...) ...
     * if (...) ... else ...
     * for (...; ...)
     * while (...) ...
     * do ... while(...)
     */
    protected int decompile(Logic logic, Instruction[] instructions, int[] sizes, int level, int start, int end, Vector result)
    {
        int          in, destination;
        Instruction  instruction;
        Instruction  firstInstruction;
        int          firstInstructionDestination;
        Instruction  lastInstruction;
        int          lastInstructionDestination;
        String       line;
        StringBuffer buffer;

        if (start == end)
        {
            return 0;
        }

        firstInstruction = instructions[start];
        lastInstruction  = instructions[end - 1];
            
        if (lastInstruction instanceof InstructionMoving)
        {
            lastInstructionDestination = ((InstructionMoving)lastInstruction).getDestination(end - 1, sizes);
        }
        else
        {
            lastInstructionDestination = -1;
        }
        
        if (firstInstruction instanceof InstructionMoving)
        {
            firstInstructionDestination = ((InstructionMoving)firstInstruction).getDestination(start, sizes);
        }
        else
        {
            firstInstructionDestination = -1;
        }
        
        // Scan for Patterns.
        if (lastInstructionDestination == start)
        {
            if (lastInstruction instanceof InstructionGoto)
            {
                // it's a "while (...) ..." loop.
                if (firstInstruction instanceof InstructionIf)
                {
                    line = evaluateExpression(logic, -1, (InstructionIf)firstInstruction);
                }
                else
                {
                    line = "(true)";
                }
                
                result.add(new LogicLine(level, start, firstInstruction, "while " + line));
                result.add(new LogicLine(level, "{"));
                decompile(logic, instructions, sizes, level + 1, start + 1, end - 1, result);
                result.add(new LogicLine(level, "}"));
                return 0;
            }
            else
            {
                // it's a "do ... while(...)" loop.
                result.add(new LogicLine(level, "do"));
                result.add(new LogicLine(level, "{"));

                decompile(logic, instructions, sizes, level + 1, start, end - 1, result);

                if (firstInstruction instanceof InstructionIf)
                {
                    line = evaluateExpression(logic, -1, (InstructionIf)lastInstruction);
                }
                else
                {
                    line = "(true)";
                }
                
                result.add(new LogicLine(level, "}"));
                result.add(new LogicLine(level, start, firstInstruction, "while " + line + ";"));
                return 0;
            }
        }
        else if (firstInstruction instanceof InstructionIf)
        {
            if (firstInstructionDestination == end)
            {
                // It's a if.
                line = evaluateExpression(logic, -1, (InstructionIf)firstInstruction);
                result.add(new LogicLine(level, start, firstInstruction, "if " + line));
                result.add(new LogicLine(level, "{"));
                
                if (lastInstruction instanceof InstructionGoto)
                {
                    decompile(logic, instructions, sizes, level + 1, start + 1, end - 1, result);
                }
                else
                {
                    decompile(logic, instructions, sizes, level + 1, start + 1, end, result);
                }
                
                result.add(new LogicLine(level, "}"));
                return 0;
            }
        }

        // Scan for Backward Jump.
        for (in = end - 1; in >= start; in--)
        {
            instruction = instructions[in];
        
            if (instruction instanceof InstructionMoving)
            {
                destination = ((InstructionMoving)instruction).getDestination(in, sizes);
                
                if (destination < in)
                {
                    // Backward Jump... Probably a loop.
                    
                    if (destination != start)
                    {
                        // Decompile the start seperatly.
                        decompile(logic, instructions, sizes, level, start, destination, result);
                    }
                    
                    decompile(logic, instructions, sizes, level, destination, in + 1, result);

                    if (in != end)
                    {
                        // Decompile the end seperatly.
                        decompile(logic, instructions, sizes, level, in + 1, end, result);
                    }
                    
                    return 0;
                }
            }
        }

        // Scan for Forward Jump.
        for (in = start; in < end; in++)
        {
            instruction = instructions[in];
        
            if (instruction instanceof InstructionMoving)
            {
                destination = ((InstructionMoving)instruction).getDestination(in, sizes);
                
                if (destination >= in)
                {
                    // Forward Jump... Probably a if.
                    
                    if (destination != start)
                    {
                        // Decompile the start seperatly.
                        decompile(logic, instructions, sizes, level, start, in, result);
                    }
                    
                    decompile(logic, instructions, sizes, level, in, destination, result);

                    lastInstruction = instructions[destination - 1];
                    
                    if (lastInstruction instanceof InstructionGoto)
                    {
                        // It's a "if ... else ..."
                        lastInstructionDestination = ((InstructionGoto)lastInstruction).getDestination(destination - 1, sizes);
                        
                        if (lastInstructionDestination > destination)
                        {
                            result.add(new LogicLine(level, destination - 1, lastInstruction, "else"));
                            result.add(new LogicLine(level, "{"));
                            
                            decompile(logic, instructions, sizes, level + 1, destination, lastInstructionDestination, result);
                            destination = lastInstructionDestination;

                            result.add(new LogicLine(level, "}"));
                        }
                    }
                    
                    if (in != end)
                    {
                        // Decompile the end seperatly.
                        decompile(logic, instructions, sizes, level, destination, end, result);
                    }
                    
                    return 0;
                }
            }
        }

        for (in = start; in < end; in++)
        {
            result.add(new LogicLine(level, in, instructions[in], evaluateInstruction(logic, -1, instructions[in]) + ";"));
        }
        
        return 0;
    }
    
    protected boolean hasJumpOutsideArea(Instruction[] instructions, int sizes[], int start, int end)
    {
        int         in, address;
        Instruction instruction;
        
        for (in = start; in < end; in++)
        {
            instruction = instructions[in];
            
            if (instruction instanceof InstructionMoving)
            {
                address = ((InstructionMoving)instructions[in]).getDestination(in, sizes);
                
                if ((address > end) || (address < start))
                {
                    return true;
                }
            }
        }
    
        return false;
    }
}
