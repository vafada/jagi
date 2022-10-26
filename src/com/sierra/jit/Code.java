package com.sierra.jit;

import com.sierra.jit.code.CompileContext;
import com.sierra.jit.code.ExceptionTableEntry;
import com.sierra.jit.code.Scope;
import com.sierra.jit.code.ScopeArgument;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class Code extends Attribute {
    protected AttributeCollection attributes = new AttributeCollection();
    protected ConstantPool constants;
    protected Scope contents;
    protected String returnType;

    protected int counter;
    protected int maxStack = -1;
    protected int maxLocals = -1;
    protected CompileContext context;

    public Code(ConstantPool constants, int nameIndex, int descriptionIndex, boolean isStatic) {
        super(nameIndex);
        this.constants = constants;
        this.contents = new ScopeArgument(constants, this, (String) constants.getContent(descriptionIndex), isStatic);
    }

    public void clearContext() {
        context = null;
    }

    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String type) {
        returnType = type;
    }

    public void compile() {
        if (context == null) {
            try {
                context = contents.compile();
            } catch (IOException ioex) {
            }
        }
    }

    public void compile(DataOutputStream outs) throws IOException {
        Enumeration en;
        Vector exceptions;

        super.compile(outs);
        compile();

        outs.writeShort(maxStack == -1 ? context.getMaxLocal() : maxStack);
        outs.writeShort(maxLocals == -1 ? context.getMaxLocal() : maxLocals);

        outs.writeInt(context.getSize());
        outs.write(context.getData());

        exceptions = context.getExceptions();
        en = exceptions.elements();
        outs.writeShort(exceptions.size());

        while (en.hasMoreElements()) {
            ((ExceptionTableEntry) en.nextElement()).compile(outs);
        }

        attributes.compile(outs);
    }

    public int getSize() {
        int size = attributes.getSize() + 2 + 2 + 4 + 2;

        compile();

        size += (context.getExceptions().size() * 8);
        size += context.getSize();
        return size;
    }

    public Scope getScope() {
        return contents;
    }

    public String generateLabel() {
        return "<generated" + counter++ + ">";
    }
}
