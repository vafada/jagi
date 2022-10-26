package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class ExpressionSaidOneWord extends ExpressionSaid {

    public ExpressionSaidOneWord(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws Exception {
        super(stream, reader, bytecode, engineEmulation);
    }

    protected int getWordCount(InputStream stream) throws IOException {
        return 1;
    }

    /**
     * Determine Expression Size.
     *
     * @return Returns the expression size.
     */
    public int getSize() {
        return 1 + (wordNumbers.length * 2);
    }
}
