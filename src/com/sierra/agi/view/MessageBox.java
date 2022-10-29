/*
 *  Box.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2002 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import static com.sierra.agi.logic.LogicVariables.FLAG_OUTPUT_MODE;

public class MessageBox extends Box {
    private static final int MAX_COLUMN = 30;

    protected String[] lines;
    protected int x = -1;
    protected int y = -1;
    protected int column;

    public MessageBox(String content) {
        init(content, 30);
        trim();
    }

    public MessageBox(String content, int x, int y, int column) {
        init(content, column);
        trim();
        this.x = x;
        this.y = y;
    }

    public MessageBox(String content, int column) {
        init(content, column);
    }

    public void draw(ViewScreen viewScreen) {
        int x = this.x;
        int y = this.y;
        int width = getWidth();
        int height = getHeight();
        int textColor = viewScreen.translatePixel(Color.black);
        int backColor = viewScreen.translatePixel(Color.white);
        int borderColor = viewScreen.translatePixel(Color.red.darker());
        int[] screen = viewScreen.getScreenData();
        int[] font = viewScreen.getFont();

        if (x < 0) {
            x = (ViewScreen.WIDTH - width) / 2;
        }

        if (y < 0) {
            y = (ViewScreen.HEIGHT - height) / 2;
        }

        int oy = y;
        int end = x + width - ViewScreen.CHAR_WIDTH;

        viewScreen.drawTopLine(borderColor, backColor, x, y, width);
        y += ViewScreen.CHAR_HEIGHT;

        for (int line = 0; line < lines.length; line++) {
            String text = lines[line];
            int textLength = text.length();
            int textEnd = x + ((textLength + 1) * ViewScreen.CHAR_WIDTH);

            viewScreen.drawLeftLine(borderColor, backColor, x, y);

            EgaUtils.putString(screen, font, text, x + ViewScreen.CHAR_WIDTH, y, ViewScreen.WIDTH, textColor, backColor, true);

            if (end != textEnd) {
                viewScreen.drawBlanks(backColor, textEnd, y, end - textEnd);
            }

            viewScreen.drawRightLine(borderColor, backColor, end, y);
            y += ViewScreen.CHAR_HEIGHT;
        }

        viewScreen.drawBottomLine(borderColor, backColor, x, y, width);
        viewScreen.putBlock(x, oy, width, height);
    }

    public KeyEvent show(LogicContext logicContext, ViewScreen viewScreen) {
        // clear out existing open modals
        viewScreen.restore(true);

        KeyEvent ev = null;
        int timeout = this.timeout;

        if (logicContext != null) {
            logicContext.stopClock();

            if (timeout == -1) {
                timeout = logicContext.getVar(LogicContext.VAR_WINDOW_RESET) * 500;

                if (timeout == 0) {
                    timeout = -1;
                }
            }
        }

        boolean modal = !logicContext.getFlag(FLAG_OUTPUT_MODE);

        if (modal) {
            viewScreen.save();
        }

        draw(viewScreen);

        if (modal) {
            EgaComponent ega = viewScreen.getComponent();
            boolean looping = true;

            ega.clearEvents();

            do {
                if ((ev = ega.popCharEvent(timeout)) == null) {
                    break;
                }

                switch (ev.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_ESCAPE:
                        looping = false;
                        break;
                }
            } while (looping);

            viewScreen.restore(true);
        } else {
            logicContext.setFlag(FLAG_OUTPUT_MODE, false);
        }

        if (logicContext != null) {
            logicContext.startClock();
        }

        return ev;
    }

    protected void init(String content, int maxColumnArg) {
        Vector lines = new Vector();

        int maxColumn = maxColumnArg == 0 ? MAX_COLUMN : maxColumnArg;

        StringTokenizer tokenizer = new StringTokenizer(content, "\r\n", false);

        while (tokenizer.hasMoreTokens()) {
            StringBuffer current = new StringBuffer();
            String token = tokenizer.nextToken();
            StringTokenizer words = new StringTokenizer(token, " ", true);

            while (words.hasMoreTokens()) {
                String word = words.nextToken();

                if ((current.length() + word.length()) > maxColumn) {
                    lines.add(current.toString());
                    current = new StringBuffer();
                }

                if (word.equals(" ") && (current.length() == 0)) {
                    continue;
                }

                current.append(word);
            }

            lines.add(current.toString());
        }

        lines.toArray(this.lines = new String[lines.size()]);
    }

    protected void trim() {
        int index;
        int length;
        String line;

        for (index = 0; index < lines.length; index++) {
            line = lines[index];
            length = line.length() - 1;

            while (length >= 0) {
                if (line.charAt(length) == ' ') {
                    length--;
                } else {
                    break;
                }
            }

            lines[index] = line = line.substring(0, length + 1);

            if (column < line.length()) {
                column = line.length();
            }
        }
    }

    public int getLineCount() {
        return lines.length;
    }

    public int getColumnCount() {
        return column;
    }

    public int getWidth() {
        return ViewScreen.CHAR_WIDTH * (column + 2);
    }

    public int getHeight() {
        return ViewScreen.CHAR_HEIGHT * (lines.length + 2);
    }
}
