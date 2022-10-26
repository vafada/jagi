/**
 * Scope.java
 * Just-in-Time Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.jit.code;

import com.sierra.jit.Code;
import com.sierra.jit.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Scope {
    // Relationships
    protected ConstantPool constants;
    protected Code code;
    protected Scope parent;

    // Variables
    protected Hashtable variables = new Hashtable();

    // Content
    protected Vector content = new Vector();
    protected int contentIndex = 0;

    public Scope(Scope parent, ConstantPool constants) {
        this.parent = parent;
        this.code = parent.getCode();
        this.constants = constants;
    }

    public Scope(ConstantPool constants, Code code) {
        this.code = code;
        this.constants = constants;
    }

    public Scope getParent() {
        return parent;
    }

    public Scope getRoot() {
        Scope s;

        if (parent == null) {
            return this;
        }

        s = parent;

        while (true) {
            if (s.parent == null) {
                return s;
            }

            s = s.parent;
        }
    }

    public Code getCode() {
        return code;
    }

    public CompileContext compile() throws IOException {
        CompileContext context = new CompileContext();

        // Phase 1 - Variables
        compileVariables(context);

        // Phase 2 - Instructions
        compileInstructions(context, 0, 0);

        // Phase 3 - Exceptions
        compileExceptionTable(context);

        // Phase 4 - Binary Generation
        compileBinary(context, 0);

        return context;
    }

    protected void compileVariables(CompileContext context) {
        Enumeration en = variables.keys();
        Object key;

        while (en.hasMoreElements()) {
            key = en.nextElement();

            context.addVariable(this, (String) key, (String) variables.get(key));
        }

        en = content.elements();

        while (en.hasMoreElements()) {
            key = en.nextElement();

            if (key instanceof Scope) {
                ((Scope) key).compileVariables(context);
            }
        }
    }

    protected int compileInstructions(CompileContext context, int pc, int stack) {
        Enumeration en = content.elements();
        int begin = pc;
        int max = context.getMaxStack();
        Object key;

        while (en.hasMoreElements()) {
            key = en.nextElement();

            if (key instanceof Scope) {
                pc = ((Scope) key).compileInstructions(context, pc, stack);
                max = context.getMaxStack();
            } else if (key instanceof String) {
                context.addLabel((String) key, pc);
            } else if (key instanceof Instruction instruction) {

                pc += instruction.getSize(context, this, pc);
                stack -= instruction.getPopCount();
                stack += instruction.getPushCount();

                if (stack > max) {
                    max = stack;
                    context.setMaxStack(stack);
                }
            }
        }

        context.addScope(this, begin, pc);
        return pc;
    }

    protected void compileExceptionTable(CompileContext context) {
        Enumeration en = content.elements();
        Object key;

        while (en.hasMoreElements()) {
            key = en.nextElement();

            if (key instanceof Scope) {
                ((Scope) key).compileExceptionTable(context);
            }
        }
    }

    protected int compileBinary(CompileContext context, int pc) throws IOException {
        DataOutputStream out = context.getOutput();
        Enumeration en = content.elements();
        Object key;

        while (en.hasMoreElements()) {
            key = en.nextElement();

            if (key instanceof Scope) {
                pc = ((Scope) key).compileBinary(context, pc);
            } else if (key instanceof Instruction instruction) {

                instruction.compile(context, this, out, pc);
                pc += instruction.getSize(context, this, pc);
            }
        }

        return pc;
    }

    public String generateLabel() {
        return code.generateLabel();
    }

    public Scope addSynchronizedScope() {
        return add(new ScopeSynchronized(this, constants));
    }

    public void addLoadVariable(String variableName) {
        add(new InstructionLoad(variableName));
    }

    public void addStoreVariable(String variableName) {
        add(new InstructionStore(variableName));
    }

    public void addInvokeSpecial(String className, String methodName, String description) {
        add(new InstructionInvoke(constants.getMethodRef(className, methodName, description), InstructionInvoke.TYPE_SPECIAL, description));
    }

    public void addInvokeVirtual(String className, String methodName, String description) {
        add(new InstructionInvoke(constants.getMethodRef(className, methodName, description), InstructionInvoke.TYPE_VIRTUAL, description));
    }

    public void addGetStatic(String className, String fieldName, String description) {
        add(new InstructionGetStatic(constants.getFieldRef(className, fieldName, description), description));
    }

    public void addPutStatic(String className, String fieldName, String description) {
        add(new InstructionPutStatic(constants.getFieldRef(className, fieldName, description), description));
    }

    public void addInvokeStatic(String className, String methodName, String description) {
        add(new InstructionInvoke(constants.getMethodRef(className, methodName, description), InstructionInvoke.TYPE_STATIC, description));
    }

    public void addPushConstant(byte constant) {
        if ((constant >= -1) && (constant <= 5)) {
            add(new InstructionDummy((byte) (0x3 + constant), 0, 1));
        } else {
            add(new InstructionDummy(new byte[]{0x10, constant}, 0, 1));
        }
    }

    public void addPushConstant(short constant) {
        if ((constant >= -1) && (constant <= 5)) {
            add(new InstructionDummy((byte) (0x3 + constant), 0, 1));
        } else if ((constant <= 127) && (constant >= -128)) {
            add(new InstructionDummy(new byte[]{0x10, (byte) constant}, 0, 1));
        } else {
            add(new InstructionDummy(new byte[]{0x11, (byte) ((constant & 0xff00) >> 8), (byte) (constant & 0xff)}, 0, 1));
        }
    }

    public void addPushConstant(int constant) {
        if ((constant >= -1) && (constant <= 5)) {
            add(new InstructionDummy((byte) (0x3 + constant), 0, 1));
        } else if ((constant <= 127) && (constant >= -128)) {
            add(new InstructionDummy(new byte[]{0x10, (byte) constant}, 0, 1));
        } else if ((constant <= 32767) && (constant >= -32768)) {
            add(new InstructionDummy(new byte[]{0x11, (byte) ((constant & 0xff00) >> 8), (byte) (constant & 0xff)}, 0, 1));
        } else {
            int i = constants.getInteger(constant);

            if (i <= 255) {
                add(new InstructionDummy(new byte[]{0x12, (byte) i}, 0, 1));
            } else {
                add(new InstructionDummy(new byte[]{0x13, (byte) (i >> 8), (byte) (i & 0xff)}, 0, 1));
            }
        }
    }

    public void addPushConstant(long constant) {
        if ((constant >= 0) && (constant <= 1)) {
            add(new InstructionDummy((byte) (0x9 + constant), 0, 2));
        } else if ((constant >= -1) && (constant <= 5)) {
            add(new InstructionDummy(new byte[]{(byte) (0x3 + (int) constant), (byte) 0x85}, 0, 1));
        } else if ((constant <= 127) && (constant >= -128)) {
            add(new InstructionDummy(new byte[]{0x10, (byte) constant, (byte) 0x85}, 0, 2));
        } else {
            int i = constants.getLong(constant);

            add(new InstructionDummy(new byte[]{0x14, (byte) (i >> 8), (byte) (i & 0xff)}, 0, 2));
        }
    }

    public void addPushConstant(String constant) {
        int i = constants.getString(constant);

        if (i <= 255) {
            add(new InstructionDummy(new byte[]{0x12, (byte) i}, 0, 1));
        } else {
            add(new InstructionDummy(new byte[]{0x13, (byte) (i >> 8), (byte) (i & 0xff)}, 0, 1));
        }
    }

    public void addLeaveScope() {
    }

    public void addPop() {
        add(new InstructionDummy((byte) 0x57, 1, 0));
    }

    public void addPopLong() {
        add(new InstructionDummy((byte) 0x58, 2, 0));
    }

    public void addDuplicate() {
        add(new InstructionDummy((byte) 0x59, 1, 2));
    }

    public void addDuplicateLong() {
        add(new InstructionDummy((byte) 0x5c, 2, 4));
    }

    public void addIntegerAdd() {
        add(new InstructionDummy((byte) 0x60, 2, 1));
    }

    public void addIntegerStore() {
        add(new InstructionDummy((byte) 0x4f, 3, 0));
    }

    public void addIfEqualZero(String label) {
        add(new InstructionConditionalGoto(InstructionConditionalGoto.CONDITION_IFEQ, label));
    }

    public void addIntegerSubstract() {
        add(new InstructionDummy((byte) 0x64, 2, 1));
    }

    public void addIntegerAnd() {
        add(new InstructionDummy((byte) 0x7e, 2, 1));
    }

    public void addIntegerToShort() {
        add(new InstructionDummy((byte) 0x93, 1, 1));
    }

    public void addGoto(String label) {
        add(new InstructionGoto(label));
    }

    public void addConditionalGoto(byte type, String label) {
        add(new InstructionConditionalGoto(type, label));
    }

    public void addReturns() {
        Scope scope = this;

        while (scope != null) {
            scope.addLeaveScope();
            scope = scope.parent;
        }

        add(new InstructionReturn(code.getReturnType()));
    }

    public void addNewArray(String signature) {
        add(getNewArray(signature));
    }

    protected Instruction getThrow() {
        return new InstructionDummy(new byte[]{(byte) 0xbf}, 1, 0);
    }

    protected Instruction getDuplicate() {
        return new InstructionDummy(new byte[]{(byte) 0x59}, 1, 2);
    }

    protected Instruction getNewArray(String signature) {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) 0xbc;

        switch (signature.charAt(0)) {
            case 'Z':
                bytes[1] = (byte) 4;
                break;

            case 'C':
                bytes[1] = (byte) 5;
                break;

            case 'F':
                bytes[1] = (byte) 6;
                break;

            case 'D':
                bytes[1] = (byte) 7;
                break;

            case 'B':
                bytes[1] = (byte) 8;
                break;

            case 'S':
                bytes[1] = (byte) 9;
                break;

            case 'I':
                bytes[1] = (byte) 10;
                break;

            case 'J':
                bytes[1] = (byte) 11;
                break;
        }

        return new InstructionDummy(bytes, 0, 1);
    }

    protected Instruction getMonitorEnter() {
        return new InstructionDummy(new byte[]{(byte) 0xc2}, 1, 0);
    }

    protected Instruction getMonitorLeave() {
        return new InstructionDummy(new byte[]{(byte) 0xc3}, 1, 0);
    }

    public void addLabel(String label) {
        content.add(contentIndex++, label);
    }

    public void addVariable(String name, String type) {
        variables.put(name, type);
    }

    public Instruction add(Instruction instruction) {
        content.add(contentIndex++, instruction);
        return instruction;
    }

    public Scope add(Scope scope) {
        content.add(contentIndex++, scope);
        return scope;
    }
}
