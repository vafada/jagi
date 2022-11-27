/*
 *  ViewScreen.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.inv.InventoryScreen;
import com.sierra.agi.logic.LogicVariables;
import com.sierra.agi.menu.AgiMenuBar;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

public class ViewScreen {
    public static final int WIDTH = 320;
    public static final int HEIGHT = 200;
    public static final int CHAR_WIDTH = 8;
    public static final int CHAR_HEIGHT = 8;

    // bitmap that's actually rendered!
    private int[] screen;

    // backup bitmap of screen when switching between pri pixel/text mode to graphics
    private int[] screenBackup;
    protected MemoryImageSource screenSource;

    private int backgroundColor;
    private byte backgroundColorByte;
    private int foregroundColor;
    private byte foregroundColorByte;
    protected int[] font;

    protected EgaComponent ega;

    protected int lineMinPrint;
    protected int lineUserInput;
    protected int lineStatus;

    protected char promptChar = '>';
    private char cursorChar = '_';
    protected int[] pixel = new int[1];

    public ViewScreen() {
        screen = new int[WIDTH * HEIGHT];
        screenSource = new MemoryImageSource(WIDTH, HEIGHT, EgaUtils.getNativeColorModel(), screen, 0, WIDTH);
        ega = new EgaComponent();

        screenSource.setAnimated(true);
        ega.setImageProducer(screenSource);

        backgroundColor = translatePixel((byte) 0);
        backgroundColorByte = 0;
        foregroundColor = translatePixel((byte) 15);
        foregroundColorByte = 15;
        font = EgaUtils.getEgaFont();
    }

    public void configure(short lineMinPrint, short lineUserInput, short lineStatus) {
        this.lineMinPrint = lineMinPrint;
        this.lineUserInput = lineUserInput;
        this.lineStatus = lineStatus;
    }

    public void setForegroundColor(byte color) {
        foregroundColorByte = color;
        foregroundColor = translatePixel(color);
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setBackgroundColor(byte color) {
        backgroundColorByte = color;
        backgroundColor = translatePixel(color);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setPromptChar(char ch) {
        promptChar = ch;
    }

    public void setCursorChar(char ch) {
        cursorChar = ch;
    }

    public void reset() {
        Arrays.fill(screen, translatePixel((byte) 0));
    }

    public void setInputLine(String inputLine) {
        if (inputLine == null) {
            clearLines(lineUserInput, lineUserInput, (short) 0);
        } else {
            drawBlanks(
                    translatePixel((byte) 0),
                    0,
                    (lineUserInput * ViewScreen.CHAR_HEIGHT),
                    ViewScreen.WIDTH);

            EgaUtils.putCharacter(
                    screen,
                    font,
                    promptChar,
                    0,
                    lineUserInput * CHAR_HEIGHT,
                    WIDTH,
                    translatePixel((byte) 15),
                    translatePixel((byte) 0),
                    true);

            int x = CHAR_WIDTH;

            for (int index = 0; index < inputLine.length(); index++) {
                EgaUtils.putCharacter(
                        screen,
                        font,
                        inputLine.charAt(index),
                        x,
                        lineUserInput * CHAR_HEIGHT,
                        WIDTH,
                        translatePixel((byte) 15),
                        translatePixel((byte) 0),
                        true);

                x += CHAR_WIDTH;
            }

            EgaUtils.putCharacter(
                    screen,
                    font,
                    cursorChar,
                    x,
                    lineUserInput * CHAR_HEIGHT,
                    WIDTH,
                    translatePixel((byte) 15),
                    translatePixel((byte) 0),
                    true);

            putBlock(
                    0,
                    (lineUserInput * ViewScreen.CHAR_HEIGHT),
                    ViewScreen.WIDTH,
                    ViewScreen.CHAR_HEIGHT);
        }
    }

    public void save() {
        if (screenBackup == null) {
            screenBackup = new int[WIDTH * HEIGHT];
        }

        System.arraycopy(screen, 0, screenBackup, 0, WIDTH * HEIGHT);
    }

    public void restore(boolean update) {
        if (screenBackup != null) {
            System.arraycopy(screenBackup, 0, screen, 0, WIDTH * HEIGHT);

            if (update) {
                putBlock(0, 0, WIDTH, HEIGHT);
            }
        }
    }

    public EgaComponent getComponent() {
        return ega;
    }

    public int[] getScreenData() {
        return screen;
    }

    public int[] getFont() {
        return font;
    }

    public synchronized int translatePixel(byte b) {
        EgaUtils.getNativeColorModel().getDataElements(EgaUtils.getIndexColorModel().getRGB(b), pixel);
        return pixel[0];
    }

    public synchronized int translatePixel(Color color) {
        EgaUtils.getNativeColorModel().getDataElements(color.getRGB(), pixel);
        return pixel[0];
    }

    public void displayLine(int col, int line, String message) {
        int x = col * CHAR_WIDTH;
        int y = line * CHAR_HEIGHT;
        int c = message.length();

        for (int i = 0; i < c; i++) {
            EgaUtils.putCharacter(screen, font, message.charAt(i), x, y, WIDTH, foregroundColor, backgroundColor, true);
            x += CHAR_WIDTH;
        }

        putBlock(0, y, WIDTH, CHAR_HEIGHT);
    }

    public void putBlock(int[] viewTable, int x, int y, int width, int height) {
        int sx, ex;
        int sy, ey;
        int off, offe;

        //y   = y - height + 1;
        ey = y + height;

        if (y < 0) {
            y = 0;
        }

        for (sy = y; sy < ey; sy++) {
            ex = x + width;
            off = (sy * ViewTable.WIDTH) + x;
            offe = ((sy + 8) * WIDTH) + (x * 2);

            for (sx = x; sx < ex; sx++) {
                screen[offe++] = screen[offe++] = viewTable[off++];
            }
        }

        x *= 2;
        y += 8;
        width *= 2;

        screenSource.newPixels(x, y, width, height, true);
        ega.putBlock(x, y, width, height);
    }

    public void putBlock(int x, int y, int width, int height) {
        screenSource.newPixels(x, y, width, height, true);
        ega.putBlock(x, y, width, height);
    }

    public int menuLoop(AgiMenuBar menuBar) {
        KeyEvent ev = null;
        boolean looping = true;
        boolean changed = true;
        int selectedMenu = 0;
        int selectedItem = 0;
        int backgroundColor = translatePixel(Color.white);
        int textColor = translatePixel(Color.black);
        int disabledColor = translatePixel(Color.gray);
        int controller = -1;
        Rectangle changedRectangle = new Rectangle();
        Rectangle previousRectangle = new Rectangle();

        save();

        while (looping) {
            if (changed) {
                changed = false;
                restore(false);

                menuBar.drawMenuBar(
                        this,
                        textColor,
                        backgroundColor,
                        selectedMenu);

                previousRectangle.setBounds(changedRectangle);

                menuBar.drawMenu(
                        this,
                        textColor,
                        disabledColor,
                        backgroundColor,
                        selectedMenu,
                        selectedItem,
                        changedRectangle);

                if (!previousRectangle.equals(changedRectangle)) {
                    putBlock(previousRectangle.x, previousRectangle.y, previousRectangle.width, previousRectangle.height);
                }
            }

            ev = ega.popKeyboardEvent(-1);

            if (ev.getID() == KeyEvent.KEY_PRESSED) {
                switch (ev.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        looping = false;
                        break;

                    case KeyEvent.VK_LEFT:
                        if (selectedMenu > 0) {
                            selectedMenu--;
                            selectedItem = 0;
                            changed = true;
                        }
                        break;

                    case KeyEvent.VK_RIGHT:
                        selectedMenu++;

                        if (selectedMenu >= menuBar.getMenuCount()) {
                            selectedMenu = menuBar.getMenuCount() - 1;
                        } else {
                            changed = true;
                            selectedItem = 0;
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        selectedItem++;

                        if (selectedItem >= menuBar.getItemCount(selectedMenu)) {
                            selectedItem = menuBar.getItemCount(selectedMenu) - 1;
                        } else {
                            changed = true;
                        }
                        break;

                    case KeyEvent.VK_UP:
                        if (selectedItem > 0) {
                            selectedItem--;
                            changed = true;
                        }
                        break;

                    case KeyEvent.VK_ENTER:
                        if (menuBar.isEnabled(selectedMenu, selectedItem)) {
                            controller = menuBar.getController(selectedMenu, selectedItem);
                            looping = false;
                        }
                        break;
                }
            }
        }

        restore(false);
        putBlock(0, 0, WIDTH, CHAR_HEIGHT);
        putBlock(changedRectangle.x, changedRectangle.y, changedRectangle.width, changedRectangle.height);

        return controller;
    }

    public void drawTopLine(int textColor, int backgroundColor, int x, int y, int width) {
        int[] screen = this.screen;

        int offset = (ViewScreen.WIDTH * y) + x;

        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, textColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);

        offset = (ViewScreen.WIDTH * (y + 1)) + x;
        screen[offset] = backgroundColor;
        screen[offset + 1] = backgroundColor;
        screen[offset + width - 1] = backgroundColor;
        screen[offset + width - 2] = backgroundColor;

        for (int i = 2; i < ViewScreen.CHAR_HEIGHT; i++) {
            offset += ViewScreen.WIDTH;
            screen[offset + 2] = textColor;
            screen[offset + 3] = textColor;
            screen[offset + width - 3] = textColor;
            screen[offset + width - 4] = textColor;
        }
    }

    public void drawBottomLine(int textColor, int backgroundColor, int x, int y, int width) {
        int i, offset, w;
        int[] screen = this.screen;

        offset = (ViewScreen.WIDTH * y) + x;

        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, textColor);
        offset += ViewScreen.WIDTH;
        Arrays.fill(screen, offset, offset + width, backgroundColor);
        offset += ViewScreen.WIDTH;

        offset = (ViewScreen.WIDTH * (y + (ViewScreen.CHAR_HEIGHT - 2))) + x;
        screen[offset] = backgroundColor;
        screen[offset + 1] = backgroundColor;
        screen[offset + width - 1] = backgroundColor;
        screen[offset + width - 2] = backgroundColor;

        for (i = 0; i < (ViewScreen.CHAR_HEIGHT - 2); i++) {
            offset -= ViewScreen.WIDTH;
            screen[offset + 2] = textColor;
            screen[offset + 3] = textColor;
            screen[offset + width - 3] = textColor;
            screen[offset + width - 4] = textColor;
        }
    }

    public void drawLeftLine(int textColor, int backgroundColor, int x, int y) {
        int i, offset;
        int[] screen = this.screen;

        offset = (ViewScreen.WIDTH * y) + x;

        for (i = 0; i < ViewScreen.CHAR_HEIGHT; i++) {
            Arrays.fill(screen, offset, offset + ViewScreen.CHAR_WIDTH, backgroundColor);
            screen[offset + 2] = textColor;
            screen[offset + 3] = textColor;
            offset += ViewScreen.WIDTH;
        }
    }

    public void drawBlanks(int color, int x, int y, int w) {
        int[] screen = this.screen;

        if (w <= 0) {
            return;
        }

        int offset = (ViewScreen.WIDTH * y) + x;

        for (int i = 0; i < ViewScreen.CHAR_HEIGHT; i++) {
            Arrays.fill(screen, offset, offset + w, color);
            offset += ViewScreen.WIDTH;
        }
    }

    public void drawRightLine(int textColor, int backgroundColor, int x, int y) {
        int i, offset;
        int[] screen = this.screen;

        offset = (ViewScreen.WIDTH * y) + x;

        for (i = 0; i < ViewScreen.CHAR_HEIGHT; i++) {
            Arrays.fill(screen, offset, offset + ViewScreen.CHAR_WIDTH, backgroundColor);
            screen[offset + 5] = textColor;
            screen[offset + 4] = textColor;
            offset += ViewScreen.WIDTH;
        }
    }

    public void clearLines(int x, int y, short color) {
        for (int l = x; l <= y; l++) {
            drawBlanks(
                    translatePixel((byte) color),
                    0,
                    (l * ViewScreen.CHAR_HEIGHT),
                    ViewScreen.WIDTH);

            putBlock(
                    0,
                    (l * ViewScreen.CHAR_HEIGHT),
                    ViewScreen.WIDTH,
                    ViewScreen.CHAR_HEIGHT);
        }
    }

    public void clearStatusLine(short color) {
        this.clearLines(this.lineStatus, this.lineStatus, color);
    }

    public void displayStatusLine(String message) {
        int x = 0;
        int y = this.lineStatus * CHAR_HEIGHT;

        drawBlanks(
                translatePixel((byte) 0),
                x,
                (this.lineStatus * ViewScreen.CHAR_HEIGHT),
                ViewScreen.WIDTH);

        for (int i = 0; i < message.length(); i++) {
            EgaUtils.putCharacter(screen, font, message.charAt(i), x, y, WIDTH, translatePixel((byte) 0), translatePixel((byte) 15), true);
            x += CHAR_WIDTH;
        }

        putBlock(0, y, WIDTH, CHAR_HEIGHT);
    }

    public void setLineUserInput(int lineUserInput) {
        this.lineUserInput = lineUserInput;
    }

    public void setLineStatus(int lineStatus) {
        this.lineStatus = lineStatus;
    }

    public void setLineMinPrint(int lineMinPrint) {
        this.lineMinPrint = lineMinPrint;
    }

    public short getNum(String message) {
        // clear the line
        setInputLine(null);
        // Show the prompt message to the user at the specified position.
        displayLine(0, lineUserInput, message);

        // Get a line of text from the user.
        StringBuilder line = new StringBuilder();

        ega.clearEvents();

        int col = message.length();
        KeyEvent ev;
        // Process entered keys until either ENTER or ESC is pressed.
        while (true) {
            //setInputLine();
            // Show the currently entered text.
            displayLine(col, lineUserInput, (line.toString() + cursorChar));

            if ((ev = ega.popCharEvent(-1)) == null) {
                break;
            }

            int key = ev.getKeyCode();
            // 0 -9
            if (key >= 48 && key <= 57) {
                // If we haven't reached the max length, add the char to the line of text.
                if (line.length() < 4) {
                    line.append((char) (key & 0xff));
                }
            } else if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_ENTER) {
                break;
            } else if (key == KeyEvent.VK_BACK_SPACE) {
                // Removes one from the end of the currently entered input.
                if (line.length() > 0) {
                    line.deleteCharAt(line.length() - 1);
                }

                // Render Line with a space overwriting the previous position of the cursor.
                displayLine(col, lineUserInput, (line.toString() + cursorChar + " "));
            }
        }

        // Strip out everything that isn't a digit. A little more robust than the original AGI interpreter.
        String digitsInLine = line.toString();

        // clear the line
        setInputLine(null);

        return (digitsInLine.length() > 0 ? Short.parseShort(digitsInLine) : 0);
    }

    public int getLineUserInput() {
        return lineUserInput;
    }

    public char getCursorChar() {
        return cursorChar;
    }

    public int getLineStatus() {
        return lineStatus;
    }

    public int getLineMinPrint() {
        return lineMinPrint;
    }

    public void textMode() {
        save();
        // Clear the whole screen to the background colour.
        clearLines(0, 24, this.backgroundColorByte);
    }
    public void textMode(byte backgroundColorByte) {
        save();
        // Clear the whole screen to the background colour.
        clearLines(0, 24, backgroundColorByte);
    }

    public void graphicMode() {
        // Clear the whole screen to the background colour.
        clearLines(0, 24, (short) 0);
        restore(true);
    }

    public String getStringInput(String message, short row, short col, short length) {
        length = length > LogicVariables.STRING_LENGTH ? LogicVariables.STRING_LENGTH : length;

        displayLine(col, row, message);

        // Position the input area immediately after the message.
        col += message.length();

        // Get a line of text from the user.
        StringBuilder line = new StringBuilder();

        ega.clearEvents();

        KeyEvent ev;
        // Process entered keys until either ENTER or ESC is pressed.
        while (true) {
            // Show the currently entered text.
            displayLine(col, row, (line.toString() + cursorChar));

            if ((ev = ega.popCharEvent(-1)) == null) {
                break;
            }

            int key = ev.getKeyCode();
            if ((key >= 65 && key <= 90) || key == 32) {
                // If we haven't reached the max length, add the char to the line of text.
                if (line.length() < length) {
                    line.append((char) key);
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                return null;
            } else if (key == KeyEvent.VK_ENTER) {
                break;
            } else if (key == KeyEvent.VK_BACK_SPACE) {
                // Removes one from the end of the currently entered input.
                if (line.length() > 0) {
                    line.deleteCharAt(line.length() - 1);
                }

                // Render Line with a space overwriting the previous position of the cursor.
                displayLine(col, row, (line.toString() + cursorChar + " "));
            }
        }
        return line.toString();
    }

    public byte getBackgroundColorByte() {
        return backgroundColorByte;
    }

    public byte getForegroundColorByte() {
        return foregroundColorByte;
    }
}
