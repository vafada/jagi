/*
 *  ViewTable.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.pic.Picture;
import com.sierra.agi.pic.PictureContext;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.res.ResourceException;

import java.awt.*;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ViewTable {
    public static final int MAX_VIEWENTRY = 32;
    public static final short EGO_ENTRY = (short) 0;
    public static final int WIDTH = 160;
    public static final int HEIGHT = 168;
    protected static final short[] directionTable = new short[]{
            (short) 8, (short) 1, (short) 2,
            (short) 7, (short) 0, (short) 3,
            (short) 6, (short) 5, (short) 4};
    protected static final short[] directionTable2 = new short[]{
            (short) 4, (short) 4, (short) 0, (short) 0, (short) 0,
            (short) 4, (short) 1, (short) 1, (short) 1, (short) 0};
    protected static final short[] directionTable4 = new short[]{
            (short) 4, (short) 3, (short) 0, (short) 0, (short) 0,
            (short) 2, (short) 1, (short) 1, (short) 1, (short) 0};
    protected static final int[] directionTableX = new int[]{0, 0, 1, 1, 1, 0, -1, -1, -1};
    protected static final int[] directionTableY = new int[]{0, -1, -1, 0, 1, 1, 1, 0, -1};
    protected LogicContext logicContext;
    protected ViewEntry[] viewEntries;
    protected boolean picShown;
    protected boolean blockSet;
    protected short blockX1;
    protected short blockX2;
    protected short blockY1;
    protected short blockY2;
    protected Random randomSeed;
    protected byte[] priority = new byte[WIDTH * HEIGHT];
    protected byte[] priorityTable = new byte[HEIGHT];
    protected ViewScreen screen;
    protected int[] screenView;
    protected Area screenUpdate;
    protected ViewList updateList = new ViewList();
    protected ViewList updateNotList = new ViewList();
    protected PictureContext pictureContext;
    protected int[] pixel = new int[1];

    public ViewTable(LogicContext context) {
        int i;

        logicContext = context;
        viewEntries = new ViewEntry[MAX_VIEWENTRY];

        for (i = 0; i < viewEntries.length; i++) {
            viewEntries[i] = new ViewEntry(i);
        }

        randomSeed = new Random();
        resetPriorityBands();

        screen = new ViewScreen();
        screenView = new int[WIDTH * HEIGHT];
        screenUpdate = new Area(new Rectangle(0, 0, WIDTH, HEIGHT));
    }

    public static short priorityToY(int priority) {
        return (short) (((priority - 5) * 12) + 48);
    }

    protected static int absolute(int n) {
        return (n < 0) ? -n : n;
    }

    public void reset() {
        int i;

        blockSet = false;
        picShown = false;

        for (i = 0; i < viewEntries.length; i++) {
            viewEntries[i].reset();
        }

        resetPriorityBands();

        screen.reset();
        screenUpdate.add(new Area(new Rectangle(0, 0, WIDTH, HEIGHT)));
        Arrays.fill(screenView, translatePixel((byte) 0));
        Arrays.fill(priority, (byte) 4);
    }

    protected void resetPriorityBands() {
        int i, p, y = 0;

        for (p = 1; p < 15; p++) {
            for (i = 0; i < 12; i++) {
                priorityTable[y++] = (p < 4) ? (byte) 4 : (byte) p;
            }
        }
    }

    public void resetNewRoom() {
        int i;
        ViewEntry v;

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            v = viewEntries[i];

            v.removeFlags(ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_DRAWN);
            v.addFlags(ViewEntry.FLAG_UPDATE);
            v.setStepTime((short) 1);
            v.setStepTimeCount((short) 1);
            v.setCycleTime((short) 1);
            v.setCycleTimeCount((short) 1);
            v.setStepSize((short) 1);
        }
    }

    public void drawPic(Picture picture) throws PictureException {
        pictureContext = picture.draw();
    }

    public void overlayPic(Picture picture) throws PictureException {
        picture.draw(pictureContext);
    }

    public void addToPic(short viewNumber, short loopNumber, short cellNumber, short x, short y, byte priority, short marge) throws ResourceException, IOException, ViewException {
        Cell cell;

        cell = logicContext.getCache().getView(viewNumber).getLoop(loopNumber).getCell(cellNumber);
        pictureContext.addToPic(cell, x, y, priority, marge);
    }

    public void showPic() {
        eraseBoth();
        System.arraycopy(pictureContext.getPictureData(), 0, screenView, 0, screenView.length);
        System.arraycopy(pictureContext.getPriorityData(), 0, priority, 0, priority.length);
        blitBoth();

        screenUpdate.reset();
        screenUpdate.add(new Area(new Rectangle(0, 0, WIDTH, HEIGHT)));
        doUpdate();
    }

    public int translatePixel(byte b) {
        EgaUtils.getNativeColorModel().getDataElements(EgaUtils.getIndexColorModel().getRGB(b), pixel);
        return pixel[0];
    }

    public boolean inBox(short entry, short x1, short y1, short x2, short y2) {
        return viewEntries[entry].inBox(x1, y1, x2, y2);
    }

    public boolean inPos(short entry, short x1, short y1, short x2, short y2) {
        return viewEntries[entry].inPos(x1, y1, x2, y2);
    }

    public boolean inCentre(short entry, short x1, short y1, short x2, short y2) {
        return viewEntries[entry].inCentre(x1, y1, x2, y2);
    }

    public boolean inRight(short entry, short x1, short y1, short x2, short y2) {
        return viewEntries[entry].inCentre(x1, y1, x2, y2);
    }

    public short distance(short s, short d) {
        ViewEntry si = viewEntries[s];
        ViewEntry di = viewEntries[d];

        if (si.isSomeFlagsSet(ViewEntry.FLAG_DRAWN) && di.isSomeFlagsSet(ViewEntry.FLAG_DRAWN)) {
            int r = absolute((si.getX() + (si.getWidth() / 2)) - (di.getX() + (di.getWidth() / 2))) + absolute(si.getY() - di.getY());

            if (r > 0xfe) {
                return (short) 0xfe;
            }

            return (short) r;
        } else {
            return (short) 0xff;
        }
    }

    public void setBlock(short x1, short y1, short x2, short y2) {
        blockSet = true;
        blockX1 = x1;
        blockY1 = y1;
        blockX2 = x2;
        blockY2 = y2;
    }

    public void resetBlock() {
        blockSet = false;
    }

    public ViewScreen getViewScreen() {
        return screen;
    }

    public void setView(short entry, short view) {
        viewEntries[entry].setView(logicContext, view);
    }

    public short getView(short entry) {
        return viewEntries[entry].getView();
    }

    public void setLoop(short entry, short loop) {
        viewEntries[entry].setLoop(logicContext, loop);
    }

    public short getLoop(short entry) {
        return viewEntries[entry].getLoop();
    }

    public void setCell(short entry, short cell) {
        viewEntries[entry].setCell(logicContext, cell);
    }

    public short getCell(short entry) {
        return viewEntries[entry].getCell();
    }

    public void setDirection(short entry, short direction) {
        viewEntries[entry].setDirection(direction);
    }

    public short getDirection(short entry) {
        return viewEntries[entry].getDirection();
    }

    public short getPriority(short entry) {
        return viewEntries[entry].getPriority();
    }

    public void setPriority(short entry, short priority) {
        ViewEntry v = viewEntries[entry];

        v.addFlags(ViewEntry.FLAG_FIX_PRIORITY);
        v.setPriority(priority);
    }

    public void releasePriority(short entry) {
        viewEntries[entry].removeFlags(entry);
    }

    public void moveObject(short entry, short x, short y, short stepSize, short flag) {
        ViewEntry v = viewEntries[entry];

        v.setMotionType(ViewEntry.MOTION_MOVEOBJECT);
        v.setEntry27(x);
        v.setEntry28(y);
        v.setEntry29(v.getStepSize());

        if (stepSize != 0) {
            v.setStepSize(stepSize);
        }

        v.setEntry2a(flag);
        logicContext.setFlag(flag, false);
        v.addFlags(ViewEntry.FLAG_UPDATE);

        if (entry == 0) {
            logicContext.setPlayerControl(false);
        }

        checkMotionMoveObject(v);
    }

    public void wanderObject(short entry) {
        ViewEntry v = viewEntries[entry];

        if (entry == 0) {
            logicContext.setPlayerControl(false);
        }

        v.setMotionType(ViewEntry.MOTION_WANDER);
        v.addFlags(ViewEntry.FLAG_UPDATE);
    }

    public void followEgo(short entry, short stepSize, short flag) {
        ViewEntry v = viewEntries[entry];

        v.setMotionType(ViewEntry.MOTION_FOLLOWEGO);

        if (stepSize <= v.getStepSize()) {
            v.setEntry27(v.getStepSize());
        } else {
            v.setEntry27(stepSize);
        }

        v.setEntry28(flag);
        logicContext.setFlag(flag, false);
        v.setEntry29((short) 0xff);
        v.addFlags(ViewEntry.FLAG_UPDATE);
    }

    public void animateObject(short entry) {
        ViewEntry v;

        if (entry >= MAX_VIEWENTRY) {
            logicContext.setError((short) 0xd);
        }

        v = viewEntries[entry];

        if (!v.isSomeFlagsSet(ViewEntry.FLAG_ANIMATE)) {
            v.setFlags((short) (ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_CYCLING | ViewEntry.FLAG_UPDATE));
            v.setMotionType(ViewEntry.MOTION_NORMAL);
            v.setCycleType(ViewEntry.CYCLE_NORMAL);
            v.setDirection(ViewEntry.DIRECTION_NONE);
        }
    }

    public void unanimateAll() {
        int i;

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            viewEntries[i].removeFlags(
                    ViewEntry.FLAG_ANIMATE |
                            ViewEntry.FLAG_DRAWN);
        }
    }

    public void observeBlocks(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_IGNORE_BLOCKS);
    }

    public void ignoreBlocks(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_IGNORE_BLOCKS);
    }

    public void observeHorizon(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_IGNORE_HORIZON);
    }

    public void ignoreHorizon(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_IGNORE_HORIZON);
    }

    public void observeObjects(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_IGNORE_OBJECTS);
    }

    public void ignoreObjects(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_IGNORE_OBJECTS);
    }

    public void forceUpdate(short entry) {
        ViewEntry v = viewEntries[entry];

        eraseBoth();
        blitBoth();
        checkMoveBoth();
    }

    public void normalCycling(short entry) {
        ViewEntry v = viewEntries[entry];

        v.setCycleType(ViewEntry.CYCLE_NORMAL);
        v.addFlags(ViewEntry.FLAG_CYCLING);
    }

    public void reverseCycling(short entry) {
        ViewEntry v = viewEntries[entry];

        v.setCycleType(ViewEntry.CYCLE_REVERSE);
        v.addFlags(ViewEntry.FLAG_CYCLING);
    }

    public void normalMotion(short entry) {
        viewEntries[entry].setMotionType(ViewEntry.MOTION_NORMAL);
    }

    public void endOfLoop(short entry, short flag) {
        ViewEntry v = viewEntries[entry];

        v.setCycleType(ViewEntry.CYCLE_ENDOFLOOP);
        v.addFlags(ViewEntry.FLAG_DONT_UPDATE | ViewEntry.FLAG_UPDATE | ViewEntry.FLAG_CYCLING);
        v.setEntry27(flag);

        logicContext.setFlag(flag, false);
    }

    public void reverseLoop(short entry, short flag) {
        ViewEntry v = viewEntries[entry];

        v.setCycleType(ViewEntry.CYCLE_REVERSELOOP);
        v.addFlags(ViewEntry.FLAG_DONT_UPDATE | ViewEntry.FLAG_UPDATE | ViewEntry.FLAG_CYCLING);
        v.setEntry27(flag);

        logicContext.setFlag(flag, false);
    }

    public void fixLoop(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_FIX_LOOP);
    }

    public void releaseLoop(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_FIX_LOOP);
    }

    public void startUpdate(short entry) {
        ViewEntry v = viewEntries[entry];

        if (!v.isSomeFlagsSet(ViewEntry.FLAG_UPDATE)) {
            eraseBoth();
            v.addFlags(ViewEntry.FLAG_UPDATE);
            blitBoth();
        }
    }

    public void stopUpdate(short entry) {
        ViewEntry v = viewEntries[entry];

        if (v.isSomeFlagsSet(ViewEntry.FLAG_UPDATE)) {
            eraseBoth();
            v.removeFlags(ViewEntry.FLAG_UPDATE);
            blitBoth();
        }
    }

    public void startMotion(short entry) {
        viewEntries[entry].setMotionType(ViewEntry.MOTION_NORMAL);

        if (entry == 0) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, ViewEntry.DIRECTION_NONE);
            logicContext.setPlayerControl(true);
        }
    }

    public void stopMotion(short entry) {
        ViewEntry v = viewEntries[entry];

        v.setDirection(ViewEntry.DIRECTION_NONE);
        v.setMotionType(ViewEntry.MOTION_NORMAL);

        if (entry == 0) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, ViewEntry.DIRECTION_NONE);
            logicContext.setPlayerControl(false);
        }
    }

    public void startCycling(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_CYCLING);
    }

    public void stopCycling(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_CYCLING);
    }

    public void onWater(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_ON_WATER);
    }

    public void onLand(short entry) {
        viewEntries[entry].addFlags(ViewEntry.FLAG_ON_LAND);
    }

    public void onAnything(short entry) {
        viewEntries[entry].removeFlags(ViewEntry.FLAG_ON_LAND | ViewEntry.FLAG_ON_WATER);
    }

    public short getCycleTime(short entry) {
        return viewEntries[entry].getCycleTime();
    }

    public void setCycleTime(short entry, short cycleTime) {
        ViewEntry v = viewEntries[entry];

        v.setCycleTime(cycleTime);
        v.setCycleTimeCount(cycleTime);
    }

    public short getStepSize(short entry) {
        return viewEntries[entry].getStepSize();
    }

    public void setStepSize(short entry, short stepSize) {
        viewEntries[entry].setStepSize(stepSize);
    }

    public short getStepTime(short entry) {
        return viewEntries[entry].getStepTime();
    }

    public void setStepTime(short entry, short stepTime) {
        ViewEntry v = viewEntries[entry];

        v.setStepTime(stepTime);
        v.setStepTimeCount(stepTime);
    }

    public ViewEntry getEntry(short entry) {
        return viewEntries[entry];
    }

    public void draw(short entry) {
        ViewEntry v;

        try {
            v = viewEntries[entry];
        } catch (IndexOutOfBoundsException oobex) {
            logicContext.setError((short) 0x13);
            return;
        }

        if (v.getCellData() == null) {
            logicContext.setError((short) 0x14);
        }

        if (!v.isSomeFlagsSet(ViewEntry.FLAG_DRAWN)) {
            v.addFlags(ViewEntry.FLAG_UPDATE);
            fixPosition(v);
            v.saveCell();
            v.savePosition();
            eraseAll(updateList);
            v.addFlags(ViewEntry.FLAG_DRAWN);
            blitAll(buildUpdateBlitList());
            commitView(v);
            v.removeFlags(ViewEntry.FLAG_DONT_UPDATE);
        }
    }

    public void erase(short entry) {
        ViewEntry v;
        boolean b;

        try {
            v = viewEntries[entry];
        } catch (IndexOutOfBoundsException oobex) {
            logicContext.setError((short) 0xc);
            return;
        }

        if (v.isSomeFlagsSet(ViewEntry.FLAG_DRAWN)) {
            eraseAll(updateList);
            b = !v.isSomeFlagsSet(ViewEntry.FLAG_UPDATE);

            if (b) {
                eraseAll(updateNotList);
            }

            v.removeFlags(ViewEntry.FLAG_DRAWN);

            if (b) {
                blitAll(buildUpdateNotBlitList());
            }

            blitAll(buildUpdateBlitList());
            commitView(v);
        }
    }

    public Point getPosition(short entry, Point p) {
        ViewEntry v = viewEntries[entry];

        p.x = v.getX();
        p.y = v.getY();

        return p;
    }

    public void setPosition(short entry, short x, short y) {
        ViewEntry v = viewEntries[entry];

        v.setX(x);
        v.setY(y);
        v.savePosition();
    }

    public void reposition(short entry, short x, short y) {
        ViewEntry v = viewEntries[entry];
        short t;

        v.addFlags(ViewEntry.FLAG_UPDATE_POS);

        t = v.getX();

        if ((x < 0) && (t < -x)) {
            v.setX((short) 0);
        } else {
            v.setX((short) (t + x));
        }

        t = v.getY();

        if ((y < 0) && (t < -y)) {
            v.setY((short) 0);
        } else {
            v.setY((short) (t + y));
        }

        fixPosition(v);
    }

    protected boolean checkPosition(ViewEntry v) {
        if ((v.getX() >= 0) &&
                ((v.getX() + v.getWidth()) <= WIDTH) &&
                ((v.getY() - v.getHeight()) >= -1) &&
                (v.getY() < HEIGHT)) {
            if (!v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_HORIZON)) {
                return v.getY() > logicContext.getHorizon();
            }

            return true;
        }

        return false;
    }

    protected boolean checkClutter(ViewEntry v) {
        int i;
        ViewEntry w;

        if (v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_OBJECTS)) {
            return false;
        }

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            w = viewEntries[i];

            if (!w.isAllFlagsSet(ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_DRAWN)) {
                continue;
            }

            if (w.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_OBJECTS)) {
                continue;
            }

            if (v == w) {
                continue;
            }

            if ((v.getX() + v.getWidth()) < w.getX()) {
                continue;
            }

            if (v.getX() > (w.getX() + w.getWidth())) {
                continue;
            }

            if (v.getY() == w.getY()) {
                return true;
            }

            if (v.getY() > w.getY()) {
                if (v.getYCopy() < w.getYCopy()) {
                    return true;
                }
            }

            if (v.getY() < w.getY()) {
                if (v.getYCopy() > w.getYCopy()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkPriority(ViewEntry v) {
        int i, o, width;
        byte b;
        boolean pass;
        boolean signal;
        boolean water;

        if (!v.isSomeFlagsSet(ViewEntry.FLAG_FIX_PRIORITY)) {
            v.setPriority(priorityTable[v.getY()]);
        }

        pass = true;
        signal = false;
        water = false;

        if (v.getPriority() != 0xf) {
            water = true;
            width = v.getWidth();
            o = (v.getY() * WIDTH) + v.getX();

            for (i = 0; i < width; i++, o++) {
                b = priority[o];

                if (b == 0) {
                    pass = true;
                    break;
                }

                if (b == 3) {
                    /* Water surface */
                    continue;
                }

                water = false;

                if (b == 1) {
                    /* Conditional blue */
                    if (v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_BLOCKS)) {
                        continue;
                    }

                    pass = false;
                    break;
                }

                if (b == 2) {
                    /* Signal */
                    signal = true;
                }
            }

            if (pass) {
                if (!water && v.isSomeFlagsSet(ViewEntry.FLAG_ON_WATER)) {
                    pass = false;
                }

                if (water && v.isSomeFlagsSet(ViewEntry.FLAG_ON_LAND)) {
                    pass = false;
                }
            }
        }

        if (v == viewEntries[0]) {
            logicContext.setFlag(LogicContext.FLAG_EGO_TOUCHED_ALERT, signal);
            logicContext.setFlag(LogicContext.FLAG_EGO_WATER, water);
        }

        return pass;
    }

    protected void fixPosition(ViewEntry v) {
        int dir, count, tries;

        if ((v.getY() <= logicContext.getHorizon()) && !v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_HORIZON)) {
            v.setY((short) (logicContext.getHorizon() + 1));
        }

        dir = 0;
        count = 1;
        tries = 1;

        while (!checkPosition(v) || checkClutter(v) || !checkPriority(v)) {
            switch (dir) {
                case 0:
                    v.setX((short) (v.getX() - 1));

                    if ((--count) != 0) {
                        continue;
                    }

                    dir = 1;
                    count = tries;
                    break;

                case 1:
                    v.setY((short) (v.getY() + 1));

                    if ((--count) != 0) {
                        continue;
                    }

                    count = ++tries;
                    break;

                case 2:
                    v.setX((short) (v.getX() + 1));

                    if ((--count) != 0) {
                        continue;
                    }

                    dir = 3;
                    count = tries;
                    break;

                case 3:
                    v.setY((short) (v.getY() - 1));

                    if ((--count) != 0) {
                        continue;
                    }

                    dir = 0;
                    count = ++tries;
                    break;
            }
        }
    }

    protected ViewList buildList(ViewList list, int listType) {
        ViewEntry[] vList = new ViewEntry[MAX_VIEWENTRY];
        short[] yList = new short[MAX_VIEWENTRY];
        int i, j, k;
        int minY, minIndex = 0, c = 0;
        ViewEntry v;
        ViewSprite head = null, sprite;

        list.prev = null;
        list.next = null;

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            v = viewEntries[i];

            switch (listType) {
                default:
                case 0:
                    if (!v.isAllFlagsSet(ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_UPDATE | ViewEntry.FLAG_DRAWN)) {
                        continue;
                    }
                    break;

                case 1:
                    if (v.getFlags(ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_UPDATE | ViewEntry.FLAG_DRAWN) != (ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_DRAWN)) {
                        continue;
                    }
                    break;
            }

            vList[c] = v;
            yList[c] = v.isSomeFlagsSet(ViewEntry.FLAG_FIX_PRIORITY) ? v.getY() : priorityToY(v.getPriority());
            c++;
        }

        for (j = 0; j < c; j++) {
            minY = 0xff;

            for (k = 0; k < c; k++) {
                if (yList[k] < minY) {
                    minIndex = k;
                    minY = yList[k];
                }
            }

            yList[minIndex] = 0xff;
            sprite = new ViewSprite(vList[minIndex]);
            sprite.prev = list.prev;

            if (list.prev != null) {
                sprite.prev.next = sprite;
            }

            list.prev = sprite;

            if (list.next == null) {
                list.next = sprite;
            }
        }

        return list;
    }

    protected ViewList buildUpdateBlitList() {
        return buildList(updateList, 0);
    }

    protected ViewList buildUpdateNotBlitList() {
        return buildList(updateNotList, 1);
    }

    protected void commitView(ViewEntry v) {
        if (picShown) {
        }
        /*
        Rectangle previousRect = new Rectangle();
        Rectangle currentRect  = new Rectangle();
        Cell      current      = v.getCellData();
        Cell      previous     = v.getPreviousCellData();

        currentRect.x       = v.getX();
        currentRect.y       = v.getY();
        currentRect.width   = current.getWidth();
        currentRect.height  = current.getHeight();

        previousRect.x      = v.getXCopy();
        previousRect.y      = v.getYCopy();
        previousRect.width  = previous.getWidth();
        previousRect.height = previous.getHeight();

        currentRect.y  = currentRect.y  - currentRect.height  - 1;
        previousRect.y = previousRect.y - previousRect.height - 1;

        if (currentRect.intersects(previousRect))
        {
            currentRect.add(previousRect);
        }
        else
        {
            screen.putBlock(screenView, previousRect.x, previousRect.y, previousRect.width, previousRect.height);
        }

        screen.putBlock(screenView, currentRect.x,  currentRect.y,  currentRect.width,  currentRect.height);
        */
        //screen.putBlock(screenView, 0, 0, WIDTH, HEIGHT);

        /*
            int  t;
            Cell current;  // di
            Cell previous; // si
            int  x1;       // ah
            int  y1;       // al
            int  x2;       // bl
            int  y2;       // bh
            int  height1;  // cl
            int  height2;  // ch
            int  width1;   // cl
            int  width2;   // ch

            current  = v.getCellData();
            previous = v.getPreviousCellData();
            v.saveCell();

            y1      = v.getY();
            y2      = v.getYCopy();
            height1 = current.getHeight();
            height2 = previous.getHeight();

            if (y1 < y2)
            {
                t       = y1;
                y1      = y2;
                y2      = t;
                t       = height1;
                height1 = height2;
                height2 = t;
            }

            y2      -= height2 + 1;
            height1  = (-height1) + y1 + 1;

            if (y2 > height1)
            {
                y2 = height1;
            }

            y2 = (-y2) + y1 + 1;

            x1     = v.getX();
            width1 = current.getWidth();
            x2     = v.getXCopy();
            width2 = previous.getWidth();

            if (x1 <= x2)
            {
                t       = x1;
                x1      = x2;
                x2      = t;
                t       = width1;
                width1  = width2;
                width2  = t;
            }

            width1 += x1;
            x2     += width2;

            if (x2 < width1)
            {
                x2 = width1;
            }

            x2 -= x1;
            screen.putBlock(screenView, x1, y1, x2, y2);
        }
        */
    }

    protected void eraseAll(ViewList list) {
        ViewSprite s;

        for (s = list.prev; s != null; s = s.prev) {
            s.restore(screenUpdate, screenView, priority);
        }
    }

    protected void eraseBoth() {
        eraseAll(updateList);
        eraseAll(updateNotList);
    }

    protected void blitAll(ViewList list) {
        ViewSprite s;

        for (s = list.next; s != null; s = s.next) {
            s.save(screenView, priority);
            s.blit(screenUpdate, screenView, priority);
        }
    }

    protected void blitBoth() {
        blitAll(buildUpdateNotBlitList());
        blitAll(buildUpdateBlitList());
    }

    protected void checkMove(ViewList list) {
        ViewSprite s = list.prev;
        ViewEntry v;

        for (; s != null; s = s.prev) {
            v = s.getViewEntry();

            commitView(v);
            v.checkMove();
        }
    }

    protected void checkMoveBoth() {
        checkMove(updateNotList);
        checkMove(updateList);
    }

    public void checkAllMotion() {
        int i;
        ViewEntry v;

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            v = viewEntries[i];

            if (v.isAllFlagsSet(ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_UPDATE | ViewEntry.FLAG_DRAWN) &&
                    (v.getStepTimeCount() == 1)) {
                checkMotion(v);
            }
        }
    }

    protected void checkMotion(ViewEntry v) {
        switch (v.getMotionType()) {
            case ViewEntry.MOTION_WANDER:
                checkMotionWander(v);
                break;
            case ViewEntry.MOTION_FOLLOWEGO:
                checkMotionFollowEgo(v);
                break;
            case ViewEntry.MOTION_MOVEOBJECT:
                checkMotionMoveObject(v);
                break;
        }

        if ((blockSet && (!v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_BLOCKS))) &&
                (v.getDirection() != 0)) {
            changePos(v);
        }
    }

    protected void checkMotionWander(ViewEntry v) {
        short entry27;
        int direction;

        entry27 = v.getEntry27();
        v.setEntry27((short) (entry27 - 1));

        if ((entry27 == 0) || v.isSomeFlagsSet(ViewEntry.FLAG_DIDNT_MOVE)) {
            direction = randomSeed.nextInt() % 9;

            if (direction < 0) {
                direction = -direction;
            }

            v.setDirection((short) direction);

            if (v == viewEntries[0]) {
                logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) direction);
            }

            if (v.getEntry27() < 6) {
                v.setEntry27((short) (randomSeed.nextInt() % 0x33));
            }
        }
    }

    public int getRandom() {
        return randomSeed.nextInt();
    }

    protected void checkMotionFollowEgo(ViewEntry obj) {
        ViewEntry ego = viewEntries[0];
        short egoX;
        short objX;
        short direction, n;

        egoX = (short) ((ego.getWidth() / 2) + ego.getX());
        objX = (short) ((obj.getWidth() / 2) + obj.getX());

        direction = getDirection(egoX, ego.getY(), objX, obj.getY(), obj.getEntry27());

        if (direction == ViewEntry.DIRECTION_NONE) {
            obj.setDirection(ViewEntry.DIRECTION_NONE);
            obj.setMotionType(ViewEntry.MOTION_NORMAL);
            logicContext.setFlag(obj.getEntry28(), true);
            return;
        }

        if (obj.getEntry29() == (short) 0xff) {
            obj.setEntry29((short) 0);
        } else if (obj.isSomeFlagsSet(ViewEntry.FLAG_DIDNT_MOVE)) {
            obj.setDirection((short) ((randomSeed.nextInt() % 8) + 1));

            n = (short) (((absolute(obj.getY() - ego.getY()) + absolute(obj.getX() - ego.getX())) / 2) + 1);

            if (n <= obj.getStepSize()) {
                obj.setEntry29(obj.getStepSize());
                return;
            } else {
                do {
                    obj.setEntry29((short) (randomSeed.nextInt() % n));
                } while (obj.getEntry29() < obj.getStepSize());

                return;
            }
        }

        if (obj.getEntry29() != (short) 0) {
            obj.setEntry29((short) (obj.getEntry29() - obj.getStepSize()));

            if (obj.getEntry29() < (short) 0) {
                obj.setEntry29((short) 0);
            }
        } else {
            obj.setDirection(direction);
        }
    }

    protected void checkMotionMoveObject(ViewEntry v) {
        v.setDirection(getDirection(v.getX(), v.getY(), v.getEntry27(), v.getEntry28(), v.getStepSize()));

        if (v == viewEntries[0]) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, v.getDirection());
        }

        if (v.getDirection() == ViewEntry.DIRECTION_NONE) {
            inDestination(v);
        }
    }

    protected void inDestination(ViewEntry v) {
        v.setStepSize(v.getEntry29());
        logicContext.setFlag(v.getEntry2a(), true);
        v.setMotionType(ViewEntry.MOTION_NORMAL);

        if (v == viewEntries[0]) {
            logicContext.setPlayerControl(true);
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) 0);
        }
    }

    protected void changePos(ViewEntry v) {
        int x, y, s;
        boolean b;

        s = v.getStepSize();
        x = v.getX();
        y = v.getY();
        b = checkBlock(x, y);

        switch (v.getDirection()) {
            case ViewEntry.DIRECTION_NE:
                x += s;
            case ViewEntry.DIRECTION_N:
                y -= s;
                break;

            case ViewEntry.DIRECTION_E:
                x += s;
                break;

            case ViewEntry.DIRECTION_SE:
                x += s;
                y += s;
                break;

            case ViewEntry.DIRECTION_SW:
                x -= s;
            case ViewEntry.DIRECITON_S:
                y += s;
                break;

            case ViewEntry.DIRECTION_W:
                x -= s;
                break;

            case ViewEntry.DIRECTION_NW:
                x -= s;
                y -= s;
                break;
        }

        if (checkBlock(x, y) == b) {
            v.removeFlags(ViewEntry.FLAG_MOTION);
        } else {
            v.addFlags(ViewEntry.FLAG_MOTION);
            v.setDirection((short) 0);

            if (v == viewEntries[0]) {
                logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) 0);
            }
        }
    }

    public short lastCell(short entry) {
        return (short) (viewEntries[entry].getLoopData().getCellCount() - 1);
    }

    public short lastLoop(short entry) {
        return viewEntries[entry].getViewData().getLoopCount();
    }

    protected boolean checkBlock(int x, int y) {
        return ((x >= blockX1) &&
                (x <= blockX2) &&
                (y >= blockY1) &&
                (y <= blockY2));
    }

    protected short getDirection(short x, short y, short destX, short destY, short stepSize) {
        int s, t;

        s = checkStep((short) (destX - x), stepSize);
        t = checkStep((short) (destY - y), stepSize);

        return directionTable[s + (3 * t)];
    }

    protected short checkStep(short delta, short stepSize) {
        if (-stepSize >= delta) {
            return (short) 0;
        }

        if (stepSize <= delta) {
            return (short) 2;
        }

        return (short) 1;
    }

    public void update() {
        ViewEntry v;
        int i, c, m;
        short n;

        for (i = 0, c = 0; i < MAX_VIEWENTRY; i++) {
            v = viewEntries[i];

            if (v.isAllFlagsSet(ViewEntry.FLAG_DRAWN | ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_UPDATE)) {
                c++;
                n = (short) 4;

                if (!v.isSomeFlagsSet(ViewEntry.FLAG_FIX_LOOP)) {
                    m = v.getLoopCount();

                    if ((m == 2) || (m == 3)) {
                        n = directionTable2[v.getDirection()];
                    } else if (m == 4) {
                        n = directionTable4[v.getDirection()];
                    }
                }

                if (v.getStepTimeCount() == 1) {
                    if (n != 4) {
                        if (n != v.getLoop()) {
                            v.setLoop(logicContext, n);
                        }
                    }
                }

                if (v.isSomeFlagsSet(ViewEntry.FLAG_CYCLING)) {
                    if (v.getCycleTimeCount() != 0) {
                        v.setCycleTimeCount((short) (v.getCycleTimeCount() - 1));

                        if (v.getCycleTimeCount() == 0) {
                            v.update(logicContext);
                            v.setCycleTimeCount(v.getCycleTime());
                        }
                    }
                }
            }
        }

        if (c > 0) {
            eraseAll(updateList);
            updatePosition();
            blitAll(buildUpdateBlitList());
            checkMove(updateList);

            viewEntries[0].removeFlags(ViewEntry.FLAG_ON_LAND | ViewEntry.FLAG_ON_WATER);
        }
    }

    public void updatePosition() {
        ViewEntry v;
        int i, x, y, oldX, oldY, dir, step, border;
        short n;

        logicContext.setVar(LogicContext.VAR_BORDER_CODE, (short) 0);
        logicContext.setVar(LogicContext.VAR_EGO_TOUCHING, (short) 0);
        logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, (short) 0);

        for (i = 0; i < MAX_VIEWENTRY; i++) {
            v = viewEntries[i];

            if (v.isAllFlagsSet(ViewEntry.FLAG_DRAWN | ViewEntry.FLAG_ANIMATE | ViewEntry.FLAG_CYCLING)) {
                n = v.getStepTimeCount();

                if (n != 0) {
                    v.setStepTimeCount(--n);

                    if (n != 0) {
                        continue;
                    }
                }

                v.setStepTimeCount(v.getStepTime());

                x = oldX = v.getX();
                y = oldY = v.getY();

                if (!v.isSomeFlagsSet(ViewEntry.FLAG_UPDATE_POS)) {
                    dir = v.getDirection();
                    step = v.getStepSize();

                    x += (step * directionTableX[dir]);
                    y += (step * directionTableY[dir]);
                }

                border = 0;

                if (x < 0) {
                    x = 0;
                    border = 4;
                } else if ((x + v.getWidth()) > WIDTH) {
                    x = 160 - v.getWidth();
                    border = 2;
                }

                if ((y - v.getHeight() + 1) < 0) {
                    y = v.getHeight() - 1;
                    border = 1;
                } else if (y > (HEIGHT - 1)) {
                    y = HEIGHT - 1;
                    border = 3;
                } else if (!v.isSomeFlagsSet(ViewEntry.FLAG_IGNORE_HORIZON) && y <= logicContext.getHorizon()) {
                    y++;
                    border = 1;
                }

                v.setX((short) x);
                v.setY((short) y);

                if (checkClutter(v) || !checkPriority(v)) {
                    v.setX((short) oldX);
                    v.setY((short) oldY);
                    border = 0;
                    fixPosition(v);
                }

                if (border != 0) {
                    if (i == 0) {
                        logicContext.setVar(LogicContext.VAR_EGO_TOUCHING, (short) border);
                    } else {
                        logicContext.setVar(LogicContext.VAR_BORDER_CODE, (short) i);
                        logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, (short) border);
                    }

                    if (v.getMotionType() == ViewEntry.MOTION_MOVEOBJECT) {
                        inDestination(v);
                    }
                }
            }

            v.removeFlags(ViewEntry.FLAG_UPDATE_POS);
        }
    }

    public void doUpdate() {
        Rectangle r;

        if (!screenUpdate.isEmpty()) {
            r = screenUpdate.getBounds();

            screen.putBlock(screenView, r.x, r.y, r.width, r.height);
        }

        screenUpdate.reset();
    }
}