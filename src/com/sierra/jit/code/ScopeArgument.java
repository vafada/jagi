/**
 * ScopeArgument.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import com.sierra.jit.Code;
import com.sierra.jit.ConstantPool;

import java.util.Enumeration;
import java.util.Vector;

public class ScopeArgument extends Scope {
    protected Vector parameters = new Vector();
    protected String signature;

    public ScopeArgument(ConstantPool constants, Code code, String signature, boolean isStatic) {
        super(constants, code);

        DescriptorTokenizer tokenizer = new DescriptorTokenizer(signature);
        int c = 0;

        if (!isStatic) {
            parameters.add(new String[]{"this", "Ljava/lang/Object;"});
        }

        while (tokenizer.hasMoreElements()) {
            parameters.add(new String[]{"<arg" + c + ">", (String) tokenizer.nextElement()});
            c++;
        }

        code.setReturnType(tokenizer.getReturnValue());
    }

    protected void compileVariables(CompileContext context) {
        Enumeration en = parameters.elements();
        String[] s;

        while (en.hasMoreElements()) {
            s = (String[]) en.nextElement();
            context.addVariable(this, s[0], s[1]);
        }

        super.compileVariables(context);
    }

    public void setParameterName(int number, String variableName) {
        ((String[]) parameters.get(number))[0] = variableName;
    }
}