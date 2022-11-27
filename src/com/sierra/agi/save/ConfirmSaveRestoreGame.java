package com.sierra.agi.save;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.ViewScreen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ConfirmSaveRestoreGame {
    private static final int MAX_COLUMN = 25;
    private String[] lines;

    public ConfirmSaveRestoreGame(boolean save, String description, String path) {
        init(save, description, path);
    }

    private void init(boolean save, String description, String path) {
        List lines = new ArrayList<String>();


        lines.add("About to " + (save ? "save" : "restore") + " the game");
        lines.add("described as:");

        lines.add("");
        lines.add("");

        lines.add(description);

        lines.add("");
        lines.add("");

        lines.add("from file:");

        lines.add(path);
        lines.add("");
        lines.add("");
        lines.add("Press ENTER to continue.");
        lines.add("Press ESC to cancel.");

        lines.toArray(this.lines = new String[lines.size()]);
    }

    public boolean show(LogicContext logicContext, ViewScreen viewScreen) {
        boolean confirm = false;

        KeyEvent ev;

        if (logicContext != null) {
            logicContext.stopClock();
        }

        viewScreen.save();

        draw(viewScreen);

        EgaComponent ega = viewScreen.getComponent();
        boolean looping = true;

        ega.clearEvents();

        do {
            if ((ev = ega.popCharEvent(-1)) == null) {
                break;
            }

            switch (ev.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    confirm = true;
                    looping = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    looping = false;
                    break;
            }
        } while (looping);

        viewScreen.restore(true);

        if (logicContext != null) {
            logicContext.startClock();
        }

        return confirm;
    }

    public void draw(ViewScreen viewScreen) {
        int x = -1;
        int y = -1;
        int width = ViewScreen.CHAR_WIDTH * (MAX_COLUMN + 2);
        int height = ViewScreen.CHAR_HEIGHT * (lines.length + 2);
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

        //int pointerYStart = y + (ViewScreen.CHAR_HEIGHT * 5);
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
}
