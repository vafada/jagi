package com.sierra.agi.logic.interpret.expression;

import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.interpret.LogicReader;

import java.io.IOException;
import java.io.InputStream;

public class ExpressionBitSet extends ExpressionBi {
    /**
     * Creates a new Bit Set Expression.
     *
     * @param context  Game context where this instance of the expression will be used.
     * @param stream   Logic Stream. Expression must be written in uninterpreted format.
     * @param reader   LogicReader used in the reading of this expression.
     * @param bytecode Bytecode of the current expression.
     */
    public ExpressionBitSet(InputStream stream, LogicReader reader, short bytecode, short engineEmulation) throws IOException {
        super(stream, bytecode);
    }

    /**
     * Evaluate Expression.
     *
     * @param logic        Logic used to evaluate the expression.
     * @param logicContext Logic Context used to evaluate the expression.
     * @return Returns the result of the evaluation.
     */
    public boolean evaluate(Logic logic, LogicContext logicContext) {
        // TODO: Change this. Needs to check the specified bit position.
        return logicContext.getVar(p1) == p2;
    }

//#ifdef DEBUG

    /**
     * Retreive the AGI Expression name and parameters.
     * <B>For debugging purpose only. Will be removed in final releases.</B>
     *
     * @return Returns the textual name of the expression.
     */
    public String[] getNames() {
        String[] names = new String[3];

        names[0] = "bit.set";
        names[1] = Integer.toString(p1);
        names[2] = "v" + p2;

        return names;
    }
//#endif DEBUG
}
