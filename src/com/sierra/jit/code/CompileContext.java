/**
 * DescriptorTokenizer.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;
import java.util.Vector;

public class CompileContext {
    protected ByteArrayOutputStream outb;
    protected DataOutputStream outs;

    protected Hashtable variables;
    protected Hashtable instructions;
    protected Vector exceptions;
    protected int maxLocal;
    protected int maxStack;

    public CompileContext() {
        outb = new ByteArrayOutputStream();
        outs = new DataOutputStream(outb);
        variables = new Hashtable();
        instructions = new Hashtable();
        exceptions = new Vector();
    }

    public static int getTypeSize(String type) {
        switch (type.charAt(0)) {
            case 'D':
            case 'J':
                return 2;

            case 'V':
                return 0;

            default:
                return 1;
        }
    }

    public int getMaxStack() {
        return maxStack;
    }

    public void setMaxStack(int stack) {
        maxStack = stack;
    }

    public int getMaxLocal() {
        return maxLocal;
    }

    public void addVariable(Scope scope, String name, String type) {
        Hashtable h = (Hashtable) variables.get(scope);
        Object[] o;

        if (h == null) {
            h = new Hashtable();
            variables.put(scope, h);
        }

        if (h.get(name) != null) {
            return;
        }

        o = new Object[]{type, Integer.valueOf(maxLocal)};
        h.put(name, o);
        maxLocal += getTypeSize(type);
    }

    protected Object[] getVariable(Scope scope, String name) {
        Hashtable h = (Hashtable) variables.get(scope);
        Object o;
        Scope p;

        if (h == null) {
            p = scope.getParent();

            if (p != null) {
                return getVariable(p, name);
            }

            return null;
        }

        o = h.get(name);
        if (o == null) {
            p = scope.getParent();

            if (p != null) {
                return getVariable(p, name);
            }

            return null;
        }

        return (Object[]) o;
    }

    public String getVariableType(Scope scope, String name) {
        Object[] o = getVariable(scope, name);

        if (o == null) {
            return null;
        }

        return (String) o[0];
    }

    public int getVariableNumber(Scope scope, String name) {
        Object[] o = getVariable(scope, name);

        if (o == null) {
            return -1;
        }

        return ((Integer) o[1]).intValue();
    }

    public void addLabel(String label, int pc) {
        instructions.put(label, Integer.valueOf(pc));
    }

    public void addScope(Scope scope, int begin, int end) {
        int[] pcs = new int[2];

        pcs[0] = begin;
        pcs[1] = end;
        instructions.put(scope, pcs);
    }

    public int getScopeBegin(Scope scope) {
        int[] pcs = (int[]) instructions.get(scope);

        if (pcs == null) {
            return -1;
        }

        return pcs[0];
    }

    public int getScopeEnd(Scope scope) {
        int[] pcs = (int[]) instructions.get(scope);

        if (pcs == null) {
            return -1;
        }

        return pcs[1];
    }

    public int getLabelAddress(String label) {
        Integer pc = (Integer) instructions.get(label);

        if (pc == null) {
            return -1;
        }

        return pc.intValue();
    }

    public DataOutputStream getOutput() {
        return outs;
    }

    public Vector getExceptions() {
        return exceptions;
    }

    public int getSize() {
        return outb.size();
    }

    public byte[] getData() {
        return outb.toByteArray();
    }
}
