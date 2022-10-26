/**
 * LogicComponent.java
 * Adventure Game Interface Debug Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug.logic;

import com.sierra.agi.logic.debug.LogicDebug;
import com.sierra.agi.logic.debug.LogicEvent;
import com.sierra.agi.logic.debug.LogicListener;
import com.sierra.agi.res.ResourceCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;
import java.util.Vector;

public class LogicComponent extends JComponent implements MouseListener, LogicListener, LogicEvaluatorListener /*, Scrollable*/ {
    protected static Image breakpoint;
    protected static Image breakpointd;
    protected static Image pointer;
    protected Dimension preferred = new Dimension(50, 50);
    protected LogicLine[] lines = null;
    protected BitSet breakpoints;
    protected BitSet breakpointsActivated;
    protected int instructionNumber = -1;
    protected int instructionLine = -1;
    protected LogicDebug logic;
    protected LogicEvaluator evaluator;

    public LogicComponent(ResourceCache cache) {
        init(cache);
        setLogic(null);
        addMouseListener(this);
    }

    public LogicComponent(ResourceCache cache, LogicDebug logic) {
        init(cache);
        setLogic(logic);
        addMouseListener(this);
    }

    protected void init(ResourceCache cache) {
        Class thisClass = getClass();

        setBackground(Color.white);
        setFont(new Font("Monospaced", Font.PLAIN, 12));

        setLogicEvaluator(new LogicEvaluator(cache));

        synchronized (thisClass) {
            if (breakpoint == null) {
                Toolkit toolkit = getToolkit();

                breakpoint = toolkit.createImage(thisClass.getResource("Breakpoint.gif"));
                breakpointd = toolkit.createImage(thisClass.getResource("Breakpointd.gif"));
                pointer = toolkit.createImage(thisClass.getResource("Pointer.gif"));

                prepareImage(breakpoint, this);
                prepareImage(breakpointd, this);
                prepareImage(pointer, this);
            }
        }
    }

    public void setInstructionNumber(int instructionNumber) {
        int index;

        this.instructionNumber = instructionNumber;

        if (instructionNumber >= 0) {
            instructionLine = -1;

            for (index = 0; index < lines.length; index++) {
                if (lines[index].instructionNumber == instructionNumber) {
                    instructionLine = index;
                    break;
                }
            }
        } else {
            instructionLine = -1;
        }

        if (instructionLine != -1) {
            FontMetrics metrics = getFontMetrics(getFont());
            Rectangle rect = new Rectangle(
                    0, (instructionLine * metrics.getHeight() + metrics.getDescent()),
                    50, metrics.getHeight() + metrics.getDescent());

            scrollRectToVisible(rect);
        }

        repaint();
    }

    public LogicDebug getLogic() {
        return logic;
    }

    public void setLogic(LogicDebug logic) {
        if (this.logic == logic) {
            return;
        }

        if (this.logic != null) {
            this.logic.removeLogicListener(this);
            this.logic = null;
        }

        if (logic == null) {
            preferred.width = 50;
            preferred.height = 50;
            this.lines = null;
            this.breakpoints = null;
            this.breakpointsActivated = null;
            this.logic = null;
        } else {
            Vector linesVector = evaluator.decompile(logic);

            breakpoints = logic.getBreakpoints();
            breakpointsActivated = logic.getBreakpointsActivated();
            lines = new LogicLine[linesVector.size()];
            this.logic = logic;

            linesVector.toArray(lines);

            updateLines();
            logic.addLogicListener(this);
        }

        revalidate();
    }

    public LogicEvaluator getLogicEvaluator() {
        return evaluator;
    }

    public void setLogicEvaluator(LogicEvaluator evaluator) {
        if (this.evaluator != null) {
            this.evaluator.removeLogicEvaluatorListener(this);
        }

        this.evaluator = evaluator;

        if (evaluator != null) {
            evaluator.addLogicEvaluatorListener(this);
        }
    }

    public String getLineText(int line) {
        if (lines == null) {
            return "";
        }

        return lines[line].text;
    }

    public int getLineCount() {
        if (lines == null) {
            return 0;
        }

        return lines.length;
    }

    protected void updateLines() {
        int i;
        int count, maxWidth, lineWidth;
        FontMetrics metrics;

        metrics = getFontMetrics(getFont());
        count = lines.length;
        maxWidth = 0;

        for (i = 0; i < count; i++) {
            lineWidth = metrics.stringWidth(lines[i].text);

            if (maxWidth <= lineWidth) {
                maxWidth = lineWidth;
            }
        }

        maxWidth += breakpoint.getWidth(this) + 15;
        preferred.width = maxWidth;
        preferred.height = (metrics.getHeight() * count) + metrics.getDescent();
    }

    public void paint(Graphics g) {
        if (logic == null) {
            return;
        }

        Dimension d = getSize();
        FontMetrics metrics = getFontMetrics(getFont());
        int i, c, x, y, cw;
        int ly1, ly2;
        int iw, im;
        float h, ih;
        LogicLine line;

        g.setFont(getFont());

        cw = d.width;
        iw = breakpoint.getWidth(this);
        ih = (float) breakpoint.getHeight(this);

        c = lines.length;
        h = (float) metrics.getHeight();
        x = iw + 15;
        y = 0;

        g.setColor(Color.white);
        g.fillRect(0, 0, cw, d.height);
        g.setColor(Color.black);

        for (i = 0; i < c; i++) {
            line = lines[i];
            ly1 = y + metrics.getDescent();
            ly2 = ly1 + (int) h;
            im = ly2 - (int) ((h / 2) + (ih / 2));

            if (line.instructionNumber >= 0) {
                if (breakpoints.get(line.instructionNumber)) {
                    if (breakpointsActivated.get(line.instructionNumber)) {
                        g.drawImage(breakpoint, 5, im, this);
                    } else {
                        g.drawImage(breakpointd, 5, im, this);
                    }
                }
            }

            if (i == instructionLine) {
                g.drawImage(pointer, 5 + iw - pointer.getWidth(this), im, this);
                g.setColor(new Color(192, 0, 0));

                g.drawLine(x - 4, ly1, cw, ly1);
                g.drawLine(x - 4, ly2, cw, ly2);
                g.drawLine(x - 5, ly1, x - 5, ly2);
                g.setColor(new Color(255, 192, 192));
                g.fillRect(x - 4, ly1 + 1, cw, ly2 - ly1 - 1);
                g.setColor(Color.black);
            }

            y += h;
            g.drawString(line.text, x, y);
        }
    }

    public Dimension getMinimumSize() {
        return new Dimension(32, 32);
    }

    public Dimension getPreferredSize() {
        return preferred;
    }

    protected void mouseClicked(MouseEvent ev, LogicLine line) {
        if (line.instructionNumber < 0) {
            return;
        }

        if ((ev.getModifiers() & InputEvent.BUTTON2_MASK) == 0) {
            if (breakpoints.get(line.instructionNumber)) {
                logic.removeBreakpoint(line.instructionNumber);
            } else {
                logic.addBreakpoint(line.instructionNumber);
            }
        } else {
            if (breakpointsActivated.get(line.instructionNumber)) {
                logic.disableBreakpoint(line.instructionNumber);
            } else {
                if (breakpoints.get(line.instructionNumber)) {
                    logic.enableBreakpoint(line.instructionNumber);
                } else {
                    logic.disableBreakpoint(line.instructionNumber, true);
                }
            }
        }
    }

    public void mouseClicked(MouseEvent ev) {
        if (logic == null) {
            return;
        }

        if (ev.getX() <= (10 + breakpoint.getWidth(this))) {
            FontMetrics metrics = getFontMetrics(getFont());
            int lineNumber = (ev.getY() - metrics.getDescent()) / metrics.getHeight();

            try {
                mouseClicked(ev, lines[lineNumber]);
            } catch (IndexOutOfBoundsException ioobex) {
            }
        }
    }

    public void mouseEntered(MouseEvent ev) {
    }

    public void mouseExited(MouseEvent ev) {
    }

    public void mousePressed(MouseEvent ev) {
    }

    public void mouseReleased(MouseEvent ev) {
    }

    public void logicBreakpointAdded(LogicEvent ev) {
        repaint();
    }

    public void logicBreakpointRemoved(LogicEvent ev) {
        repaint();
    }

    public void logicBreakpointEnabled(LogicEvent ev) {
        repaint();
    }

    public void logicBreakpointDisabled(LogicEvent ev) {
        repaint();
    }

    public void logicMapChanged(LogicEvaluatorEvent ev) {
        updateLines();
        revalidate();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(300, 200);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getFontMetrics(getFont()).getHeight() * 10;
    }

    public boolean getScrollableTracksViewportHeight() {
        return true;
    }

    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getFontMetrics(getFont()).getHeight();
    }
}

