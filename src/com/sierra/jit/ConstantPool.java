package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ConstantPool {
    protected Vector constants;

    public ConstantPool() {
        constants = new Vector();
        constants.add(new Object());
    }

    protected int add(Object o) {
        int index = constants.indexOf(o);

        if (index == -1) {
            index = constants.size();
            constants.add(o);
        }

        return index;
    }

    protected int addClass(String s) {
        String ns = s.replace('.', '/');
        int index = constants.indexOf(ns);

        if (index == -1) {
            index = constants.size();
            constants.add(ns);
        }

        return index;
    }

    public Object getObject(int index) {
        return constants.get(index);
    }

    public int getInteger(int integer) {
        return add(Integer.valueOf(integer));
    }

    public int getLong(long longint) {
        Long o = Long.valueOf(longint);
        int index = constants.indexOf(o);

        if (index == -1) {
            index = constants.size();
            constants.add(o);
            constants.add(null);
        }

        return index;
    }

    public int getUTF8(String str) {
        return add(str);
    }

    public int getString(String str) {
        return add(new ConstantString(add(str)));
    }

    public int getClass(String str) {
        return add(new ConstantClass(addClass(str)));
    }

    public int getNameAndType(String name, String type) {
        return add(new ConstantNameAndType(add(name), add(type)));
    }

    public int getFieldRef(String className, String name, String type) {
        return add(new ConstantField(add(new ConstantClass(addClass(className))),
                add(new ConstantNameAndType(add(name), add(type)))));
    }

    public int getMethodRef(String className, String name, String type) {
        return add(new ConstantMethod(add(new ConstantClass(addClass(className))),
                add(new ConstantNameAndType(add(name), add(type)))));
    }

    public int getInterfaceMethodRef(String className, String name, String type) {
        return add(new ConstantInterfaceMethod(add(new ConstantClass(addClass(className))),
                add(new ConstantNameAndType(add(name), add(type)))));
    }

    public Object getContent(int index) {
        return constants.get(index);
    }

    public void compile(DataOutputStream outs) throws IOException {
        int i, s = constants.size();

        outs.writeShort(s); // Constant Pool Count

        for (i = 1; i < s; i++) {
            Object o = constants.get(i);

            if (o instanceof String) {
                outs.write(1);
                outs.writeUTF((String) o);
            } else if (o instanceof Integer) {
                outs.write(3);
                outs.writeInt(((Integer) o).intValue());
            } else if (o instanceof Long) {
                outs.write(5);
                outs.writeLong(((Long) o).longValue());
                i++;
            } else if (o instanceof Constant) {
                ((Constant) o).compile(outs);
            }
        }
    }
}
