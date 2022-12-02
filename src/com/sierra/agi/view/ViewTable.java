/*
 *  ViewTable.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicVariables;
import com.sierra.agi.pic.Picture;
import com.sierra.agi.pic.PictureContext;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.res.ResourceException;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.sierra.agi.logic.LogicVariables.FLAG_OUTPUT_MODE;

public class ViewTable {
    public static final int MAX_ANIMATED_OBJECTS = 32;
    public static final short EGO_ENTRY = (short) 0;
    public static final int WIDTH = 160;
    public static final int HEIGHT = 168;
    protected static final short[] directionTable = new short[]{
            8, 1, 2,
            7, 0, 3,
            6, 5, 4};
    protected static final short[] directionTable2 = new short[]{
            4, 4, 0, 0, 0,
            4, 1, 1, 1, 0};
    protected static final short[] directionTable4 = new short[]{
            4, 3, 0, 0, 0,
            2, 1, 1, 1, 0};
    protected static final int[] directionTableX = new int[]{0, 0, 1, 1, 1, 0, -1, -1, -1};
    protected static final int[] directionTableY = new int[]{0, -1, -1, 0, 1, 1, 1, 0, -1};
    protected LogicContext logicContext;
    private AnimatedObject[] animatedObjects;
    protected boolean picShown;
    protected boolean blockSet;
    protected short blockUpperLeftX;
    protected short blockLowerRightX;
    protected short blockUpperLeftY;
    protected short blockLowerRightY;
    protected Random randomSeed;
    protected byte[] priorityTable = new byte[HEIGHT];
    protected ViewScreen viewScreen;
    private int[] picturePixels;
    private List<ViewSprite> updateList = new ArrayList<>();
    private List<ViewSprite> updateNotList = new ArrayList<>();
    protected PictureContext pictureContext;
    protected int[] pixel = new int[1];

    private Area screenUpdate;

    private int[] priorityPixels = new int[WIDTH * HEIGHT];

    public ViewTable(LogicContext context) {
        logicContext = context;
        animatedObjects = new AnimatedObject[MAX_ANIMATED_OBJECTS];

        for (int i = 0; i < animatedObjects.length; i++) {
            animatedObjects[i] = new AnimatedObject(i);
        }

        randomSeed = new Random();
        resetPriorityBands();

        viewScreen = new ViewScreen();
        picturePixels = new int[WIDTH * HEIGHT];
        screenUpdate = new Area(new Rectangle(0, 0, WIDTH, HEIGHT));
    }

    public static short priorityToY(int priority) {
        return (short) (((priority - 5) * 12) + 48);
    }

    protected static int absolute(int n) {
        return (n < 0) ? -n : n;
    }

    public void reset() {
        blockSet = false;
        picShown = false;

        for (int i = 0; i < animatedObjects.length; i++) {
            animatedObjects[i].reset();
        }

        resetPriorityBands();

        viewScreen.reset();
        screenUpdate.add(new Area(new Rectangle(0, 0, WIDTH, HEIGHT)));
        Arrays.fill(picturePixels, translatePixel((byte) 0));
        Arrays.fill(priorityPixels, (byte) 4);
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
        viewScreen.reset();
        updateList.clear();
        updateNotList.clear();
        for (int i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            AnimatedObject v = animatedObjects[i];

            v.removeFlags(AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_DRAWN);
            v.addFlags(AnimatedObject.FLAG_UPDATE);
            v.setStepTime((short) 1);
            v.setStepTimeCount((short) 1);
            v.setCycleTime((short) 1);
            v.setCycleTimeCount((short) 1);
            v.setStepSize((short) 1);
        }
    }

    public void drawPic(Picture picture) throws PictureException {
        pictureContext = picture.draw();
        System.arraycopy(pictureContext.getPictureData(), 0, picturePixels, 0, picturePixels.length);
        System.arraycopy(pictureContext.getPriorityData(), 0, priorityPixels, 0, priorityPixels.length);
    }

    public void overlayPic(Picture picture) throws PictureException {
        picture.draw(pictureContext);
    }

    public void addToPic(short viewNumber, short loopNumber, short cellNumber, short x, short y, byte priority, short controlBoxColour) throws ResourceException, IOException, ViewException {
        Cel cel = logicContext.getCache().getView(viewNumber).getLoop(loopNumber).getCell(cellNumber);
        logicContext.getScriptBuffer().addScript(ScriptBuffer.ScriptBufferEventType.AddToPic, 0, new byte[]{
                (byte) viewNumber, (byte) loopNumber, (byte) cellNumber, (byte) x, (byte) y, (byte) (priority | (controlBoxColour << 4))
        });
        pictureContext.addToPic(cel, x, y, priority, controlBoxColour);
    }

    public void showPic() {
        logicContext.setFlag(FLAG_OUTPUT_MODE, false);
        System.arraycopy(pictureContext.getPictureData(), 0, picturePixels, 0, picturePixels.length);
        System.arraycopy(pictureContext.getPriorityData(), 0, priorityPixels, 0, priorityPixels.length);

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
        return animatedObjects[entry].inBox(x1, y1, x2, y2);
    }

    public boolean inPos(short entry, short x1, short y1, short x2, short y2) {
        return animatedObjects[entry].inPos(x1, y1, x2, y2);
    }

    public boolean inCentre(short entry, short x1, short y1, short x2, short y2) {
        return animatedObjects[entry].inCentre(x1, y1, x2, y2);
    }

    public boolean inRight(short entry, short x1, short y1, short x2, short y2) {
        return animatedObjects[entry].inCentre(x1, y1, x2, y2);
    }

    public short distance(short s, short d) {
        AnimatedObject si = animatedObjects[s];
        AnimatedObject di = animatedObjects[d];

        if (si.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN) && di.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN)) {
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
        blockUpperLeftX = x1;
        blockUpperLeftY = y1;
        blockLowerRightX = x2;
        blockLowerRightY = y2;
    }

    public void resetBlock() {
        blockSet = false;
    }

    public ViewScreen getViewScreen() {
        return viewScreen;
    }

    public void setView(short entry, short view) {
        animatedObjects[entry].setView(logicContext, view);
    }

    public short getView(short entry) {
        return animatedObjects[entry].getView();
    }

    public void setLoop(short entry, short loop) {
        animatedObjects[entry].setLoop(logicContext, loop);
    }

    public short getLoop(short entry) {
        return animatedObjects[entry].getLoop();
    }

    public void setCell(short entry, short cell) {
        animatedObjects[entry].setCell(logicContext, cell);
    }

    public short getCell(short entry) {
        return animatedObjects[entry].getCell();
    }

    public void setDirection(short entry, short direction) {
        animatedObjects[entry].setDirection(direction);
    }

    public short getDirection(short entry) {
        return animatedObjects[entry].getDirection();
    }

    public short getPriority(short entry) {
        return animatedObjects[entry].getPriority();
    }

    public void setPriority(short entry, short priority) {
        AnimatedObject v = animatedObjects[entry];

        v.addFlags(AnimatedObject.FLAG_FIX_PRIORITY);
        v.setPriority(priority);
    }

    public void releasePriority(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_FIX_PRIORITY);
    }

    public void moveObject(short entry, short x, short y, short stepSize, short flag) {
        AnimatedObject v = animatedObjects[entry];

        v.setMotionType(AnimatedObject.MOTION_MOVEOBJECT);
        v.setTargetX(x);
        v.setTargetY(y);
        v.setOldStepSize(v.getStepSize());

        if (stepSize != 0) {
            v.setStepSize(stepSize);
        }

        v.setEndFlag(flag);
        logicContext.setFlag(flag, false);
        v.addFlags(AnimatedObject.FLAG_UPDATE);

        if (entry == 0) {
            logicContext.setPlayerControl(false);
        }

        checkMotionMoveObject(v);
    }

    public void wanderObject(short entry) {
        AnimatedObject v = animatedObjects[entry];

        if (entry == 0) {
            logicContext.setPlayerControl(false);
        }

        v.setMotionType(AnimatedObject.MOTION_WANDER);
        v.addFlags(AnimatedObject.FLAG_UPDATE);
    }

    public void followEgo(short entry, short stepSize, short flag) {
        AnimatedObject v = animatedObjects[entry];

        v.setMotionType(AnimatedObject.MOTION_FOLLOWEGO);

        if (stepSize <= v.getStepSize()) {
            v.setTargetX(v.getStepSize());
        } else {
            v.setTargetX(stepSize);
        }

        v.setTargetY(flag);
        logicContext.setFlag(flag, false);
        v.setOldStepSize((short) 0xff);
        v.addFlags(AnimatedObject.FLAG_UPDATE);
    }

    public void animateObject(short entry) {
        if (entry >= MAX_ANIMATED_OBJECTS) {
            logicContext.setError((short) 0xd);
        }

        AnimatedObject v = animatedObjects[entry];

        if (!v.isSomeFlagsSet(AnimatedObject.FLAG_ANIMATE)) {
            v.setFlags((short) (AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_CYCLING | AnimatedObject.FLAG_UPDATE));
            v.setMotionType(AnimatedObject.MOTION_NORMAL);
            v.setCycleType(AnimatedObject.CYCLE_NORMAL);
            v.setDirection(AnimatedObject.DIRECTION_NONE);
        }
    }

    public void unanimateAll() {
        int i;

        for (i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            animatedObjects[i].removeFlags(
                    AnimatedObject.FLAG_ANIMATE |
                            AnimatedObject.FLAG_DRAWN);
        }
    }

    public void observeBlocks(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_IGNORE_BLOCKS);
    }

    public void ignoreBlocks(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_IGNORE_BLOCKS);
    }

    public void observeHorizon(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_IGNORE_HORIZON);
    }

    public void ignoreHorizon(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_IGNORE_HORIZON);
    }

    public void observeObjects(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_IGNORE_OBJECTS);
    }

    public void ignoreObjects(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_IGNORE_OBJECTS);
    }

    public void forceUpdate() {
        restoreBackgrounds();
        blitBoth();
        checkMoveBoth();
    }

    public void normalCycling(short entry) {
        AnimatedObject v = animatedObjects[entry];

        v.setCycleType(AnimatedObject.CYCLE_NORMAL);
        v.addFlags(AnimatedObject.FLAG_CYCLING);
    }

    public void reverseCycling(short entry) {
        AnimatedObject v = animatedObjects[entry];

        v.setCycleType(AnimatedObject.CYCLE_REVERSE);
        v.addFlags(AnimatedObject.FLAG_CYCLING);
    }

    public void normalMotion(short entry) {
        animatedObjects[entry].setMotionType(AnimatedObject.MOTION_NORMAL);
    }

    public void endOfLoop(short entry, short flag) {
        AnimatedObject v = animatedObjects[entry];

        v.setCycleType(AnimatedObject.CYCLE_ENDOFLOOP);
        v.addFlags(AnimatedObject.FLAG_DONT_UPDATE | AnimatedObject.FLAG_UPDATE | AnimatedObject.FLAG_CYCLING);
        v.setTargetX(flag);

        logicContext.setFlag(flag, false);
    }

    public void reverseLoop(short entry, short flag) {
        AnimatedObject v = animatedObjects[entry];

        v.setCycleType(AnimatedObject.CYCLE_REVERSELOOP);
        v.addFlags(AnimatedObject.FLAG_DONT_UPDATE | AnimatedObject.FLAG_UPDATE | AnimatedObject.FLAG_CYCLING);
        v.setTargetX(flag);

        logicContext.setFlag(flag, false);
    }

    public void fixLoop(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_FIX_LOOP);
    }

    public void releaseLoop(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_FIX_LOOP);
    }

    public void startUpdate(short entry) {
        AnimatedObject v = animatedObjects[entry];

        if (!v.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE)) {
            restoreBackgrounds();
            v.addFlags(AnimatedObject.FLAG_UPDATE);
            blitBoth();
        }
    }

    public void stopUpdate(short entry) {
        AnimatedObject v = animatedObjects[entry];

        if (v.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE)) {
            restoreBackgrounds();
            v.removeFlags(AnimatedObject.FLAG_UPDATE);
            blitBoth();
        }
    }

    public void startMotion(short entry) {
        animatedObjects[entry].setMotionType(AnimatedObject.MOTION_NORMAL);

        if (entry == 0) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, AnimatedObject.DIRECTION_NONE);
            logicContext.setPlayerControl(true);
        }
    }

    public void stopMotion(short entry) {
        AnimatedObject v = animatedObjects[entry];

        v.setDirection(AnimatedObject.DIRECTION_NONE);
        v.setMotionType(AnimatedObject.MOTION_NORMAL);

        if (entry == 0) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, AnimatedObject.DIRECTION_NONE);
            logicContext.setPlayerControl(false);
        }
    }

    public void startCycling(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_CYCLING);
    }

    public void stopCycling(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_CYCLING);
    }

    public void onWater(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_ON_WATER);
    }

    public void onLand(short entry) {
        animatedObjects[entry].addFlags(AnimatedObject.FLAG_ON_LAND);
    }

    public void onAnything(short entry) {
        animatedObjects[entry].removeFlags(AnimatedObject.FLAG_ON_LAND | AnimatedObject.FLAG_ON_WATER);
    }

    public short getCycleTime(short entry) {
        return animatedObjects[entry].getCycleTime();
    }

    public void setCycleTime(short entry, short cycleTime) {
        AnimatedObject v = animatedObjects[entry];

        v.setCycleTime(cycleTime);
        v.setCycleTimeCount(cycleTime);
    }

    public short getStepSize(short entry) {
        return animatedObjects[entry].getStepSize();
    }

    public void setStepSize(short entry, short stepSize) {
        animatedObjects[entry].setStepSize(stepSize);
    }

    public short getStepTime(short entry) {
        return animatedObjects[entry].getStepTime();
    }

    public void setStepTime(short entry, short stepTime) {
        AnimatedObject v = animatedObjects[entry];

        v.setStepTime(stepTime);
        v.setStepTimeCount(stepTime);
    }

    public AnimatedObject getEntry(short entry) {
        return animatedObjects[entry];
    }

    public void draw(short entry) {
        AnimatedObject v;

        try {
            v = animatedObjects[entry];
        } catch (IndexOutOfBoundsException oobex) {
            logicContext.setError((short) 0x13);
            return;
        }

        if (v.getCellData() == null) {
            logicContext.setError((short) 0x14);
        }

        if (!v.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN)) {
            v.addFlags(AnimatedObject.FLAG_UPDATE);
            fixPosition(v);
            v.saveCell();
            v.savePosition();
            restoreBackgrounds(updateList);
            v.addFlags(AnimatedObject.FLAG_DRAWN);
            blitAll(buildUpdateBlitList());
            // commitView(v);
            v.removeFlags(AnimatedObject.FLAG_DONT_UPDATE);
        }
    }

    public void erase(short entry) {
        AnimatedObject v;

        try {
            v = animatedObjects[entry];
        } catch (IndexOutOfBoundsException oobex) {
            logicContext.setError((short) 0xc);
            return;
        }

        if (v.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN)) {
            restoreBackgrounds(updateList);
            boolean b = !v.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE);

            if (b) {
                restoreBackgrounds(updateNotList);
            }

            v.removeFlags(AnimatedObject.FLAG_DRAWN);

            if (b) {
                blitAll(buildUpdateNotBlitList());
            }

            blitAll(buildUpdateBlitList());
        }
    }

    public Point getPosition(short entry) {
        AnimatedObject v = animatedObjects[entry];
        return new Point(v.getX(), v.getY());
    }

    public void setPosition(short entry, short x, short y) {
        AnimatedObject v = animatedObjects[entry];

        v.setX(x);
        v.setY(y);
        v.savePosition();
    }

    /**
     * @param entry
     * @param x     Delta for the X position (signed, where negative is to the left)
     * @param y     Delta for the Y position (signed, where negative is to the top)
     */
    public void reposition(short entry, byte x, byte y) {
        AnimatedObject v = animatedObjects[entry];

        v.addFlags(AnimatedObject.FLAG_UPDATE_POS);

        short t = v.getX();

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

    protected boolean checkPosition(AnimatedObject v) {
        if ((v.getX() >= 0) &&
                ((v.getX() + v.getWidth()) <= WIDTH) &&
                ((v.getY() - v.getHeight()) >= -1) &&
                (v.getY() < HEIGHT)) {
            if (!v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_HORIZON)) {
                return v.getY() > logicContext.getHorizon();
            }

            return true;
        }

        return false;
    }

    protected boolean checkClutter(AnimatedObject v) {
        int i;
        AnimatedObject w;

        if (v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_OBJECTS)) {
            return false;
        }

        for (i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            w = animatedObjects[i];

            if (!w.isAllFlagsSet(AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_DRAWN)) {
                continue;
            }

            if (w.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_OBJECTS)) {
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

    protected boolean checkPriority(AnimatedObject v) {
        if (!v.isSomeFlagsSet(AnimatedObject.FLAG_FIX_PRIORITY)) {
            v.setPriority(priorityTable[v.getY()]);
        }

        boolean canBeHere = true;
        boolean signal = false;
        boolean water = false;

        // Priority 15 skips the whole base line testing. None of the control lines
        // have any affect.
        if (v.getPriority() != 15) {
            int startPixelPos = (v.getY() * WIDTH) + v.getX();
            int endPixelPos = startPixelPos + v.getWidth();

            // Start by assuming we're on water. Will be set false if it turns out we're not.
            water = true;

            for (int pixelPos = startPixelPos; pixelPos < endPixelPos; pixelPos++) {
                int priority = priorityPixels[pixelPos];

                // at least one pixel isn't in the water... so set water to false
                if (priority != 3) {
                    water = false;
                }

                // The black control line is an unconditional obstacle;
                if (priority == 0) {
                    canBeHere = false;
                    break;
                }

                // The blue is a conditional obstacle
                if (priority == 1) {
                    /* Conditional blue */
                    if (v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_BLOCKS)) {
                        continue;
                    }

                    canBeHere = false;
                    break;
                }

                // The green is an alarm line
                if (priority == 2) {
                    /* Signal */
                    signal = true;
                }
            }

            if (canBeHere) {
                if (!water && v.isSomeFlagsSet(AnimatedObject.FLAG_ON_WATER)) {
                    canBeHere = false;
                }

                if (water && v.isSomeFlagsSet(AnimatedObject.FLAG_ON_LAND)) {
                    canBeHere = false;
                }
            }
        }

        if (v == animatedObjects[0]) {
            logicContext.setFlag(LogicContext.FLAG_EGO_TOUCHED_ALERT, signal);
            logicContext.setFlag(LogicContext.FLAG_EGO_WATER, water);
        }

        return canBeHere;
    }

    protected void fixPosition(AnimatedObject v) {
        if ((v.getY() <= logicContext.getHorizon()) && !v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_HORIZON)) {
            v.setY((short) (logicContext.getHorizon() + 1));
        }

        if (checkPosition(v) && !checkClutter(v) && checkPriority(v)) {
            return;
        }

        int dir = 0;
        int count = 1;
        int tries = 1;

        while (!checkPosition(v) || checkClutter(v) || !checkPriority(v)) {
            //System.out.println("tries = " + tries + " = v#: " + v.viewNumber + " obj#: " + v.toString());
            switch (dir) {
                case 0:
                    v.setX((short) (v.getX() - 1));

                    count -= 1;
                    if (count == 0) {
                        dir = 1;
                        count = tries;
                    }
                    break;

                case 1:
                    v.setY((short) (v.getY() + 1));

                    count -= 1;
                    if (count == 0) {
                        dir = 2;
                        count = ++tries;
                    }

                    break;

                case 2:
                    v.setX((short) (v.getX() + 1));

                    count -= 1;
                    if (count == 0) {
                        dir = 3;
                        count = tries;
                    }
                    break;

                case 3:
                    v.setY((short) (v.getY() - 1));

                    count -= 1;
                    if (count == 0) {
                        dir = 0;
                        count = ++tries;
                    }
                    break;
            }
        }
    }

    private List<ViewSprite> buildList(List<ViewSprite> list, boolean updating) {
        list.clear();

        for (int i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            AnimatedObject aniObj = animatedObjects[i];
            if (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN) && (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE) == updating)) {
                list.add(new ViewSprite(aniObj));
            }
        }

        Collections.sort(list);

        return list;
    }

    protected List<ViewSprite> buildUpdateBlitList() {
        return buildList(updateList, true);
    }

    protected List<ViewSprite> buildUpdateNotBlitList() {
        return buildList(updateNotList, false);
    }

    protected void restoreBackgrounds(List<ViewSprite> list) {
        final List<ViewSprite> result = new ArrayList<>(list);
        Collections.reverse(result);
        for (ViewSprite viewSprite : result) {
            viewSprite.restore(screenUpdate, picturePixels, priorityPixels);
        }
    }

    protected void restoreBackgrounds() {
        restoreBackgrounds(updateList);
        restoreBackgrounds(updateNotList);
    }

    protected void blitAll(List<ViewSprite> list) {
        for (ViewSprite viewSprite : list) {
            viewSprite.save(picturePixels, priorityPixels);
            viewSprite.blit(screenUpdate, picturePixels, priorityPixels);
        }
    }

    protected void blitBoth() {
        blitAll(buildUpdateNotBlitList());
        blitAll(buildUpdateBlitList());
    }

    protected void checkMove(List<ViewSprite> list) {
        for (ViewSprite viewSprite : list) {
            AnimatedObject aniObj = viewSprite.getViewEntry();
            aniObj.checkMove();
        }
    }

    protected void checkMoveBoth() {
        checkMove(updateNotList);
        checkMove(updateList);
    }

    public void checkAllMotion() {
        for (int i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            AnimatedObject v = animatedObjects[i];

            if (v.isAllFlagsSet(AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_UPDATE | AnimatedObject.FLAG_DRAWN) &&
                    (v.getStepTimeCount() == 1)) {
                checkMotion(v);
            }
        }
    }

    private void checkMotion(AnimatedObject v) {
        switch (v.getMotionType()) {
            case AnimatedObject.MOTION_WANDER:
                checkMotionWander(v);
                break;
            case AnimatedObject.MOTION_FOLLOWEGO:
                checkMotionFollowEgo(v);
                break;
            case AnimatedObject.MOTION_MOVEOBJECT:
                checkMotionMoveObject(v);
                break;
        }

        if ((blockSet && (!v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_BLOCKS))) &&
                (v.getDirection() != 0)) {
            changePos(v);
        }
    }

    protected void checkMotionWander(AnimatedObject v) {
        short targetX = v.getTargetX();

        if ((targetX == 0) || v.isSomeFlagsSet(AnimatedObject.FLAG_DIDNT_MOVE)) {
            int direction = getRandom() % 9;

            v.setDirection((short) direction);

            if (v == animatedObjects[0]) {
                logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) direction);
            }

            if (v.getTargetX() < 6) {
                v.setTargetX((short) (getRandom() % 0x33));
            }
        }
    }

    public int getRandom() {
        return Math.abs(randomSeed.nextInt());
    }

    protected void checkMotionFollowEgo(AnimatedObject obj) {
        AnimatedObject ego = animatedObjects[0];
        short egoX;
        short objX;
        short direction, n;

        egoX = (short) ((ego.getWidth() / 2) + ego.getX());
        objX = (short) ((obj.getWidth() / 2) + obj.getX());

        direction = getDirection(egoX, ego.getY(), objX, obj.getY(), obj.getTargetX());

        if (direction == AnimatedObject.DIRECTION_NONE) {
            obj.setDirection(AnimatedObject.DIRECTION_NONE);
            obj.setMotionType(AnimatedObject.MOTION_NORMAL);
            logicContext.setFlag(obj.getTargetY(), true);
            return;
        }

        if (obj.getOldStepSize() == (short) 0xff) {
            obj.setOldStepSize((short) 0);
        } else if (obj.isSomeFlagsSet(AnimatedObject.FLAG_DIDNT_MOVE)) {
            obj.setDirection((short) ((getRandom() % 8) + 1));

            n = (short) (((absolute(obj.getY() - ego.getY()) + absolute(obj.getX() - ego.getX())) / 2) + 1);

            if (n <= obj.getStepSize()) {
                obj.setOldStepSize(obj.getStepSize());
                return;
            } else {
                do {
                    obj.setOldStepSize((short) (getRandom() % n));
                } while (obj.getOldStepSize() < obj.getStepSize());

                return;
            }
        }

        if (obj.getOldStepSize() != (short) 0) {
            obj.setOldStepSize((short) (obj.getOldStepSize() - obj.getStepSize()));

            if (obj.getOldStepSize() < (short) 0) {
                obj.setOldStepSize((short) 0);
            }
        } else {
            obj.setDirection(direction);
        }
    }

    private void checkMotionMoveObject(AnimatedObject v) {
        v.setDirection(getDirection(v.getX(), v.getY(), v.getTargetX(), v.getTargetY(), v.getStepSize()));

        if (v == animatedObjects[0]) {
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, v.getDirection());
        }

        if (v.getDirection() == AnimatedObject.DIRECTION_NONE) {
            inDestination(v);
        }
    }

    private void inDestination(AnimatedObject v) {
        v.setStepSize(v.getOldStepSize());
        logicContext.setFlag(v.getEndFlag(), true);
        v.setMotionType(AnimatedObject.MOTION_NORMAL);

        if (v == animatedObjects[0]) {
            logicContext.setPlayerControl(true);
            logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) 0);
        }
    }

    private void changePos(AnimatedObject v) {
        int x, y, s;
        boolean b;

        s = v.getStepSize();
        x = v.getX();
        y = v.getY();
        b = checkBlock(x, y);

        switch (v.getDirection()) {
            case AnimatedObject.DIRECTION_NE:
                x += s;
            case AnimatedObject.DIRECTION_N:
                y -= s;
                break;

            case AnimatedObject.DIRECTION_E:
                x += s;
                break;

            case AnimatedObject.DIRECTION_SE:
                x += s;
                y += s;
                break;

            case AnimatedObject.DIRECTION_SW:
                x -= s;
            case AnimatedObject.DIRECITON_S:
                y += s;
                break;

            case AnimatedObject.DIRECTION_W:
                x -= s;
                break;

            case AnimatedObject.DIRECTION_NW:
                x -= s;
                y -= s;
                break;
        }

        if (checkBlock(x, y) == b) {
            v.removeFlags(AnimatedObject.FLAG_MOTION);
        } else {
            v.addFlags(AnimatedObject.FLAG_MOTION);
            v.setDirection((short) 0);

            if (v == animatedObjects[0]) {
                logicContext.setVar(LogicContext.VAR_EGO_DIRECTION, (short) 0);
            }
        }
    }

    public short lastCell(short entry) {
        return (short) (animatedObjects[entry].getLoopData().getCellCount() - 1);
    }

    public short lastLoop(short entry) {
        return animatedObjects[entry].getViewData().getLoopCount();
    }

    protected boolean checkBlock(int x, int y) {
        return ((x >= blockUpperLeftX) &&
                (x <= blockLowerRightX) &&
                (y >= blockUpperLeftY) &&
                (y <= blockLowerRightY));
    }

    protected short getDirection(short x, short y, short destX, short destY, short stepSize) {
        int s = checkStep((short) (destX - x), stepSize);
        int t = checkStep((short) (destY - y), stepSize);

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
        int count = 0;

        for (int i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            AnimatedObject v = animatedObjects[i];

            if (v.isAllFlagsSet(AnimatedObject.FLAG_DRAWN | AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_UPDATE)) {
                count++;
                short direction = 4;

                if (!v.isSomeFlagsSet(AnimatedObject.FLAG_FIX_LOOP)) {
                    short loopCount = v.getLoopCount();

                    if ((loopCount == 2) || (loopCount == 3)) {
                        direction = directionTable2[v.getDirection()];
                    } else if (loopCount == 4) {
                        direction = directionTable4[v.getDirection()];
                    }
                }

                if (v.getStepTimeCount() == 1) {
                    if (direction != 4) {
                        if (direction != v.getLoop()) {
                            v.setLoop(logicContext, direction);
                        }
                    }
                }

                if (v.isSomeFlagsSet(AnimatedObject.FLAG_CYCLING)) {
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

        if (count > 0) {
            restoreBackgrounds(updateList);
            updatePosition();
            blitAll(buildUpdateBlitList());
            checkMove(updateList);

            animatedObjects[0].removeFlags(AnimatedObject.FLAG_ON_LAND | AnimatedObject.FLAG_ON_WATER);
        }
    }

    private void updatePosition() {
        AnimatedObject v;
        int i, x, y, oldX, oldY, dir, step, border;
        short n;

        logicContext.setVar(LogicContext.VAR_BORDER_CODE, (short) 0);
        logicContext.setVar(LogicContext.VAR_EGO_EDGE, (short) 0);
        logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, (short) 0);

        for (i = 0; i < MAX_ANIMATED_OBJECTS; i++) {
            v = animatedObjects[i];

            if (v.isAllFlagsSet(AnimatedObject.FLAG_DRAWN | AnimatedObject.FLAG_ANIMATE | AnimatedObject.FLAG_UPDATE)) {
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

                if (!v.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE_POS)) {
                    dir = v.getDirection();
                    step = v.getStepSize();

                    x += (step * directionTableX[dir]);
                    y += (step * directionTableY[dir]);
                }

                border = 0;

                if (x < 0) {
                    x = 0;
                    border = LogicVariables.LEFT;
                } else if ((x + v.getWidth()) > WIDTH) {
                    x = 160 - v.getWidth();
                    border = LogicVariables.RIGHT;
                }

                if ((y - v.getHeight() + 1) < 0) {
                    y = v.getHeight() - 1;
                    border = LogicVariables.TOP;
                } else if (y > (HEIGHT - 1)) {
                    y = HEIGHT - 1;
                    border = LogicVariables.BOTTOM;
                } else if (!v.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_HORIZON) && y <= logicContext.getHorizon()) {
                    y = logicContext.getHorizon();
                    border = LogicVariables.TOP;
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
                        logicContext.setVar(LogicContext.VAR_EGO_EDGE, (short) border);
                    } else {
                        logicContext.setVar(LogicContext.VAR_BORDER_CODE, (short) i);
                        logicContext.setVar(LogicContext.VAR_BORDER_TOUCHING, (short) border);
                    }

                    if (v.getMotionType() == AnimatedObject.MOTION_MOVEOBJECT) {
                        inDestination(v);
                    }
                }
            }

            v.removeFlags(AnimatedObject.FLAG_UPDATE_POS);
        }
    }

    public void doUpdate() {
        if (!screenUpdate.isEmpty()) {
            Rectangle r = screenUpdate.getBounds();

            viewScreen.putBlock(picturePixels, r.x, r.y, r.width, r.height);
        }

        screenUpdate.reset();
    }

    public void setBlockSet(boolean blockSet) {
        this.blockSet = blockSet;
    }

    public void setBlockUpperLeftX(short blockUpperLeftX) {
        this.blockUpperLeftX = blockUpperLeftX;
    }

    public void setBlockLowerRightX(short blockLowerRightX) {
        this.blockLowerRightX = blockLowerRightX;
    }

    public void setBlockUpperLeftY(short blockUpperLeftY) {
        this.blockUpperLeftY = blockUpperLeftY;
    }

    public void setBlockLowerRightY(short blockLowerRightY) {
        this.blockLowerRightY = blockLowerRightY;
    }

    public short getBlockUpperLeftX() {
        return blockUpperLeftX;
    }

    public short getBlockLowerRightX() {
        return blockLowerRightX;
    }

    public short getBlockUpperLeftY() {
        return blockUpperLeftY;
    }

    public short getBlockLowerRightY() {
        return blockLowerRightY;
    }

    public PictureContext getPictureContext() {
        return pictureContext;
    }

    public boolean isBlockSet() {
        return blockSet;
    }

    public AnimatedObject[] getAnimatedObjects() {
        return animatedObjects;
    }

    public void showPriorityScreen() {
        viewScreen.save();

        int[] backup = new int[WIDTH * HEIGHT];

        System.arraycopy(picturePixels, 0, backup, 0, picturePixels.length);

        boolean looping = true;

        EgaComponent egaComponent = viewScreen.getComponent();

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int priColorIndex = this.priorityPixels[i];
            this.picturePixels[i] = translatePixel((byte) priColorIndex);
        }
        screenUpdate.reset();
        screenUpdate.add(new Area(new Rectangle(0, 0, WIDTH, HEIGHT)));
        doUpdate();

        do {
            if ((egaComponent.popCharEvent(-1)) == null) {
                break;
            }
            looping = false;
        } while (looping);

        System.arraycopy(backup, 0, picturePixels, 0, backup.length);

        viewScreen.restore(true);
    }

    public void showInventoryObject(short viewNumber) throws Exception {
        AnimatedObject animatedObject = new AnimatedObject(-1);
        animatedObject.setView(this.logicContext, viewNumber);
        animatedObject.setX((short) ((ViewTable.WIDTH - animatedObject.getWidth()) / 2));
        animatedObject.setxCopy((short) ((ViewTable.WIDTH  - animatedObject.getWidth()) / 2));
        animatedObject.setY((short) (ViewTable.HEIGHT - 1));
        animatedObject.setyCopy((short) (ViewTable.HEIGHT - 1));
        animatedObject.setPriority((short) 15);
        animatedObject.addFlags(AnimatedObject.FLAG_FIX_PRIORITY);
        animatedObject.setPreviousCellData(animatedObject.getCellData());

        ViewSprite viewSprite = new ViewSprite(animatedObject);
        viewSprite.save(picturePixels, priorityPixels);
        // viewSprite.blit(screenUpdate, picturePixels, priorityPixels);

        View viewData = logicContext.getCache().getView(viewNumber);
        (new MessageBox(logicContext.processMessage(viewData.getDescription()))).show(logicContext, logicContext.getViewScreen());

        viewSprite.restore(screenUpdate, picturePixels, priorityPixels);
    }
}