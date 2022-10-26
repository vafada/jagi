package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class ExpressionUnknown1 extends ExpressionUni {
    /**
     * Creates a new unknown expression with one parameter.
     *
     * @param context  Game context where this instance of the expression will be used.
     * @param stream   Logic Stream. Expression must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this expression.
     * @param bytecode Bytecode of the current expression.
     */
    public ExpressionUnknown1(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    @Override
    public boolean evaluate(Logic logic, LogicContext logicContext) throws Exception {
        return false;
    }

    @Override
    public String[] getNames() {
        return new String[]{"unknown", Integer.toString(p1)};
    }
}
