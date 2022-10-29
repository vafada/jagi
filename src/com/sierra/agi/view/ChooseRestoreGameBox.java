package com.sierra.agi.view;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ChooseRestoreGameBox extends Box {
    private static final int MAX_COLUMN = 35;
    private static final String MESSAGE = "Use the arrow keys to select the game which you wish to restore. Press ENTER to restore the game, ESC to not restore a game.";

    private String[] lines;
    private int x = -1;
    private int y = -1;

    public ChooseRestoreGameBox() {
        init();
    }

    private void init() {
        List lines = new ArrayList<String>();

        StringBuffer current = new StringBuffer();
        StringTokenizer words = new StringTokenizer(MESSAGE, " ", true);

        while (words.hasMoreTokens()) {
            String word = words.nextToken();

            if ((current.length() + word.length()) > MAX_COLUMN) {
                lines.add(current.toString());
                current = new StringBuffer();
            }

            if (word.equals(" ") && (current.length() == 0)) {
                continue;
            }

            current.append(word);
        }

        lines.add(current.toString());

        lines.toArray(this.lines = new String[lines.size()]);

    }

    public KeyEvent show(LogicContext logicContext, ViewScreen viewScreen,  boolean modal) {
        KeyEvent ev = null;

        if (logicContext != null) {
            logicContext.stopClock();
        }


        viewScreen.save();

        draw(viewScreen);

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
                case KeyEvent.VK_UP:
                    System.out.println("up");
                    break;
                case KeyEvent.VK_DOWN:
                    System.out.println("down");
                    break;
            }
        } while (looping);

        viewScreen.restore(true);

        if (logicContext != null) {
            logicContext.startClock();
        }

        return ev;
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

    public int getLineCount() {
        return lines.length;
    }

    public int getColumnCount() {
        return MAX_COLUMN;
    }

    public int getWidth() {
        return ViewScreen.CHAR_WIDTH * (MAX_COLUMN + 2);
    }

    public int getHeight() {
        return ViewScreen.CHAR_HEIGHT * (lines.length + 2);
    }
}
