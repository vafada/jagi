/**
 * DebugLogicProvider.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic.debug;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.logic.LogicProvider;
import com.sierra.agi.logic.interpret.LogicReader;
import com.sierra.agi.logic.interpret.instruction.InstructionReturn;
import com.sierra.agi.res.ResourceConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class DebugLogicProvider implements LogicProvider {
    protected LogicReader reader;

    public DebugLogicProvider(ResourceConfiguration config) {
        reader = new LogicReader(config.engineEmulation);
    }

    public Logic loadLogic(short logicNumber, InputStream inputStream, int size) throws IOException, LogicException {
        Vector instructions = new Vector();
        String[] messages;

        messages = reader.loadLogic(inputStream, size, instructions);

        if (!(instructions.get(instructions.size() - 1) instanceof InstructionReturn)) {
            instructions.add(new InstructionReturn());
        }

        return new LogicDebug(logicNumber, instructions, messages);
    }
}
