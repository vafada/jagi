package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class Method {
    protected static final short ACC_PUBLIC = 0x0001;
    protected static final short ACC_PRIVATE = 0x0002;
    protected static final short ACC_PROTECTED = 0x0004;
    protected static final short ACC_STATIC = 0x0008;
    protected static final short ACC_FINAL = 0x0010;
    protected static final short ACC_SYNCHRONIZED = 0x0020;
    protected static final short ACC_NATIVE = 0x0100;
    protected static final short ACC_ABSTRACT = 0x0400;
    protected static final short ACC_STRICT = 0x0800;
    protected ConstantPool constants;
    protected int nameIndex;
    protected int descriptionIndex;
    protected int flags;
    protected AttributeCollection attributes;

    public Method(ConstantPool constants, int nameIndex, int descriptionIndex) {
        this.constants = constants;
        this.nameIndex = nameIndex;
        this.descriptionIndex = descriptionIndex;
        this.attributes = new AttributeCollection();
        this.flags = ACC_PUBLIC;
    }

    public void compile(DataOutputStream outs) throws IOException {
        outs.writeShort(flags);
        outs.writeShort(nameIndex);
        outs.writeShort(descriptionIndex);
        attributes.compile(outs);
    }

    public String getMethodName() {
        return (String) constants.getObject(nameIndex);
    }

    public boolean isPublic() {
        return (flags & ACC_PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (flags & ACC_PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return (flags & ACC_PRIVATE) != 0;
    }

    public boolean isStatic() {
        return (flags & ACC_STATIC) != 0;
    }

    public void setStatic(boolean v) {
        if (v) {
            flags |= ACC_STATIC;
        } else {
            flags &= ~ACC_STATIC;
        }
    }

    public boolean isFinal() {
        return (flags & ACC_FINAL) != 0;
    }

    public void setFinal(boolean v) {
        if (v) {
            flags |= ACC_FINAL;
        } else {
            flags &= ~ACC_FINAL;
        }
    }

    public boolean isSynchronized() {
        return (flags & ACC_SYNCHRONIZED) != 0;
    }

    public void setSynchronized(boolean v) {
        if (v) {
            flags |= ACC_SYNCHRONIZED;
        } else {
            flags &= ~ACC_SYNCHRONIZED;
        }
    }

    public boolean isNative() {
        return (flags & ACC_NATIVE) != 0;
    }

    public void setNative(boolean v) {
        if (v) {
            flags |= ACC_NATIVE;
        } else {
            flags &= ~ACC_NATIVE;
        }
    }

    public boolean isAbstract() {
        return (flags & ACC_ABSTRACT) != 0;
    }

    public void setAbstract(boolean v) {
        if (v) {
            flags |= ACC_ABSTRACT;
        } else {
            flags &= ~ACC_ABSTRACT;
        }
    }

    public boolean isStrict() {
        return (flags & ACC_STRICT) != 0;
    }

    public void setStrict(boolean v) {
        if (v) {
            flags |= ACC_STRICT;
        } else {
            flags &= ~ACC_STRICT;
        }
    }

    public void setPublic() {
        flags |= ACC_PUBLIC;
        flags &= ~(ACC_PROTECTED | ACC_PRIVATE);
    }

    public void setProtected() {
        flags |= ACC_PROTECTED;
        flags &= ~(ACC_PUBLIC | ACC_PRIVATE);
    }

    public void setPrivate() {
        flags |= ACC_PRIVATE;
        flags &= ~(ACC_PUBLIC | ACC_PROTECTED);
    }

    public void addException(String exceptionClass) {
        ExceptionsAttribute o = (ExceptionsAttribute) attributes.get("Exceptions");

        if (o == null) {
            o = new ExceptionsAttribute(constants.getUTF8("Exceptions"));
            attributes.put("Exceptions", o);
        }

        o.addException(constants.getClass(exceptionClass));
    }

    public Code getCode() {
        Code o = (Code) attributes.get("Code");

        if (o == null) {
            o = new Code(constants, constants.getUTF8("Code"), descriptionIndex, isStatic());
            attributes.put("Code", o);
        }

        return o;
    }

    public com.sierra.jit.code.Scope getCodeScope() {
        return getCode().getScope();
    }
}
