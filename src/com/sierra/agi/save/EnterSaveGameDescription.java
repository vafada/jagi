package com.sierra.agi.save;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.ViewScreen;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;

public class EnterSaveGameDescription {
    private static final int MAX_COLUMN = 31;
    private static final int MAX_DESCRIPTION_LENGTH = 30;

    private String description = null;

    public EnterSaveGameDescription(String description) {
        if (description != null && !description.isBlank() && !description.isEmpty()) {
            this.description = description;
        }
    }

    public String show(LogicContext logicContext, ViewScreen viewScreen) {
        if (logicContext != null) {
            logicContext.stopClock();
        }
        String description = null;

        viewScreen.save();

        Point inputPoint = draw(viewScreen);

        EgaComponent ega = viewScreen.getComponent();
        ega.clearEvents();

        // Get a line of text from the user.
        StringBuilder line = new StringBuilder();
        KeyEvent ev;
        // Process entered keys until either ENTER or ESC is pressed.
        while (true) {
            // Show the currently entered text.
            displayLine(viewScreen, inputPoint.x, inputPoint.y, (line + "_"));

            if ((ev = ega.popCharEvent(-1)) == null) {
                break;
            }

            int key = ev.getKeyCode();
            if (key >= 32 && key <= 126) {
                // If we haven't reached the max length, add the char to the line of text.
                if (line.length() < MAX_DESCRIPTION_LENGTH) {
                    line.append((char) key);
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                description = null;
                break;
            } else if (key == KeyEvent.VK_ENTER) {
                description = line.toString();
                break;
            } else if (key == KeyEvent.VK_BACK_SPACE) {
                // Removes one from the end of the currently entered input.
                if (line.length() > 0) {
                    line.deleteCharAt(line.length() - 1);
                }

                // Render Line with a space overwriting the previous position of the cursor.
                displayLine(viewScreen, inputPoint.x, inputPoint.y, (line + "_ "));
            }
        }

        viewScreen.restore(true);

        if (logicContext != null) {
            logicContext.startClock();
        }

        return description;
    }

    public Point draw(ViewScreen viewScreen) {
        int x = -1;
        int y = -1;
        int width = ViewScreen.CHAR_WIDTH * (MAX_COLUMN + 2);
        int height = ViewScreen.CHAR_HEIGHT * 8;
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

        String[] lines = new String[]{
                "How would you like to describe",
                "this saved game?",
                "",
                " ".repeat(MAX_DESCRIPTION_LENGTH + 1),
        };

        int inputX = 0;
        int inputY = 0;

        for (int line = 0; line < lines.length; line++) {
            String text = lines[line];
            int textLength = text.length();
            int textEnd = x + ((textLength + 1) * ViewScreen.CHAR_WIDTH);

            viewScreen.drawLeftLine(borderColor, backColor, x, y);

            if (line == 3) {
                inputX = x + ViewScreen.CHAR_WIDTH;
                inputY = y;
            } else {
                EgaUtils.putString(screen, font, text, x + ViewScreen.CHAR_WIDTH, y, ViewScreen.WIDTH, textColor, backColor, true);
            }
            if (end != textEnd) {
                viewScreen.drawBlanks(backColor, textEnd, y, end - textEnd);
            }

            viewScreen.drawRightLine(borderColor, backColor, end, y);
            y += ViewScreen.CHAR_HEIGHT;
        }

        Point inputPoint = new Point(inputX, inputY);

        viewScreen.drawBottomLine(borderColor, backColor, x, y, width);
        viewScreen.putBlock(x, oy, width, height);

        return inputPoint;
    }

    private void displayLine(ViewScreen viewScreen, int col, int line, String message) {
        int c = message.length();
        int[] screen = viewScreen.getScreenData();
        int[] font = viewScreen.getFont();

        for (int i = 0; i < c; i++) {
            EgaUtils.putCharacter(screen, font, message.charAt(i), col, line, ViewScreen.WIDTH, viewScreen.translatePixel(Color.white), viewScreen.translatePixel(Color.black), true);
            col += ViewScreen.CHAR_WIDTH;
        }

        viewScreen.putBlock(0, line, ViewScreen.WIDTH, ViewScreen.CHAR_HEIGHT);
    }
}
