/*
 *  EgaEvent.java
 *  Adventure Game Interface AWT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2002 Dr. Z. All rights reserved.
 */

package com.sierra.agi.awt;

public class EgaEvent {
    public static final byte TYPE_DIRECTION = (byte) 1;
    public static final byte TYPE_CHAR = (byte) 2;
    public static final byte TYPE_SHORTCUT = (byte) 3;
    public byte type;
    public short data;

    public EgaEvent() {
    }

    public EgaEvent(byte type, short data) {
        this.type = type;
        this.data = data;
    }
}
