/*
 *  ViewEntry.java
 *  Adventre Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicVariables;

public class AnimatedObject {
    public static final short DIRECTION_NONE = (short) 0;
    public static final short DIRECTION_N = (short) 1;
    public static final short DIRECTION_NE = (short) 2;
    public static final short DIRECTION_E = (short) 3;
    public static final short DIRECTION_SE = (short) 4;
    public static final short DIRECITON_S = (short) 5;
    public static final short DIRECTION_SW = (short) 6;
    public static final short DIRECTION_W = (short) 7;
    public static final short DIRECTION_NW = (short) 8;
    public static final short MOTION_NORMAL = (short) 0;
    public static final short MOTION_WANDER = (short) 1;
    public static final short MOTION_FOLLOWEGO = (short) 2;
    public static final short MOTION_MOVEOBJECT = (short) 3;
    public static final short CYCLE_NORMAL = (short) 0;
    public static final short CYCLE_ENDOFLOOP = (short) 1;
    public static final short CYCLE_REVERSELOOP = (short) 2;
    public static final short CYCLE_REVERSE = (short) 3;
    public static final short FLAG_DRAWN = (short) 0x0001;
    public static final short FLAG_IGNORE_BLOCKS = (short) 0x0002;
    public static final short FLAG_FIX_PRIORITY = (short) 0x0004;
    public static final short FLAG_IGNORE_HORIZON = (short) 0x0008;
    public static final short FLAG_UPDATE = (short) 0x0010;
    public static final short FLAG_CYCLING = (short) 0x0020;
    public static final short FLAG_ANIMATE = (short) 0x0040;
    public static final short FLAG_MOTION = (short) 0x0080;
    public static final short FLAG_ON_WATER = (short) 0x0100;
    public static final short FLAG_IGNORE_OBJECTS = (short) 0x0200;
    public static final short FLAG_UPDATE_POS = (short) 0x0400;
    public static final short FLAG_ON_LAND = (short) 0x0800;
    public static final short FLAG_DONT_UPDATE = (short) 0x1000;
    public static final short FLAG_FIX_LOOP = (short) 0x2000;
    public static final short FLAG_DIDNT_MOVE = (short) 0x4000;
    protected short stepTime;          // 0
    protected short stepTimeCount;     // 1
    protected int objectNumber;        // 2
    protected short x;                 // 3-4
    protected short y;                 // 5-6
    protected short currentView;       // 7
    protected View currentViewData;   // 8-9
    protected short currentLoop;       // a
    protected Loop currentLoopData;   // c-d
    protected short currentCell;       // e
    protected Cel currentCelData;   // 10-11
    protected Cel previousCelData;  // 12-13
    protected short xCopy;             // 16-17
    protected short yCopy;             // 18-19
    protected short width;             // 1a
    protected short height;            // 1c
    protected short stepSize;          // 1e
    protected short cycleTime;         // 1f
    protected short cycleTimeCount;    // 20
    protected short direction;         // 21
    protected short motionType;        // 22
    protected short cycleType;         // 23
    protected short priority;          // 24
    protected int flags;             // 25
    private short targetX;           // 27
    protected short targetY;           // 28
    protected short oldStepSize;           // 29
    protected short endFlag;           // Flag to set when this AnimatedObject reaches the target position.

    public AnimatedObject(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public static String flagsToString(short flags) {
        StringBuffer s = new StringBuffer();

        if ((flags & FLAG_DRAWN) != 0) {
            s.append("drawn,");
        }

        if ((flags & FLAG_IGNORE_BLOCKS) != 0) {
            s.append("ignoreblocks,");
        }

        if ((flags & FLAG_FIX_PRIORITY) != 0) {
            s.append("fixpriority,");
        }

        if ((flags & FLAG_IGNORE_HORIZON) != 0) {
            s.append("ignorehorizon,");
        }

        if ((flags & FLAG_UPDATE) != 0) {
            s.append("update,");
        }

        if ((flags & FLAG_CYCLING) != 0) {
            s.append("cycling,");
        }

        if ((flags & FLAG_ANIMATE) != 0) {
            s.append("animate,");
        }

        if ((flags & FLAG_MOTION) != 0) {
            s.append("motion,");
        }

        if ((flags & FLAG_ON_WATER) != 0) {
            s.append("onwater,");
        }

        if ((flags & FLAG_IGNORE_OBJECTS) != 0) {
            s.append("ignoreobjects,");
        }

        if ((flags & FLAG_UPDATE_POS) != 0) {
            s.append("updatepos,");
        }

        if ((flags & FLAG_ON_LAND) != 0) {
            s.append("onland,");
        }

        if ((flags & FLAG_DONT_UPDATE) != 0) {
            s.append("dontupdate,");
        }

        if ((flags & FLAG_FIX_LOOP) != 0) {
            s.append("fixloop,");
        }

        if ((flags & FLAG_DIDNT_MOVE) != 0) {
            s.append("didntmove,");
        }

        if (s.length() > 0) {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();
    }

    public void reset() {
        stepTime = 0;
        x = 0;
        y = 0;
        currentView = 0;
        currentViewData = null;
        currentLoop = 0;
        currentLoopData = null;
        currentCell = 0;
        currentCelData = null;
        xCopy = 0;
        yCopy = 0;
        width = 0;
        height = 0;
        stepSize = 0;
        cycleTime = 0;
        direction = 0;
        motionType = 0;
        cycleType = 0;
        priority = 0;
        flags = 0;
        targetX = 0;
        targetY = 0;
        oldStepSize = 0;
        endFlag = 0;
    }

    public Cel getCellData() {
        return currentCelData;
    }

    public void saveCell() {
        previousCelData = currentCelData;
    }

    public Cel getPreviousCellData() {
        return previousCelData;
    }

    public short getCell() {
        return currentCell;
    }

    public Loop getLoopData() {
        return currentLoopData;
    }

    public short getLoop() {
        return currentLoop;
    }

    public View getViewData() {
        return currentViewData;
    }

    public short getView() {
        return currentView;
    }

    public short getLoopCount() {
        return currentViewData.getLoopCount();
    }

    public short getCellCount() {
        return currentLoopData.getCellCount();
    }

    public void setView(LogicContext logicContext, short view) {
        View viewData;

        try {
            viewData = logicContext.getCache().getView(view);
        } catch (Exception ex) {
            System.err.println("view does not exists: " + view);
            ex.printStackTrace();
            logicContext.setError((short) 0x3);
            return;
        }

        currentViewData = viewData;
        currentView = view;

        if (currentLoop >= currentViewData.getLoopCount()) {
            setLoop(logicContext, (short) 0);
        } else {
            setLoop(logicContext, currentLoop);
        }
    }

    public void setLoop(LogicContext logicContext, short loop) {
        currentLoop = loop;

        try {
            currentLoopData = currentViewData.getLoop(loop);

            if (currentCell >= currentLoopData.getCellCount()) {
                currentCell = 0;
            }
        } catch (Exception e) {
            // in kq1, set.loop is called before set.view
            currentCell = 0;
        }

        setCell(logicContext, currentCell);
    }

    public void setCell(LogicContext logicContext, short cell) {
        if (cell >= currentLoopData.getCellCount()) {
            logicContext.setError((short) 0x8);
        }

        currentCell = cell;
        currentCelData = currentLoopData.getCell(cell);
        width = currentCelData.getWidth();
        height = currentCelData.getHeight();

        if ((x + width) > ViewTable.WIDTH) {
            x = (short) (ViewTable.WIDTH - width);
            flags |= FLAG_UPDATE_POS;
        }

        if (((y - height) + 1) < 0) {
            y = (short) (y + height);
            flags |= FLAG_UPDATE_POS;

            if (y <= logicContext.getHorizon() && ((flags & FLAG_IGNORE_HORIZON) == 0)) {
                y = (short) (logicContext.getHorizon() + 1);
            }
        }
    }

    public short getStepTime() {
        return stepTime;
    }

    public void setStepTime(short stepTime) {
        this.stepTime = stepTime;
    }

    public short getStepTimeCount() {
        return stepTimeCount;
    }

    public void setStepTimeCount(short stepTimeCount) {
        this.stepTimeCount = stepTimeCount;
    }

    public short getDirection() {
        return direction;
    }

    public void setDirection(short direction) {
        this.direction = direction;
    }

    public short getStepSize() {
        return stepSize;
    }

    public void setStepSize(short stepSize) {
        this.stepSize = stepSize;
    }

    public short getFlags() {
        return (short) flags;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public int getFlags(int flags) {
        return this.flags & flags;
    }

    public void addFlags(int flags) {
        this.flags |= flags;
    }

    public void removeFlags(int flags) {
        this.flags &= ~flags;
    }

    public boolean isSomeFlagsSet(int flags) {
        return (this.flags & flags) != 0;
    }

    public boolean isAllFlagsSet(int flags) {
        return (this.flags & flags) == flags;
    }

    public void checkMove() {
        if (stepTime == stepTimeCount) {
            if ((x == xCopy) && (y == yCopy)) {
                addFlags(FLAG_DIDNT_MOVE);
            } else {
                savePosition();
                removeFlags(FLAG_DIDNT_MOVE);
            }
        }
    }

    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getXCopy() {
        return xCopy;
    }

    public short getWidth() {
        return width;
    }

    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public short getYCopy() {
        return yCopy;
    }

    public short getHeight() {
        return height;
    }

    public int getPositionOffset() {
        return (y * ViewTable.WIDTH) + x;
    }

    public void savePosition() {
        xCopy = x;
        yCopy = y;
    }

    public short getMotionType() {
        return motionType;
    }

    public void setMotionType(short motionType) {
        this.motionType = motionType;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public short getCycleType() {
        return cycleType;
    }

    public void setCycleType(short cycleType) {
        this.cycleType = cycleType;
    }

    public short getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(short cycleTime) {
        this.cycleTime = cycleTime;
    }

    public short getCycleTimeCount() {
        return cycleTimeCount;
    }

    public void setCycleTimeCount(short cycleTimeCount) {
        this.cycleTimeCount = cycleTimeCount;
    }

    public short getTargetX() {
        return targetX;
    }

    public void setTargetX(short targetX) {
        this.targetX = targetX;
    }

    public short getTargetY() {
        return targetY;
    }

    public void setTargetY(short targetY) {
        this.targetY = targetY;
    }

    public short getOldStepSize() {
        return oldStepSize;
    }

    public void setOldStepSize(short entry29) {
        this.oldStepSize = entry29;
    }

    public short getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(short flag) {
        this.endFlag = flag;
    }

    public boolean inPos(int x1, int y1, int x2, int y2) {
        return (x >= x1) &&
                (y >= y1) &&
                (x <= x2) &&
                (y <= y2);
    }

    public boolean inBox(int x1, int y1, int x2, int y2) {
        return (x >= x1) &&
                (y >= y1) &&
                ((x + width - 1) <= x2) &&
                (y <= y2);
    }

    public boolean inCentre(int x1, int y1, int x2, int y2) {
        return ((x + width / 2) >= x1) &&
                ((x + width / 2) <= x2) &&
                (y >= y1) &&
                (y <= y2);
    }

    public boolean inRight(int x1, int y1, int x2, int y2) {
        return ((x + width - 1) >= x1) &&
                ((x + width - 1) <= x2) &&
                (y >= y1) &&
                (y <= y2);
    }

    public void update(LogicContext logicContext) {
        short cell, cellLast;

        if (isSomeFlagsSet(AnimatedObject.FLAG_DONT_UPDATE)) {
            removeFlags(AnimatedObject.FLAG_DONT_UPDATE);
            return;
        }

        cell = this.currentCell;
        cellLast = (short) (currentLoopData.getCellCount() - 1);

        switch (cycleType) {
            case CYCLE_NORMAL:
                if (++cell > cellLast) {
                    cell = 0;
                }
                break;

            case CYCLE_ENDOFLOOP:
                if (cell < cellLast) {
                    if (++cell != cellLast) {
                        break;
                    }

                    logicContext.setFlag(targetX, true);
                    removeFlags(FLAG_CYCLING);
                    direction = DIRECTION_NONE;
                    cycleType = CYCLE_NORMAL;
                }
                break;

            case CYCLE_REVERSELOOP:
                if (cell == 0) {
                    logicContext.setFlag(targetX, true);
                    removeFlags(FLAG_CYCLING);
                    direction = DIRECTION_NONE;
                    cycleType = CYCLE_NORMAL;
                } else {
                    cell--;
                }
                break;

            case CYCLE_REVERSE:
                if (cell == 0) {
                    cell = cellLast;
                } else {
                    cell--;
                }
                break;
        }

        setCell(logicContext, cell);
    }

    public void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

    public void setCurrentView(short currentView) {
        this.currentView = currentView;
    }

    public void setCurrentLoop(short currentLoop) {
        this.currentLoop = currentLoop;
    }

    public void setCurrentCell(short currentCell) {
        this.currentCell = currentCell;
    }

    public View getCurrentViewData() {
        return currentViewData;
    }

    public void setPreviousCellData(Cel previousCelData) {
        this.previousCelData = previousCelData;
    }

    public Cel getCurrentCellData() {
        return currentCelData;
    }

    public void setxCopy(short xCopy) {
        this.xCopy = xCopy;
    }

    public void setyCopy(short yCopy) {
        this.yCopy = yCopy;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    public short getCurrentView() {
        return currentView;
    }

    public short getCurrentLoop() {
        return currentLoop;
    }

    public short getCurrentCell() {
        return currentCell;
    }

    public short getxCopy() {
        return xCopy;
    }

    public short getyCopy() {
        return yCopy;
    }

    public short effectiveY() {
        if (this.isSomeFlagsSet(AnimatedObject.FLAG_FIX_PRIORITY)) {
            return (short)(LogicVariables.PRIORITY_BASE + Math.ceil(((168.0 - LogicVariables.PRIORITY_BASE) / 10.0f) * (this.priority - 4 - 1)));
        }

        return y;
    }
}
