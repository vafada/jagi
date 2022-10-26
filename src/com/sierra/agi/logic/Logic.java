/**
 * Logic.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

public abstract class Logic {
    protected short logicNumber;

    public abstract void execute(LogicContext logicContext) throws LogicException;

    public abstract String getMessage(int msgNumber);

    public String getMessageProcessed(int msgNumber) {
        String s = getMessage(msgNumber), b, e;
        int i, j, n;

        if (s == null) {
            return null;
        }

        while (true) {
            i = s.indexOf("%m");

            if (i == -1) {
                break;
            }

            b = s.substring(0, i);
            i += 2;
            j = i;

            try {
                while (Character.isDigit(s.charAt(j))) {
                    j++;
                }
            } catch (IndexOutOfBoundsException ioobex) {
            }

            n = Integer.valueOf(s.substring(i, j)).intValue();
            e = s.substring(j);
            s = b + getMessageProcessed(n) + e;
        }

        return s;
    }
}
