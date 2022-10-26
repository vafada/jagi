/*
 *  Loop.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.io.ByteCaster;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class Loop {
    /**
     * Cells
     */
    protected Cell[] cells = null;

    /**
     * Creates new Loop
     */
    public Loop(Cell[] cells) {
        this.cells = cells;
    }

    public Loop(byte[] b, int start, int loopNumber) {
        short cellCount;
        int i, j;

        cellCount = ByteCaster.lohiUnsignedByte(b, start);
        cells = new Cell[cellCount];

        j = start + 1;
        for (i = 0; i < cellCount; i++) {
            cells[i] = new Cell(b, start + ByteCaster.lohiUnsignedShort(b, j), loopNumber);
            j += 2;
        }
    }

    public Cell getCell(int cellNumber) {
        return cells[cellNumber];
    }

    public short getCellCount() {
        return (short) cells.length;
    }
}