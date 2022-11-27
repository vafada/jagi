/*
 *  ViewSprite.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.logic.LogicVariables;

import java.awt.Rectangle;
import java.awt.geom.Area;

public class ViewSprite implements Comparable<ViewSprite> {
    private final AnimatedObject entry;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private int[] backupPriority;
    private int[] backupPicture;

    public ViewSprite(AnimatedObject v) {
        entry = v;
        x = v.getX();
        y = v.getY() - v.getHeight() + 1;
        width = v.getWidth();
        height = v.getHeight();
    }

    public void blit(Area screenUpdate, int[] picture, int[] priority) {
        byte cellPriority = (byte) entry.getPriority();
        int[] cellData = entry.getCellData().getPixelData();
        int cellTransparent = entry.getCellData().getTransparentPixel();

        int screenOffset = (y * ViewTable.WIDTH) + x;
        int cellOffset = 0;
        int remaining = ViewTable.WIDTH - width;

        for (int line = 0; line < height; line++) {
            for (int col = 0; col < width; col++, screenOffset++, cellOffset++) {
                if (priority[screenOffset] <= cellPriority) {
                    int pixel = cellData[cellOffset];

                    if (pixel != cellTransparent) {
                        picture[screenOffset] = pixel;
                        int currentPriorityPixel = priority[screenOffset];
                        // do not override control pixels
                        if (currentPriorityPixel >= 4) {
                            priority[screenOffset] = cellPriority;
                        }
                    }
                }
            }

            screenOffset += remaining;
        }

        screenUpdate.add(new Area(new Rectangle(x, y, width, height)));
    }

    public void save(int[] picture, int[] priority) {
        if (backupPriority == null) {
            backupPriority = new int[width * height];
        }

        if (backupPicture == null) {
            backupPicture = new int[width * height];
        }

        int screenOffset = (y * ViewTable.WIDTH) + x;
        int backupOffset = 0;

        for (int line = 0; line < height; line++) {
            System.arraycopy(picture, screenOffset, backupPicture, backupOffset, width);
            System.arraycopy(priority, screenOffset, backupPriority, backupOffset, width);
            screenOffset += ViewTable.WIDTH;
            backupOffset += width;
        }
    }

    public void restore(Area screenUpdate, int[] picture, int[] priority) {
        if ((backupPicture == null) || (backupPriority == null)) {
            System.out.println("(backupScreen == null) || (backupPriority == null)");
            return;
        }

        int screenOffset = (y * ViewTable.WIDTH) + x;
        int backupOffset = 0;

        for (int line = 0; line < height; line++) {
            System.arraycopy(backupPicture, backupOffset, picture, screenOffset, width);
            System.arraycopy(backupPriority, backupOffset, priority, screenOffset, width);
            screenOffset += ViewTable.WIDTH;
            backupOffset += width;
        }

        screenUpdate.add(new Area(new Rectangle(x, y, width, height)));
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



    @Override
    public int compareTo(ViewSprite other) {
        if (this.entry.priority < other.entry.priority) {
            return -1;
        } else if (this.entry.priority > other.entry.priority) {
            return 1;
        } else {
            if (this.entry.effectiveY() < other.entry.effectiveY()) {
                return -1;
            } else if (this.entry.effectiveY() > other.entry.effectiveY()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
