/*
 *  EgaComponent.java
 *  Adventure Game Interface AWT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.awt;

import com.sierra.agi.view.ViewScreen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import java.util.Vector;

public class EgaComponent extends Canvas implements MouseListener, KeyListener {
    protected static final int ZOOM = 2;
    protected static Dimension dimension = new Dimension(640, 400);
    protected static int[] altKeys = {
            30, 48, 46, 32, 18, 33, 34, 35, 23, 36, 37, 38, 50,  // A-M
            49, 24, 25, 16, 19, 31, 20, 22, 47, 17, 45, 21, 44}; // N-Z
    protected static int[] directionKeys = new int[]{
            KeyEvent.VK_UP, 1,
            KeyEvent.VK_PAGE_UP, 2,
            KeyEvent.VK_RIGHT, 3,
            KeyEvent.VK_PAGE_DOWN, 4,
            KeyEvent.VK_DOWN, 5,
            KeyEvent.VK_END, 6,
            KeyEvent.VK_LEFT, 7,
            KeyEvent.VK_HOME, 8,
            KeyEvent.VK_NUMPAD8, 1,
            KeyEvent.VK_NUMPAD9, 2,
            KeyEvent.VK_NUMPAD6, 3,
            KeyEvent.VK_NUMPAD3, 4,
            KeyEvent.VK_NUMPAD2, 5,
            KeyEvent.VK_NUMPAD1, 6,
            KeyEvent.VK_NUMPAD4, 7,
            KeyEvent.VK_NUMPAD7, 8,
            KeyEvent.VK_NUMPAD5, 0};
    protected Image back;
    protected Graphics front;
    protected Vector keys = new Vector();
    protected Vector clicks = new Vector();
    protected Hashtable shortcuts = new Hashtable();

    public EgaComponent() {
        addMouseListener(this);
        addKeyListener(this);
    }

    /**
     * Called when the ViewTable object is initially created.
     */
    public void setImageProducer(ImageProducer producer) {
        Toolkit toolkit = getToolkit();

        back = toolkit.createImage(
                new FilteredImageSource(
                        producer,
                        new QuickerScaleFilter()));

        toolkit.prepareImage(back, ViewScreen.WIDTH * ZOOM, ViewScreen.HEIGHT * ZOOM, this);
    }

    /**
     * Update a particular block of the Central part of the screen.
     */
    public void putBlock(int x, int y, int width, int height) {
        if (front == null) {
            front = getGraphics();

            if (front == null) {
                /* EgaComponent has not been inserted in a Frame object. */
                return;
            }
        }

        /* Apparently, it is faster to clip the zone and simply
           draw the image than to draw partially the image. */

        front.setClip(
                x * ZOOM,
                y * ZOOM,
                width * ZOOM,
                height * ZOOM);

        front.drawImage(back, 0, 0, this);
    }

    public void paint(Graphics g) {
        g.drawImage(back, 0, 0, this);
    }

    public void update(Graphics g) {
    }

    public synchronized void pushKeyboardEvent(KeyEvent ev) {
        keys.add(ev);
    }

    public KeyEvent popCharEvent(int timeout) {
        KeyEvent ev;

        while (true) {
            ev = popKeyboardEvent(timeout);

            if (ev == null) {
                break;
            }

            if (ev.getID() == KeyEvent.KEY_PRESSED) {
                break;
            }
        }

        return ev;
    }

    public KeyEvent popKeyboardEvent(int timeout) {
        if (timeout < 0) {
            timeout = 0x7fffffff;
        }

        while (true) {
            synchronized (this) {
                if (keys.size() != 0) {
                    return popKeyboardEvent();
                }
            }

            if (timeout < 0) {
                break;
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException iex) {
            }

            timeout -= 25;
        }

        return null;
    }

    public synchronized boolean hasKeyEvent() {
        return keys.size() > 0;
    }

    public synchronized KeyEvent popKeyboardEvent() {
        KeyEvent ev = null;

        if (keys.size() > 0) {
            ev = (KeyEvent) keys.get(0);
            keys.removeElementAt(0);
        }

        return ev;
    }

    public synchronized boolean hasMouseEvent() {
        return clicks.size() > 0;
    }

    public synchronized MouseEvent popMouseEvent() {
        MouseEvent ev = null;

        if (clicks.size() > 0) {
            ev = (MouseEvent) clicks.get(0);
            clicks.removeElementAt(0);
        }

        return ev;
    }

    public synchronized void clearEvents() {
        keys.removeAllElements();
        clicks.removeAllElements();
    }

    public synchronized void mouseClicked(MouseEvent ev) {
        clicks.add(ev);
    }

    public void mouseEntered(MouseEvent ev) {
    }

    public void mouseExited(MouseEvent ev) {
    }

    public void mousePressed(MouseEvent ev) {
    }

    public void mouseReleased(MouseEvent ev) {
    }

    public void keyTyped(KeyEvent ev) {
        pushKeyboardEvent(ev);
    }

    public void keyPressed(KeyEvent ev) {
        pushKeyboardEvent(ev);
    }

    public void keyReleased(KeyEvent ev) {
        pushKeyboardEvent(ev);
    }

    public Dimension getPreferredSize() {
        return dimension;
    }

    public Dimension getMinimumSize() {
        return dimension;
    }

    public Dimension getMaximumSize() {
        return dimension;
    }

    public EgaEvent mapKeyEventToAGI(KeyEvent event) {
        int agiCode;
        Object object;

        if (event == null) {
            return null;
        }

        agiCode = mapKeyEventToDirection(event);

        if (agiCode < 0) {
            agiCode = mapKeyEventToIBM(event);
            object = shortcuts.get(Integer.valueOf(agiCode));

            if (object != null) {
                if (object instanceof Number) {
                    return new EgaEvent(EgaEvent.TYPE_SHORTCUT, ((Number) object).shortValue());
                }
            }
        } else {
            return new EgaEvent(EgaEvent.TYPE_DIRECTION, (short) agiCode);
        }

        return new EgaEvent(EgaEvent.TYPE_CHAR, (short) agiCode);
    }

    public int mapKeyEventToIBM(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int modifiers = event.getModifiers();

        if ((keyCode >= KeyEvent.VK_F1) && (keyCode <= KeyEvent.VK_F10)) {
            // F1 - F10
            return (keyCode - KeyEvent.VK_F1 + 0x3B) << 8;
        } else if ((modifiers & KeyEvent.ALT_MASK) != 0) {
            if ((keyCode >= KeyEvent.VK_A) && (keyCode <= KeyEvent.VK_Z)) {
                return altKeys[keyCode - KeyEvent.VK_A] << 8;
            }
        } else if ((modifiers & KeyEvent.CTRL_MASK) != 0) {
            if ((keyCode >= KeyEvent.VK_A) && (keyCode <= KeyEvent.VK_Z)) {
                return keyCode - KeyEvent.VK_A + 1;
            }
        } else {
            switch (keyCode) {
                case KeyEvent.VK_TAB:
                    return 0x09;

                case KeyEvent.VK_ESCAPE:
                    return 0x1B;

                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    return 0x08;

                case KeyEvent.VK_ENTER:
                    return 0x0D;
            }
        }

        return keyCode & 0x7F;
    }

    public int mapKeyEventToDirection(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int[] directionKeys = EgaComponent.directionKeys;
        int index;

        for (index = 0; index < directionKeys.length; index += 2) {
            if (directionKeys[index] == keyCode) {
                return directionKeys[index + 1];
            }
        }

        return -1;
    }
}
