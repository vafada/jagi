package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class ExpressionSaidTwoWords extends ExpressionSaid {

    public ExpressionSaidTwoWords(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws Exception {
        super(stream, reader, bytecode, engineEmulation);
    }

    protected int getWordCount(InputStream stream) throws IOException {
        return 2;
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
