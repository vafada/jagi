package com.sierra.jit;

import java.io.DataOutputStream;
import java.io.IOException;

public class Field {
    protected static final short ACC_PUBLIC = 0x0001;
    protected static final short ACC_PRIVATE = 0x0002;
    protected static final short ACC_PROTECTED = 0x0004;
    protected static final short ACC_STATIC = 0x0008;
    protected static final short ACC_FINAL = 0x0010;
    protected static final short ACC_VOLATILE = 0x0040;
    protected static final short ACC_TRANSCIENT = 0x0080;
    protected ConstantPool constants;
    protected int nameIndex;
    protected int descriptionIndex;
    protected int flags;
    protected AttributeCollection attributes;

    public Field(ConstantPool constants, int nameIndex, int descriptionIndex) {
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

    public boolean isVolatile() {
        return (flags & ACC_VOLATILE) != 0;
    }

    public void setVolatile(boolean v) {
        if (v) {
            flags |= ACC_VOLATILE;
        } else {
            flags &= ~ACC_VOLATILE;
        }
    }

    public boolean isTranscient() {
        return (flags & ACC_TRANSCIENT) != 0;
    }

    public void setTranscient(boolean v) {
        if (v) {
            flags |= ACC_TRANSCIENT;
        } else {
            flags &= ~ACC_TRANSCIENT;
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

    public int getConstantValue() {
        Object o = attributes.get("ConstantValue");

        if (o == null) {
            return -1;
        }

        return ((ConstantValueAttribute) o).getConstantIndex();
    }

    public void setConstantValue(int constantIndex) {
        attributes.put("ConstantValue", new ConstantValueAttribute(constants.getUTF8("ConstantValue"), constantIndex));
    }
}
