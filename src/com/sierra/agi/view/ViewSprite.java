/*
 *  ViewSprite.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import java.awt.*;
import java.awt.geom.Area;

public class ViewSprite extends ViewList {
    protected AnimatedObject entry;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int[] backupPriority;
    protected int[] backupScreen;

    public ViewSprite(AnimatedObject v) {
        entry = v;
        x = v.getX();
        y = v.getY() - v.getHeight() + 1;
        width = v.getWidth();
        height = v.getHeight();

        v.setSprite(this);
    }

    public void dispose() {
        backupPriority = null;
        backupScreen = null;
    }

    public void blit(Area screenUpdate, int[] screen, int[] priority) {
        byte cellPriority = (byte) entry.getPriority();
        int[] cellData = entry.getCellData().getPixelData();
        int cellTransparent = entry.getCellData().getTransparentPixel();

        int screenOffset, cellOffset;
        int line, col, remaining;
        int pixel;

        screenOffset = (y * ViewTable.WIDTH) + x;
        cellOffset = 0;
        remaining = ViewTable.WIDTH - width;

        for (line = 0; line < height; line++) {
            for (col = 0; col < width; col++, screenOffset++, cellOffset++) {
                if (priority[screenOffset] <= cellPriority) {
                    pixel = cellData[cellOffset];

                    if (pixel != cellTransparent) {
                        screen[screenOffset] = pixel;
                        priority[screenOffset] = cellPriority;
                    }
                }
            }

            screenOffset += remaining;
        }

        screenUpdate.add(new Area(new Rectangle(x, y, width, height)));
    }

    public void save(int[] screen, int[] priority) {
        int screenOffset, backupOffset;
        int line;

        if (backupPriority == null) {
            backupPriority = new int[width * height];
        }

        if (backupScreen == null) {
            backupScreen = new int[width * height];
        }

        screenOffset = (y * ViewTable.WIDTH) + x;
        backupOffset = 0;

        for (line = 0; line < height; line++) {
            System.arraycopy(screen, screenOffset, backupScreen, backupOffset, width);
            System.arraycopy(priority, screenOffset, backupPriority, backupOffset, width);
            screenOffset += ViewTable.WIDTH;
            backupOffset += width;
        }
    }

    public void restore(Area screenUpdate, int[] screen, int[] priority) {
        int screenOffset, backupOffset;
        int line;

        if ((backupScreen == null) || (backupPriority == null)) {
            System.out.println("(backupScreen == null) || (backupPriority == null)");
            return;
        }

        screenOffset = (y * ViewTable.WIDTH) + x;
        backupOffset = 0;

        for (line = 0; line < height; line++) {
            System.arraycopy(backupScreen, backupOffset, screen, screenOffset, width);
            System.arraycopy(backupPriority, backupOffset, priority, screenOffset, width);
            screenOffset += ViewTable.WIDTH;
            backupOffset += width;
        }

        screenUpdate.add(new Area(new Rectangle(x, y, width, height)));
        //backupScreen   = null;
        //backupPriority = null;
    }

    public AnimatedObject getViewEntry() {
        return entry;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScreenOffset() {
        return (y * ViewTable.WIDTH) + x;
    }
}
